package com.animsh.trace.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.animsh.trace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.animsh.trace.util.Constants.fromLogin;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText getUserEmail;
    EditText getPassword;
    ImageView imageLogin;
    TextView txtSignUpPage;
    TextView txtForgetPassword;
    FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // check activity id already opened
        /*SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
        }

        if (restorePrefData()) {
            Intent homePageActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(homePageActivity);
            finish();
        }*/

        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        getUserEmail = findViewById(R.id.login_username);
        getPassword = findViewById(R.id.login_password);
        imageLogin = findViewById(R.id.image_login);
        txtSignUpPage = findViewById(R.id.txt_sign_up_page);
        txtForgetPassword = findViewById(R.id.txt_forgot_password_page);

        firebaseAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        imageLogin.setImageResource(R.drawable.img_sign_in);

        txtSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser() {

        String password = getPassword.getText().toString();
        String email = getUserEmail.getText().toString();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (password.length() < 8) {
                Toast.makeText(this, "Password length should be 8 characters", Toast.LENGTH_SHORT).show();
            }
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            if (getUserEmail.getText().toString().matches(emailPattern)) {
                Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            loadingBar.setTitle("Login You In");
            loadingBar.setMessage("Please wait while we are checking your details");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            allowAccessToAccount(email, password);

        }

    }

    private void allowAccessToAccount(final String email, final String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            savePrefsData();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            fromLogin = true;
                            startActivity(intent);
                            LoginActivity.this.finish();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isUserLogin", true);
        editor.apply();
    }


}