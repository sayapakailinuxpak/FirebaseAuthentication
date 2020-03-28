package com.eldisprojectexpert.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textViewSignUp;
    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    ProgressBar progressBarLogin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initializeUI(){
        editTextEmail = findViewById(R.id.edt_email_login);
        editTextPassword = findViewById(R.id.edt_password);
        buttonLogin = findViewById(R.id.btn_login);
        textViewSignUp = findViewById(R.id.textView_signUp);
        progressBarLogin = findViewById(R.id.progressbar_login);
        textViewSignUp.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_signUp :
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
            case R.id.btn_login :
                final String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    editTextEmail.setError("This field can't be blank");
                    return;
                }else if (TextUtils.isEmpty(password)){
                    editTextPassword.setError("This field can't be blank");
                    return;
                }

                progressBarLogin.setVisibility(View.VISIBLE);

                //authenticate user!
               firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(getApplicationContext(), "Successful Login as " + email, Toast.LENGTH_SHORT).show();
                           new Intent().putExtra("email", email);
                           startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                           editTextEmail.setText("");
                           editTextPassword.setText("");
                           progressBarLogin.setVisibility(View.GONE);
                       } else {
                           Toast.makeText(getApplicationContext(), "There is something wrong " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           progressBarLogin.setVisibility(View.GONE);
                       }
                   }
               });

        }
    }
}
