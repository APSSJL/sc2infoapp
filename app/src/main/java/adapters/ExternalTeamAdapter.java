package adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.R;

import java.util.ArrayList;

public class ExternalTeamAdapter extends RecyclerView.Adapter<ExternalTeamAdapter.ExternalTeamViewHolder> {

    ArrayList<Pair<String, String>> players;
    Context context;

    public ExternalTeamAdapter(ArrayList<Pair<String, String>> players, Context context)
    {
        this.players = players;
        this.context = context;
    }

    @NonNull
    @Override
    public ExternalTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_user, parent, false);
        return new ExternalTeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExternalTeamViewHolder holder, int position) {
        holder.bind(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class ExternalTeamViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvName;
        TextView tvRace;

        public ExternalTeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRace = itemView.findViewById(R.id.tvRace);
        }

        public void bind(Pair<String, String> player)
        {
            tvName.setText(player.first);
            tvRace.setText(player.second);
        }
    }
}

