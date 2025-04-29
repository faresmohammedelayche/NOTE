package com.example.note;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private ImageView backButton;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String noteId;
    private DocumentReference noteRef;

    // Handler and Runnable to auto-save after a delay
    private Handler handler = new Handler();
    private Runnable saveRunnable = new Runnable() {
        @Override
        public void run() {
            saveNote();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note); // Ensure correct layout name

        // Link UI elements
        titleEditText = findViewById(R.id.note_title);
        contentEditText = findViewById(R.id.note_content);
        backButton = findViewById(R.id.back);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        noteId = getIntent().getStringExtra("noteId");

        if (noteId == null || noteId.isEmpty()) {
            Toast.makeText(this, "Note ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        noteRef = firestore.collection("Users").document(userId)
                .collection("notes").document(noteId);

        // Load the note's data
        loadNote();

        // Set up auto-save
        titleEditText.addTextChangedListener(autoSaveWatcher);
        contentEditText.addTextChangedListener(autoSaveWatcher);

        // Back button click listener – save note before exiting
        backButton.setOnClickListener(v -> saveNoteAndFinish());
    }

    private void loadNote() {
        noteRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String title = documentSnapshot.getString("title");
                String content = documentSnapshot.getString("content");
                titleEditText.setText(title);
                contentEditText.setText(content);
            } else {
                Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Save note data to Firestore
    private void saveNote() {
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedContent = contentEditText.getText().toString().trim();

        if (updatedTitle.isEmpty() && updatedContent.isEmpty()) {
            return; // Don't save if the note is empty
        }

        noteRef.update("title", updatedTitle, "content", updatedContent)
                .addOnSuccessListener(unused -> {
                    // Optionally, show a toast message when the note is auto-saved
                    // Toast.makeText(this, "Note auto-saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to auto-save note", Toast.LENGTH_SHORT).show());
    }

    private void saveNoteAndFinish() {
        saveNote();  // Call save before finishing the activity
        finish();    // Close the activity
    }

    // TextWatcher to listen for changes and trigger auto-save
    private final TextWatcher autoSaveWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // Optional: Clear the previous saveRunnable if a new change occurs
            handler.removeCallbacks(saveRunnable);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // Optional: Handle any specific behavior during text changes
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Delay the save to prevent frequent database writes while typing
            handler.removeCallbacks(saveRunnable);
            handler.postDelayed(saveRunnable, 1000);  // Auto-save after 1 second of idle time
        }
    };
}
