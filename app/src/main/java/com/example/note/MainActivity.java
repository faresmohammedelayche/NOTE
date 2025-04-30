package com.example.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView menu = findViewById(R.id.menu);
        FloatingActionButton addNote = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.notes_recyclerview);
        sortSpinner = findViewById(R.id.spinner);


        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        loadNotes("newest");


        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Ranking, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String sortOption = getSortOption(position);
                loadNotes(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

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

    private void loadNotes(String sortOption) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        Query baseQuery = firestore.collection("Users")
                .document(userId)
                .collection("notes")
                .whereEqualTo("archived", false)
                .whereEqualTo("deleted", false);
        Query finalQuery;

        switch (sortOption) {
            case "a_to_z":
                finalQuery = baseQuery.orderBy("title", Query.Direction.ASCENDING);
                break;
            case "z_to_a":
                finalQuery = baseQuery.orderBy("title", Query.Direction.DESCENDING);
                break;
            case "newest":
                finalQuery = baseQuery.orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case "oldest":
                finalQuery = baseQuery.orderBy("timestamp", Query.Direction.ASCENDING);
                break;
            default:
                finalQuery = baseQuery.orderBy("timestamp", Query.Direction.DESCENDING);
                break;
        }

        finalQuery.addSnapshotListener((value, error) -> {
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

    private String getSortOption(int position) {
        switch (position) {
            case 0:
                return "a_to_z";
            case 1:
                return "z_to_a";
            case 2:
                return "newest";
            case 3:
                return "oldest";
            default:
                return "newest";
        }
    }
}
