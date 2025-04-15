package model;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Participant extends RealmObject {
    @PrimaryKey
    private int member_number;
    private String first_name;
    private String last_name;
    private String phone_number;
    private int session_id;

    // Getters and Setters
    public int getMember_number() { return member_number; }
    public void setMember_number(int member_number) { this.member_number = member_number; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }

    public int getSession_id() { return session_id; }
    public void setSession_id(int session_id) { this.session_id = session_id; }
}

