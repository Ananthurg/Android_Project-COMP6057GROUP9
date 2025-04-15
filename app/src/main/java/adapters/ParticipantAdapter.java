package adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sporty.R;

import java.util.List;

import io.realm.Realm;
import model.Participant;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private final List<Participant> participants;
    private final Context context;
    private final OnParticipantRemovedListener listener;

    public interface OnParticipantRemovedListener {
        void onParticipantRemoved();
    }

    public ParticipantAdapter(Context context, List<Participant> participants, OnParticipantRemovedListener listener) {
        this.context = context;
        this.participants = participants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        Participant p = participants.get(position);
        holder.nameText.setText(p.getFirst_name() + " " + p.getLast_name());
        holder.phoneText.setText("📞 " + p.getPhone_number());
        holder.memberIdText.setText("ID: " + p.getMember_number());

        holder.dropButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Remove Participant")
                    .setMessage("Are you sure you want to drop " + p.getFirst_name() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(r -> {
                            Participant toDelete = r.where(Participant.class)
                                    .equalTo("member_number", p.getMember_number())
                                    .findFirst();
                            if (toDelete != null) {
                                toDelete.deleteFromRealm();
                            }
                        });
                        realm.close();
                        if (listener != null) {
                            listener.onParticipantRemoved();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, phoneText, memberIdText;
        Button dropButton;

        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textParticipantName);
            phoneText = itemView.findViewById(R.id.textPhone);
            memberIdText = itemView.findViewById(R.id.textMemberId);
            dropButton = itemView.findViewById(R.id.buttonDrop);
        }
    }
}
