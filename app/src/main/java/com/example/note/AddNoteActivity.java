package com.example.note;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {

    EditText noteTitle, noteContent;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    DocumentReference noteRef;

    private Handler handler = new Handler();
    private Runnable autoSaveRunnable;
    private static final int AUTO_SAVE_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteTitle = findViewById(R.id.note_title);
        noteContent = findViewById(R.id.note_content);
        ImageView back = findViewById(R.id.back);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();
        noteRef = firestore.collection("Users")
                .document(userId)
                .collection("notes")
                .document();

        autoSaveRunnable = this::saveNote;

        TextWatcher autoSaveWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(autoSaveRunnable);
                handler.postDelayed(autoSaveRunnable, AUTO_SAVE_DELAY);
            }
            @Override public void afterTextChanged(Editable s) { }
        };

        noteTitle.addTextChangedListener(autoSaveWatcher);
        noteContent.addTextChangedListener(autoSaveWatcher);

        back.setOnClickListener(v -> finish());
    }

    private void saveNote() {
        String title = noteTitle.getText().toString().trim();
        String content = noteContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) return;

        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("content", content);
        note.put("timestamp", FieldValue.serverTimestamp());
        note.put("archived", false);
        note.put("deleted", false);

        noteRef.set(note).addOnSuccessListener(unused ->
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show()
        );
    }
}
