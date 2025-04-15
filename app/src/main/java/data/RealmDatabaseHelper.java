package data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;

import io.realm.Realm;
import io.realm.RealmList;
import model.Participant;
import model.Session;

public class RealmDatabaseHelper {

    private final Realm realm;

    public RealmDatabaseHelper() {
        realm = Realm.getDefaultInstance();
    }

    public void importSessionsFromJson(Context context, int rawResourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(rawResourceId);
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            JsonArray sessionsArray = jsonObject.getAsJsonArray("Sessions");

            if (sessionsArray == null || sessionsArray.size() == 0) {
                Log.e("RealmDB", "No sessions found in JSON");
                return;
            }

            realm.executeTransaction(r -> {
                for (JsonElement sessionElement : sessionsArray) {
                    JsonObject sessionObject = sessionElement.getAsJsonObject();

                    // Create or update session
                    Session session = new Session();
                    session.setSession_id(sessionObject.get("session_id").getAsInt());
                    session.setSport(sessionObject.get("sport").getAsString());
                    session.setGym(sessionObject.get("gym").getAsString());
                    session.setStart_time(sessionObject.get("start_time").getAsLong());

                    Log.d("RealmDB", "Inserting session ID: " + session.getSession_id());

                    RealmList<Participant> participants = new RealmList<>();
                    JsonArray participantsArray = sessionObject.getAsJsonArray("participants");

                    for (JsonElement p : participantsArray) {
                        JsonObject participantJson = p.getAsJsonObject();

                        int memberNumber = participantJson.get("member_number").getAsInt();
                        String firstName = participantJson.get("first_name").getAsString();
                        String lastName = participantJson.get("last_name").getAsString();
                        String phone = participantJson.get("phone_number").getAsString();
                        int sessionId = participantJson.get("session_id").getAsInt();

                        // ✅ Check for existing participant
                        Participant participant = r.where(Participant.class)
                                .equalTo("member_number", memberNumber)
                                .findFirst();

                        if (participant == null) {
                            participant = r.createObject(Participant.class, memberNumber);
                            participant.setFirst_name(firstName);
                            participant.setLast_name(lastName);
                            participant.setPhone_number(phone);
                            participant.setSession_id(sessionId);
                            Log.d("RealmDB", "Added NEW participant: " + firstName + " " + lastName);
                        } else {
                            Log.w("RealmDB", "Duplicate member_number: " + memberNumber + " already exists, adding to session.");
                        }

                        // ✅ Always add to this session's list
                        participants.add(participant);
                    }

                    session.setParticipants(participants);
                    r.insertOrUpdate(session);
                }
            });

            Log.d("RealmDB", "Sessions imported successfully");

        } catch (Exception e) {
            Log.e("RealmDB", "Error importing sessions", e);
        }
    }
}
