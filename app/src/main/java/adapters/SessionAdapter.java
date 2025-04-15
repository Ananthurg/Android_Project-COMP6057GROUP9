package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.Session;
import com.example.sporty.R; // replace with your actual package name
import com.example.sporty.SessionDetailsActivity;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private Context context;
    private List<Session> sessionList;

    public SessionAdapter(Context context, List<Session> sessionList) {
        this.context = context;
        this.sessionList = sessionList;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessionList.get(position);

        holder.sportText.setText(session.getSport());
        holder.gymText.setText("Gym: " + session.getGym());

        String formattedTime = formatDate(session.getStart_time());
        holder.timeText.setText("Starts at: " + formattedTime);

        int count = session.getParticipants() != null ? session.getParticipants().size() : 0;
        holder.participantCount.setText("Participants: " + count);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SessionDetailsActivity.class);
            intent.putExtra("session_id", session.getSession_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView sportText, gymText, timeText, participantCount;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sportText = itemView.findViewById(R.id.textSport);
            gymText = itemView.findViewById(R.id.textGym);
            timeText = itemView.findViewById(R.id.textStartTime);
            participantCount = itemView.findViewById(R.id.textParticipantCount);
        }
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault()).format(new Date(millis));
    }
}
