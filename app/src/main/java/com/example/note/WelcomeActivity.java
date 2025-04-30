package com.example.note;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button signin = findViewById(R.id.btn_signin);
        signin.setOnClickListener(view -> showBottomSheetsignin());

        Button login = findViewById(R.id.btn_login);
        login.setOnClickListener(view -> showBottomSheetLogin());


    }

    private void showBottomSheetsignin() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        EditText email = bottomSheetDialog.findViewById(R.id.email);
        EditText password = bottomSheetDialog.findViewById(R.id.password);
        CheckBox remember = bottomSheetDialog.findViewById(R.id.remember);
        Button signin = bottomSheetDialog.findViewById(R.id.btnLogin);
        Button goToLogin = bottomSheetDialog.findViewById(R.id.tologin);
        Button forget = bottomSheetDialog.findViewById(R.id.btn_forget);

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);


        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        if (isRemembered) {
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");
            if (email != null) email.setText(savedEmail);
            if (password != null) password.setText(savedPassword);
            if (remember != null) remember.setChecked(true);
        }

        if (signin != null) {
            signin.setOnClickListener(view -> {
                String emailTxt = email.getText().toString().trim();
                String passTxt = password.getText().toString().trim();

                if (emailTxt.isEmpty() || passTxt.isEmpty()) {
                    Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (remember != null && remember.isChecked()) {
                        editor.putString("email", emailTxt);
                        editor.putString("password", passTxt);
                        editor.putBoolean("rememberMe", true);
                    } else {
                        editor.clear();
                    }
                    editor.apply();

                    loginUser(emailTxt, passTxt);
                    bottomSheetDialog.dismiss();
                }
            });
        }

        if (goToLogin != null) {
            goToLogin.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                showBottomSheetLogin();
            });
        }

        if (forget != null){
            forget.setOnClickListener(view -> {
                String emailTxt = email.getText().toString().trim();

                if (emailTxt.isEmpty()) {
                    Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(emailTxt)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            });

        }

        bottomSheetDialog.show();
    }
    private void showBottomSheetLogin() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_login_layout);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        EditText fullname = bottomSheetDialog.findViewById(R.id.fullname);
        EditText emailEdit = bottomSheetDialog.findViewById(R.id.email);
        EditText passwordEdit = bottomSheetDialog.findViewById(R.id.password);
        Button LoginBtn = bottomSheetDialog.findViewById(R.id.btnLogin);
        Button goToSignin = bottomSheetDialog.findViewById(R.id.signin);

        if (LoginBtn != null) {
            LoginBtn.setOnClickListener(view -> {
                String name = fullname.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Complete all information", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, email, password);
                    bottomSheetDialog.dismiss();
                }
            });
        }

        if (goToSignin != null) {
            goToSignin.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                showBottomSheetsignin();
            });
        }

        bottomSheetDialog.show();
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", name);
                    user.put("email", email);

                    db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "You are registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error in saving information", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)

                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
