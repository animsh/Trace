package com.animsh.trace;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.animsh.trace.ui.ForgotPasswordActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.animsh.trace.Constants.restoreUserData;
import static com.animsh.trace.Constants.saveUserData;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile1, profile2, profile3, profile4, profile5, profile6, profile7, profile8, selectedProfile, backButton;
    private EditText profileName;
    private Button saveButton, forgetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // initialize components
        profile1 = findViewById(R.id.profile_1);
        profile2 = findViewById(R.id.profile_2);
        profile3 = findViewById(R.id.profile_3);
        profile4 = findViewById(R.id.profile_4);
        profile5 = findViewById(R.id.profile_5);
        profile6 = findViewById(R.id.profile_6);
        profile7 = findViewById(R.id.profile_7);
        profile8 = findViewById(R.id.profile_8);
        selectedProfile = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        saveButton = findViewById(R.id.save_btn);
        backButton = findViewById(R.id.imageBack);
        forgetButton = findViewById(R.id.forget_btn);

        profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_1);
                selectedProfile.setTag(R.drawable.image_1);
            }
        });

        profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_2);
                selectedProfile.setTag(R.drawable.image_2);
            }
        });

        profile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_3);
                selectedProfile.setTag(R.drawable.image_3);
            }
        });

        profile4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_4);
                selectedProfile.setTag(R.drawable.image_4);
            }
        });

        profile5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_5);
                selectedProfile.setTag(R.drawable.image_5);
            }
        });

        profile6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_6);
                selectedProfile.setTag(R.drawable.image_6);
            }
        });

        profile7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_7);
                selectedProfile.setTag(R.drawable.image_7);
            }
        });

        profile8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedProfile.setImageResource(R.drawable.image_8);
                selectedProfile.setTag(R.drawable.image_8);
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        String[] userData = restoreUserData(ProfileActivity.this);
        profileName.setText(userData[0]);
        selectedProfile.setImageResource(Integer.parseInt(userData[4]));
        selectedProfile.setTag(Integer.parseInt(userData[4]));

        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) ProfileActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (profileName.length() != 0) {
                    if (isConnected) {
                        dialog.show();
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid());
                        UserModel upload = new UserModel(profileName.getText().toString(), userData[1], userData[2], userData[3], (int) selectedProfile.getTag());
                        /*String key = root.push().getKey();*/
                        root.child("USER_DATA").child(userData[3]).setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                saveUserData(ProfileActivity.this, profileName.getText().toString(), userData[1], userData[2], userData[3], (int) selectedProfile.getTag());
                                dialog.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}