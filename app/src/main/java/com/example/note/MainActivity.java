package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.sidesheet.SideSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // عناصر الواجهة
        ImageView menu = findViewById(R.id.menu);
        FloatingActionButton addNote = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.notes_recyclerview);

        // RecyclerView
        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // تحميل الملاحظات
        loadNotes();

        // الأحداث
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
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.category).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, CategoryActivity.class));
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.archives).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ArchiveActivity.class));
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.recycle).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RecycleActivity.class));
            sideSheetDialog.dismiss();
        });

        sideSheetDialog.findViewById(R.id.logout).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LogoutActivity.class));
            sideSheetDialog.dismiss();
        });
    }

    private void loadNotes() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("Users")
                .document(userId)
                .collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    noteList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Note note = doc.toObject(Note.class);
                        noteList.add(note);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
