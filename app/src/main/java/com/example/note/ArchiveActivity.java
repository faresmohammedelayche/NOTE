package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive);
        ImageView back = findViewById(R.id.back);

        recyclerView = findViewById(R.id.notes_recyclerview);

        // Set up RecyclerView
        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Firebase Setup
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        back.setOnClickListener(view -> goBack());

        loadNotes();
    }

    private void loadNotes() {
        if (firebaseAuth.getCurrentUser() == null) return;

        String userId = firebaseAuth.getCurrentUser().getUid();
        Query query = firestore.collection("Users")
                .document(userId)
                .collection("notes")
                .whereEqualTo("archived", true)
                .orderBy("timestamp", Query.Direction.DESCENDING); // ترتيب حسب التاريخ من الأحدث

        query.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            noteList.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                Note note = doc.toObject(Note.class);
                note.setId(doc.getId());
                noteList.add(note);
            }
            adapter.notifyDataSetChanged();
        });
    }


    private void goBack() {
        Intent intent = new Intent(ArchiveActivity.this,MainActivity.class);
        startActivity(intent);
    }
}