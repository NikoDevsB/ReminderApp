package com.example.reminderapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.reminderapp.DataModel.Note;
import com.example.reminderapp.DataModel.NoteDAO;
import com.example.reminderapp.DataModel.NoteDatabase;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AddReminderDialog extends AppCompatDialogFragment {
    private ReminderAppListener addReminderDialogListener;
    private EditText editTextUserInput;
    private Button saveNote;
    private Button cancel;
    private double longt;
    private double latit;
    private int autoId=0;

    Locale locale;
    NoteDAO noteDAO;
    TextView locationSelector;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String ACCESS_FINE_LOCATION = "1";
    int LOCATION_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;


    @Database(entities = {Note.class}, version = 1)
    public static abstract class AppDataBase extends RoomDatabase {
        public abstract NoteDAO noteDAO();

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        noteDAO = NoteDatabase.getDBInstance(getContext().getApplicationContext()).noteDAO();
        View view = inflater.inflate(R.layout.layout_dialog, null);


        builder.setView(view);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext().getApplicationContext());
        editTextUserInput = view.findViewById(R.id.reminder_text);
        saveNote = view.findViewById(R.id.save_reminder_dialog);
        locationSelector = view.findViewById(R.id.select_loca);
        cancel = view.findViewById(R.id.cancel_reminder);
        AutoCompleteTextView selectItem = view.findViewById(R.id.select_item);

        //Hard-Coded list -> in real life must be called withing location API (if exists- for example : physical store)
        ArrayList arrayList = new ArrayList<>();
        arrayList.add("Chicken");
        arrayList.add("Bread");
        arrayList.add("Fish");
        arrayList.add("Milk");
        arrayList.add("Eggs");
        arrayList.add("Sugar");
        arrayList.add("Salt");
        arrayList.add("Toilet Paper");
        arrayList.add("Oranges");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext().getApplicationContext(), com.facebook.common.R.layout.support_simple_spinner_dropdown_item, arrayList);
        selectItem.setAdapter(arrayAdapter);
        selectItem.setThreshold(1); //start autocomplete at first letter


        //If we need to make any operation in before/during or after the user types in the box
        selectItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged", String.valueOf(s));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged", String.valueOf(s));

            }
        });


        Dialog dialog = builder.create();


        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ;

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        locationSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLocationDialog();

            }
        });
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DEBUG
                String userInput = editTextUserInput.getText().toString();
                Reminder rt = new Reminder(String.valueOf(autoId=autoId+1), userInput, selectItem.getText().toString(), longt, latit);
                addReminderDialogListener.applyTexts(rt);
                //END DEBUG
//                String newReminder = editTextUserInput.getText().toString();
//                Note nt = new Note(58,newReminder,"NEW REMINDER","ITEM NEW");
//                addReminderDialogListener.applyTexts(nt);
//
//                noteDAO.insertOne(nt);
                dialog.dismiss();

            }
        });

        return dialog;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addReminderDialogListener = (ReminderAppListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement...");
        }

    }


    public void openLocationDialog() {
        if (!Places.isInitialized()) {
            Places.initialize(getContext().getApplicationContext(), "AIzaSyC2YGUWKDjmInzq1g3TIu6V291ArqbSZ2w", Locale.getDefault());

        }
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);


        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            locationSelector.setText(Autocomplete.getPlaceFromIntent(data).getName());
            LatLng location = Autocomplete.getPlaceFromIntent(data).getLatLng();
            updateLocation(location); ///send the locations to local parameters
            return;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    //update the local longitude and latitude to pass later to Reminder constructor
    private void updateLocation(LatLng address) {
        longt = address.longitude;
        latit = address.latitude;

    }

    public interface ReminderAppListener {
        void applyTexts(Reminder reminder);
    }


}
