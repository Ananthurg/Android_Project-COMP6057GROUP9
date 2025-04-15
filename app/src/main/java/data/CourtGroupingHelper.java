package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;
import model.Participant;
import model.Session;

public class CourtGroupingHelper {

    public static class GroupingResult {
        public List<List<Participant>> courts;
        public List<Participant> benched;

        public GroupingResult(List<List<Participant>> courts, List<Participant> benched) {
            this.courts = courts;
            this.benched = benched;
        }
    }

    public static GroupingResult groupForRound(Session session) {
        int maxPerCourt = SportRules.getMaxPlayersPerCourt(session.getSport());
        int totalCourts = SportRules.getCourtCount(session.getSport(), session.getGym());

        RealmList<Participant> originalList = session.getParticipants();

        List<Participant> shuffledList = new ArrayList<>(originalList);
        Collections.shuffle(shuffledList);

        List<List<Participant>> courts = new ArrayList<>();
        List<Participant> benched = new ArrayList<>();

        int playersPerRound = maxPerCourt * totalCourts;

        for (int i = 0; i < shuffledList.size(); i++) {
            if (i < playersPerRound) {
                int courtIndex = i / maxPerCourt;
                if (courts.size() <= courtIndex) {
                    courts.add(new ArrayList<>());
                }
                courts.get(courtIndex).add(shuffledList.get(i));
            } else {
                benched.add(shuffledList.get(i));
            }
        }

        return new GroupingResult(courts, benched);
    }
}
