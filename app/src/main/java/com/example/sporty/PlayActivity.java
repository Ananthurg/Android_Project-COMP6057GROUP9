package com.example.sporty;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import data.CourtGroupingHelper;
import data.SportRules;
import io.realm.Realm;
import model.Participant;
import model.Session;

public class PlayActivity extends AppCompatActivity {

    private Realm realm;
    private int sessionId;
    private Session session;

    private TextView textRound, countdownText, benchText, textHistory;
    private LinearLayout layoutCourts;
    private Button buttonEndSession;

    private CountDownTimer countDownTimer;
    private long roundDurationInMillis;
    private int roundNumber = 1;

    private List<List<Participant>> courtGroups = new ArrayList<>();
    private List<Participant> benchedParticipants = new ArrayList<>();
    private List<RoundRecord> roundHistory = new ArrayList<>();

    private static class RoundRecord {
        int roundNumber;
        List<List<Participant>> courts;
        List<Participant> benched;

        public RoundRecord(int roundNumber, List<List<Participant>> courts, List<Participant> benched) {
            this.roundNumber = roundNumber;
            this.courts = courts;
            this.benched = benched;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Log.d("PlayActivity", "onCreate called");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Realm.init(this);
        realm = Realm.getDefaultInstance();

        textRound = findViewById(R.id.textRound);
        countdownText = findViewById(R.id.textCountdown);
        benchText = findViewById(R.id.textBench);
        textHistory = findViewById(R.id.textHistory);
        layoutCourts = findViewById(R.id.layoutCourts);
        buttonEndSession = findViewById(R.id.buttonEndSession);

        sessionId = getIntent().getIntExtra("session_id", -1);
        session = realm.where(Session.class).equalTo("session_id", sessionId).findFirst();

        if (session == null || session.getParticipants() == null || session.getParticipants().isEmpty()) {
            Log.e("PlayActivity", "❌ No participants found for session ID: " + sessionId);
            textRound.setText("No participants found.");
            return;
        }

        Log.d("PlayActivity", "✅ Loaded session with " + session.getParticipants().size() + " participants");

        int totalMinutes = SportRules.getSessionDurationMinutes(session.getSport());
        roundDurationInMillis = (totalMinutes * 60_000L) / 4;

        regroup();
        startTimer();

        buttonEndSession.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            finish();
        });
    }

    private void regroup() {
        CourtGroupingHelper.GroupingResult result = CourtGroupingHelper.groupForRound(session);
        courtGroups = result.courts;
        benchedParticipants = result.benched;

        roundHistory.add(new RoundRecord(roundNumber, courtGroups, benchedParticipants));

        displayCourts();
        displayBenched();
        displayHistory();
    }

    private void displayCourts() {
        layoutCourts.removeAllViews();

        for (int i = 0; i < courtGroups.size(); i++) {
            List<Participant> court = courtGroups.get(i);

            TextView courtView = new TextView(this);
            StringBuilder sb = new StringBuilder();
            sb.append("🏟️ Court ").append(i + 1).append(":\n");

            for (Participant p : court) {
                sb.append("• ").append(p.getFirst_name()).append(" ").append(p.getLast_name()).append("\n");
            }

            courtView.setText(sb.toString());
            courtView.setTextSize(16);
            courtView.setPadding(20, 20, 20, 20);
            courtView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

            layoutCourts.addView(courtView);
        }

        int totalMinutes = SportRules.getSessionDurationMinutes(session.getSport());
        textRound.setText("🎯 Round " + roundNumber + " of 4 (Session: " + totalMinutes + " mins)");

    }

    private void displayBenched() {
        StringBuilder builder = new StringBuilder();

        if (benchedParticipants.isEmpty()) {
            builder.append("✅ Everyone is playing this round!");
        } else {
            builder.append("🪑 Benched this round:\n");
            for (Participant p : benchedParticipants) {
                builder.append("• ").append(p.getFirst_name()).append(" ").append(p.getLast_name()).append("\n");
            }
        }

        benchText.setText(builder.toString());
    }

    private void displayHistory() {
        StringBuilder builder = new StringBuilder();
        builder.append("📜 Round History:\n\n");

        for (RoundRecord record : roundHistory) {
            builder.append("Round ").append(record.roundNumber).append(":\n");

            for (int i = 0; i < record.courts.size(); i++) {
                builder.append("  Court ").append(i + 1).append(": ");
                for (Participant p : record.courts.get(i)) {
                    builder.append(p.getFirst_name()).append(" ").append(p.getLast_name()).append(", ");
                }
                if (!record.courts.get(i).isEmpty()) {
                    builder.setLength(builder.length() - 2);
                }
                builder.append("\n");
            }

            if (!record.benched.isEmpty()) {
                builder.append("  Benched: ");
                for (Participant p : record.benched) {
                    builder.append(p.getFirst_name()).append(" ").append(p.getLast_name()).append(", ");
                }
                builder.setLength(builder.length() - 2);
                builder.append("\n");
            }

            builder.append("\n");
        }

        textHistory.setText(builder.toString());
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(roundDurationInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;
                countdownText.setText(String.format("⏳ Round %d - Time Left: %02d:%02d", roundNumber, minutes, seconds));
            }

            public void onFinish() {
                roundNumber++;
                regroup();
                startTimer();
            }
        };
        countDownTimer.start();
    }
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        if (realm != null && !realm.isClosed()) realm.close();
    }
}
