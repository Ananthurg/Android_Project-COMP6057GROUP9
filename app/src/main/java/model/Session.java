package model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Session extends RealmObject {
    @PrimaryKey
    private int session_id;
    private String sport;
    private String gym;
    private long start_time;
    private RealmList<Participant> participants;

    // Getters and Setters
    public int getSession_id() { return session_id; }
    public void setSession_id(int session_id) { this.session_id = session_id; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getGym() { return gym; }
    public void setGym(String gym) { this.gym = gym; }

    public long getStart_time() { return start_time; }
    public void setStart_time(long start_time) { this.start_time = start_time; }

    public RealmList<Participant> getParticipants() { return participants; }
    public void setParticipants(RealmList<Participant> participants) { this.participants = participants; }
}
