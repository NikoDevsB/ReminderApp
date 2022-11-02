package com.example.reminderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText fName,mail,phn,psw;
    private DatabaseReference mDataBase;
    String USER_KEY="User";
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        init();
        mAuth = FirebaseAuth.getInstance();


    }

    public void init(){
        fName = findViewById(R.id.full_name);
        mail = findViewById(R.id.email);
        phn= findViewById(R.id.phone);
        psw = findViewById(R.id.password);
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY);
    }

    //signUp
    //signUser
    public void signUser(View view){
        String id = mDataBase.getKey();
        String name = fName.getText().toString();
        String email = mail.getText().toString();
        String phone = phn.getText().toString();
        String password =psw.getText().toString();
        User  newUser = new User(id,name,email,phone,password);
        mDataBase.push().setValue(newUser);
        Toast.makeText(this, "User Successfully Created", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Register.this,MainActivity.class));
    }

    public void signUp(View view){

        String name = fName.getText().toString();
        String email = mail.getText().toString();
        String phone = phn.getText().toString();
        String password =psw.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        Intent intent = new Intent(Register.this,MainActivity.class);
        startActivity(intent);

    }
}