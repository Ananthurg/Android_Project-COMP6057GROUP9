package com.example.sporty;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import model.Participant;
import model.Session;

public class AddParticipantActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, phoneInput, memberIdInput;
    private Button saveBtn;
    private Realm realm;
    private int sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sessionId = getIntent().getIntExtra("session_id", -1);
        realm = Realm.getDefaultInstance();

        firstNameInput = findViewById(R.id.editFirstName);
        lastNameInput = findViewById(R.id.editLastName);
        phoneInput = findViewById(R.id.editPhone);
        memberIdInput = findViewById(R.id.editMemberId);
        saveBtn = findViewById(R.id.buttonSaveParticipant);

        saveBtn.setOnClickListener(v -> {
            String first = firstNameInput.getText().toString().trim();
            String last = lastNameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String memberStr = memberIdInput.getText().toString().trim();

            if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(memberStr)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int memberNumber = Integer.parseInt(memberStr);

            realm.executeTransaction(r -> {
                Participant participant = r.createObject(Participant.class, memberNumber);
                participant.setFirst_name(first);
                participant.setLast_name(last);
                participant.setPhone_number(phone);
                participant.setSession_id(sessionId);
                Session session = r.where(Session.class).equalTo("session_id", sessionId).findFirst();

                if (session != null) {
                    // Add the newly created participant to the session's participant list
                    session.getParticipants().add(participant);
                }

            });

            Toast.makeText(this, "Participant added", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); //
            finish();

        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
