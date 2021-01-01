package com.animsh.trace.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.animsh.trace.R;
import com.animsh.trace.model.UserModel;
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

    public static void saveUserData(Context context, String uName, String uEmail, String uPhone, String uKey, int tag) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UNAME", uName);
        editor.putString("UEMAIL", uEmail);
        editor.putString("UPHONE", uPhone);
        editor.putString("UKEY", uKey);
        editor.putInt("TAG", tag);
        editor.apply();
    }

    public static String[] restoreUserData(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        String[] userData = new String[5];
        userData[0] = pref.getString("UNAME", "");
        userData[1] = pref.getString("UEMAIL", "");
        userData[2] = pref.getString("UPHONE", "");
        userData[3] = pref.getString("UKEY", "");
        userData[4] = String.valueOf(pref.getInt("TAG", R.drawable.image_1));
        Log.e("RESTORE: ", pref.getString("UNAME", ""));
        return userData;
    }

    public static void fetchUserdata(FirebaseAuth mAuth, Context context, Dialog dialog, TextView uName, TextView uPhone, ImageView uImage) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid()).child("USER_DATA");
        final UserModel[] model = {null};
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    model[0] = dataSnapshot.getValue(UserModel.class);
                    Log.e("USER DATA: ", "onDataChange: " + model[0].getuName() + model[0].getuEmail() + model[0].getuPhone() + model[0].getuTag());
                    uName.setText(model[0].getuName());
                    uPhone.setText(model[0].getuEmail());
                    uImage.setImageResource(model[0].getuTag());
                    saveUserData(context, model[0].getuName(), model[0].getuEmail(), model[0].getuPhone(), model[0].getUkey(), model[0].getuTag());
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
