package com.eldisprojectexpert.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textViewLogin;
    EditText editTextEmail, editTextUsername, editTextPassword;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    Button buttonSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeUI();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        //jika ada yang sudah sign up maka ke home activity
        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void initializeUI(){
        editTextEmail = findViewById(R.id.edt_email);
        editTextUsername = findViewById(R.id.edt_username_register);
        editTextPassword = findViewById(R.id.edt_password_register);
        progressBar = findViewById(R.id.progress_bar);
        textViewLogin = findViewById(R.id.textview_login);
        buttonSignup = findViewById(R.id.button_signup);
        textViewLogin.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_login :
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.button_signup :
                final String email = editTextEmail.getText().toString().trim();
                final String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    editTextEmail.setError("This field cannot be blank!");
                    return;
                }
                if (TextUtils.isEmpty(username)){
                    editTextUsername.setError("This field cannot be blank!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    editTextPassword.setError("This field cannot be blank!");
                    return;
                }

                if (password.length() < 8){
                    editTextPassword.setError("Min. 8 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register user to firebase!
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //verify email
                            verifyEmail();

                            Toast.makeText(RegisterActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            //Storing data in collection Firestore
                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username); //key will act as Document in FireStore
                            userMap.put("email", email);
                            documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RegisterActivity", "onSuccess: user profile created for " + userId);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            editTextEmail.setText("");
                            editTextUsername.setText("");
                            editTextPassword.setText("");
                            progressBar.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(RegisterActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    //verify email
    private void verifyEmail(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(RegisterActivity.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("RegisterActivity", "onFailure " + e.getMessage());
                }
            });
        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
