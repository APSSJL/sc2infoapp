package adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.R;
import com.parse.ParseUser;

import java.util.ArrayList;

import models.Match;
import models.TeamMatch;

public class TeamMatchAdapter  extends RecyclerView.Adapter<TeamMatchAdapter.TeamMatchViewHolder> {

    Context context;
    ArrayList<TeamMatch> matches;

    public TeamMatchAdapter(Context context, ArrayList<TeamMatch> matches)
    {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public TeamMatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_internal_match, parent, false);
        return new TeamMatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMatchViewHolder holder, int position) {
        holder.bind(matches.get(position));
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class TeamMatchViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvName;
        TextView tvTime;
        Button btnp1;
        Button btnp2;
        Button btnPredict;
        ProgressBar pbChances;

        public TeamMatchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnp1 = itemView.findViewById(R.id.btnBet1);
            btnp2 = itemView.findViewById(R.id.btnBet2);
            btnPredict = itemView.findViewById(R.id.btnPredict);
            pbChances = itemView.findViewById(R.id.pbChances);
        }

        public void bind(TeamMatch tm)
        {
            tvName.setText(tm.getOpponent());
            tvTime.setText(tm.getTime());

            if (!(tm.getPredicted())) {
                btnp1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tm.add("predicted", ParseUser.getCurrentUser().getObjectId());
                        tm.increment("t1PredictionVotes");
                        tm.saveInBackground();
                        pbChances.setProgress(pbChances.getProgress() + 1);
                        pbChances.setMax(pbChances.getMax() + 1);
                        btnp1.setEnabled(false);
                        btnp2.setEnabled(false);
                    }
                });
                btnp2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tm.add("predicted", ParseUser.getCurrentUser().getObjectId());
                        tm.increment("t2PredictionVotes");
                        tm.saveInBackground();
                        pbChances.setMax(pbChances.getMax() + 1);
                        btnp1.setEnabled(false);
                        btnp2.setEnabled(false);
                    }
                });
            }
            else
            {
                btnp1.setEnabled(false);
                btnp2.setEnabled(false);
            }

            Pair<Integer, Integer> chances = (tm).getDistribution();
            if(chances.first + chances.second == 0)
                pbChances.setIndeterminate(true);
            else
            {
                pbChances.setMax(chances.first + chances.second);
                pbChances.setProgress(chances.first);
            }
        }
    }
}
