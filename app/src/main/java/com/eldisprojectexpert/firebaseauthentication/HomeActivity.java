package com.eldisprojectexpert.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomeActivity extends AppCompatActivity {
    Button buttonLogout, buttonVerifyNow;
    TextView textViewUsername, textViewEmail, textViewVerifyStatus;
    ImageView imageViewUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        buttonLogout = findViewById(R.id.btn_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        textViewEmail = findViewById(R.id.textview_email);
        textViewUsername = findViewById(R.id.textview_username);
        textViewVerifyStatus = findViewById(R.id.textview_verified_status);
        buttonVerifyNow = findViewById(R.id.button_verify_now);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fetchDataFromFireStore();

        //Check if email is verified or not
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (!firebaseUser.isEmailVerified()) {
            textViewVerifyStatus.setVisibility(View.VISIBLE);
            buttonVerifyNow.setVisibility(View.VISIBLE);

            buttonVerifyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(HomeActivity.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MainActivity", "onFailure " + e.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    private void fetchDataFromFireStore(){
        userId = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                textViewUsername.setText(documentSnapshot.getString("username"));
                textViewEmail.setText(documentSnapshot.getString("email"));
            }
        });
    }
}
