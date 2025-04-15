package com.example.sporty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapters.ParticipantAdapter;
import io.realm.Realm;
import model.Session;
import data.SportRules;


public class SessionDetailsActivity extends AppCompatActivity {

    private Realm realm;
    private Session session;
    private int sessionId;

    private TextView sportText, gymText, startTimeText, participantCount;
    private RecyclerView recyclerView;
    private Button addParticipantButton, playNowButton;

    private ParticipantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Realm.init(this);
        realm = Realm.getDefaultInstance();


        sportText = findViewById(R.id.textSport);
        gymText = findViewById(R.id.textGym);
        startTimeText = findViewById(R.id.textStartTime);
        participantCount = findViewById(R.id.textParticipantCount);
        recyclerView = findViewById(R.id.recyclerParticipants);
        addParticipantButton = findViewById(R.id.buttonAddParticipant);
        playNowButton = findViewById(R.id.buttonPlay);


        sessionId = getIntent().getIntExtra("session_id", -1);
        session = realm.where(Session.class).equalTo("session_id", sessionId).findFirst();

        if (session != null) {
            sportText.setText("🏀 Sport: " + session.getSport());
            gymText.setText("🏋️ Gym: " + session.getGym());

            // 🕒 Format and set start time
            long timestampSeconds = session.getStart_time();
            Date date = new Date(timestampSeconds * 1000L);
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            String formattedTime = format.format(date);
            startTimeText.setText("🕒 Start Time: " + formattedTime);

            participantCount.setText("Participants: " + session.getParticipants().size());

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ParticipantAdapter(this, session.getParticipants(), () -> {
                adapter.notifyDataSetChanged();
                participantCount.setText("Participants: " + session.getParticipants().size());
            });
            recyclerView.setAdapter(adapter);
        }

        addParticipantButton.setOnClickListener(v -> {
            Intent intent = new Intent(SessionDetailsActivity.this, AddParticipantActivity.class);
            intent.putExtra("session_id", sessionId);
            startActivity(intent);
        });

        playNowButton.setOnClickListener(v -> {
            Log.d("SessionDetails", "Play Now clicked with sessionId: " + sessionId);
            Intent intent = new Intent(SessionDetailsActivity.this, PlayActivity.class);
            intent.putExtra("session_id", sessionId);
            startActivity(intent);
        });

        TextView rulesText = findViewById(R.id.textSportRules);
        String sport = session.getSport();
        String rules = SportRules.getRulesForSport(sport);
        rulesText.setText(rules);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        session = realm.where(Session.class).equalTo("session_id", sessionId).findFirst();
        if (session != null && session.getParticipants() != null) {
            adapter = new ParticipantAdapter(this, session.getParticipants(), () -> {
                adapter.notifyDataSetChanged();
                participantCount.setText("Participants: " + session.getParticipants().size());
            });
            recyclerView.setAdapter(adapter);
            participantCount.setText("Participants: " + session.getParticipants().size());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
