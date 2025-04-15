package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button signin = findViewById(R.id.btn_signin);
        signin.setOnClickListener(view -> showbottomsheetlayout());

        Button login = findViewById(R.id.btn_login);
        login.setOnClickListener(view -> showbottomsheetloginlayout());
    }

    // دالة عرض BottomSheet للتسجيل
    private void showbottomsheetloginlayout() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_login_layout);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        // مكونات واجهة المستخدم
        EditText fullname = bottomSheetDialog.findViewById(R.id.fullname);
        EditText emailEdit = bottomSheetDialog.findViewById(R.id.email);
        EditText passwordEdit = bottomSheetDialog.findViewById(R.id.password);
        Button signinBtn = bottomSheetDialog.findViewById(R.id.btnlogin);
        Button login2 = bottomSheetDialog.findViewById(R.id.login);

        // زر تسجيل المستخدمين الجدد
        if (signinBtn != null) {
            signinBtn.setOnClickListener(view -> {
                String name = fullname.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Complete all information", Toast.LENGTH_SHORT).show();
                } else {
                    // استدعاء دالة تسجيل المستخدم
                    registerUser(name, email, password);
                    bottomSheetDialog.dismiss();
                }
            });
        }

        // زر التحويل بين التسجيل والدخول
        if (login2 != null) {
            login2.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                showbottomsheetloginlayout();
            });
        }

        bottomSheetDialog.show();
    }

    // دالة تسجيل المستخدم
    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", name);
                    user.put("email", email);

                    db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error in save information", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // دالة تسجيل الدخول
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class)); // التوجيه إلى الشاشة الرئيسية
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // دالة عرض BottomSheet لتسجيل الدخول
    private void showbottomsheetlayout() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        EditText email = bottomSheetDialog.findViewById(R.id.email);
        EditText password = bottomSheetDialog.findViewById(R.id.password);
        Button loginBtn = bottomSheetDialog.findViewById(R.id.btnlogin);
        Button signin2 = bottomSheetDialog.findViewById(R.id.signin);

        if (loginBtn != null) {
            loginBtn.setOnClickListener(view -> {
                String emailTxt = email.getText().toString().trim();
                String passTxt = password.getText().toString().trim();

                if (emailTxt.isEmpty() || passTxt.isEmpty()) {
                    Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(emailTxt, passTxt);
                    bottomSheetDialog.dismiss();
                }
            });
        }

        if (signin2 != null) {
            signin2.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                showbottomsheetlayout();
            });
        }

        bottomSheetDialog.show();
    }
}
