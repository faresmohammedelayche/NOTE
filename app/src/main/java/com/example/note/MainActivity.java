package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.sidesheet.SideSheetDialog;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView menu = findViewById(R.id.menu);
        FloatingActionButton addNote = findViewById(R.id.add_note_button);

        menu.setOnClickListener(view -> showSideSheet());
        addNote.setOnClickListener(view -> goToAdd());
    }

    private void goToAdd() {
        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void showSideSheet() {
        SideSheetDialog sideSheetDialog = new SideSheetDialog(this);
        sideSheetDialog.setContentView(R.layout.side_sheet_dialog);
        sideSheetDialog.setCanceledOnTouchOutside(true);
        sideSheetDialog.setSheetEdge(Gravity.START);
        sideSheetDialog.show();

        sideSheetDialog.findViewById(R.id.setting).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.category).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
            startActivity(intent);
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.archives).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ArchiveActivity.class);
            startActivity(intent);
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.recycle).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RecycleActivity.class);
            startActivity(intent);
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.logout).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
            startActivity(intent);
            sideSheetDialog.dismiss();
        });
    }
}
