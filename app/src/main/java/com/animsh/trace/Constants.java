package com.animsh.trace;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class Constants {

    public static String userName;
    public static String userPhoneNumber;

    public static String userEmail;
    public static String userPassword;
    public static boolean fromLogin = false;

    public static void saveUserData(Context context, String uName, String uEmail, String uPhone, String uPassword) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UNAME", uName);
        editor.putString("UEMAIL", uEmail);
        editor.putString("UPHONE", uPhone);
        editor.putString("UPASSWORD", uPassword);
        editor.apply();
    }

    public static String[] restoreUserData(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        String[] userData = new String[4];
        userData[0] = pref.getString("UNAME", "");
        userData[1] = pref.getString("UEMAIL", "");
        userData[2] = pref.getString("UPHONE", "");
        userData[3] = pref.getString("UPASSWORD", "");
        Log.e("RESTORE: ", pref.getString("UNAME", ""));
        return userData;
    }

    public static void fetchUserdata(FirebaseAuth mAuth, Context context, Dialog dialog, TextView uName, TextView uPhone) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid()).child("USER_DATA");
        final UserModel[] model = {null};
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    model[0] = dataSnapshot.getValue(UserModel.class);
                    Log.e("USER DATA: ", "onDataChange: " + model[0].getuName() + model[0].getuEmail() + model[0].getuPhone());
                    uName.setText(model[0].getuName());
                    uPhone.setText(model[0].getuEmail());
                    saveUserData(context, model[0].getuName(), model[0].getuEmail(), model[0].getuPhone(), "0000");
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
