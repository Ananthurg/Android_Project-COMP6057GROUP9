package data;

public class SportRules {

    public static int getCourtCount(String sport, String gym) {
        sport = sport.toLowerCase();
        gym = gym.toLowerCase();

        switch (sport) {
            case "basketball":
                return gym.equals("gym 1") ? 2 : 0;
            case "badminton":
                if (gym.equals("gym 1")) return 6;
                if (gym.equals("gym 2")) return 3;
                return 0;
            case "volleyball":
                return gym.equals("gym 1") ? 3 : 0;
            case "pickleball":
                return gym.equals("gym 2") ? 3 : 0;
            case "dodgeball":
                return gym.equals("gym 3") ? 1 : 0;
            default:
                return 0;
        }
    }

    public static int getMaxPlayersPerCourt(String sport) {
        switch (sport.toLowerCase()) {
            case "basketball":
            case "dodgeball":
                return 20;
            case "volleyball":
                return 12;
            case "badminton":
            case "pickleball":
                return 4;
            default:
                return 0;
        }
    }

    public static int getSessionDurationMinutes(String sport) {
        switch (sport.toLowerCase()) {
            case "basketball":
            case "volleyball":
                return 120;
            case "badminton":
            case "pickleball":
            case "dodgeball":
                return 90;
            default:
                return 0;
        }
    }

    public static String getRulesForSport(String sport) {
        switch (sport.toLowerCase()) {
            case "basketball":
                return "🏀 Basketball\n• Gym 1 only\n• 2 games at once\n• 20 players per court\n• 2-hour session";
            case "badminton":
                return "🏸 Badminton\n• Gym 1: 6 courts\n• Gym 2: 3 courts\n• 4 players per court\n• 90-minute session";
            case "volleyball":
                return "🏐 Volleyball\n• Gym 1 only\n• 3 courts\n• 12 players per court\n• 2-hour session";
            case "pickleball":
                return "🏓 Pickleball\n• Gym 2 only\n• 3 courts\n• 4 players per court\n• 90-minute session";
            case "dodgeball":
                return "🤾 Dodgeball\n• Gym 3 only\n• 1 court\n• 20 players\n• 90-minute session";
            default:
                return "No rules available for this sport.";
        }
    }
}
