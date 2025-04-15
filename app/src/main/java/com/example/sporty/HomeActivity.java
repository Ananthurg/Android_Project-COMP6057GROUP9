package com.example.sporty;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import adapters.SessionAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Session;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private Realm realm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Realm setup
        realm = Realm.getDefaultInstance();

        // Initialize UI
        recyclerView = findViewById(R.id.recyclerSessions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load sessions
        loadSessions();
    }

    private void loadSessions() {
        RealmResults<Session> results = realm.where(Session.class).findAll();
        Log.d("REALM_CHECK", "Session count: " + results.size());

        adapter = new SessionAdapter(this, results);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadSessions();
    }

}

