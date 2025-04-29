package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
         ImageView back = findViewById(R.id.back);
         back.setOnClickListener(view -> GoBack());
    }

    private void GoBack() {
        Intent intent = new Intent(CategoryActivity.this,MainActivity.class);
        startActivity(intent);
    }
}