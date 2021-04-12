package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import interfaces.IMatch;
import com.example.sc2infoapp.LiquipediaParser;
import com.example.sc2infoapp.MainActivity;

import interfaces.IPredictable;

import interfaces.IPublished;
import models.ExternalMatch;
import models.Match;

import com.example.sc2infoapp.MatchDetailActivity;
import com.example.sc2infoapp.R;
import models.TaskRunner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHolder> {

    ArrayList<IMatch> opponents;
    Context context;

    @Override
    public int getItemViewType(int position) {
        return (opponents.get(position).getMatchType());
    }

    public MatchesAdapter(ArrayList<IMatch> opponents, Context context)
    {
        this.opponents = opponents;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesAdapter.MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == IMatch.EXTERNAL)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
            return new MatchesAdapter.ExternalMatchesViewHolder(view);
        }
        else if(viewType == IMatch.INTERNAL || viewType == IMatch.TEAM)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_internal_match, parent, false);
            return new MatchesAdapter.InternalMatchesViewHolder(view);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.MatchesViewHolder holder, int position) {

        holder.bind(opponents.get(position));
    }

    @Override
    public int getItemCount() {
        return opponents.size();
    }

    class ExternalMatchesViewHolder extends MatchesViewHolder
    {
        TextView tvName;
        TextView tvTime;
        Button btnPredict;

        public ExternalMatchesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnPredict = itemView.findViewById(R.id.btnPredict);
        }

        public void bind(IMatch match)
        {
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent((Activity)context, MatchDetailActivity.class);
                    i.putExtra("match", Parcels.wrap(match));
                    ((Activity)context).startActivity(i);
                }
            });
            tvName.setText(match.getOpponent());
            tvTime.setText(match.getTime());
            if(((ExternalMatch)match).getBo() == -1)
            {
                btnPredict.setVisibility(View.GONE);
            }
            else {
                btnPredict.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TaskRunner taskRunner = new TaskRunner();
                        LiquipediaParser parser = new LiquipediaParser();
                        String[] s = match.getOpponent().split(" vs ");
                        taskRunner.executeAsync(new PredicitonTask(s[0], s[1], ((ExternalMatch)match).getBo()), (data) -> {
                            Log.i("PLAYER", data.toString());
                            try {
                                double prob = data.getDouble("proba");
                                if (prob > 0.5) {
                                    Toast.makeText(context, data.getJSONObject("pla").getString("tag"), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, data.getJSONObject("plb").getString("tag"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
        }
    }

    public class PredicitonTask implements Callable<JSONObject> {
        private final String input1;
        private final String input2;
        private final Integer bo;

        public PredicitonTask(String input1, String input2, Integer bo) {
            this.input1 = input1;
            this.input2 = input2;
            this.bo = bo;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            int p1 = MainActivity.aligulacClient.getPlayer(input1).getInt("id");
            int p2 = MainActivity.aligulacClient.getPlayer(input2).getInt("id");
            return MainActivity.aligulacClient.getPrediction(p1, p2, bo);
        }
    }

    class InternalMatchesViewHolder extends MatchesViewHolder
    {

        TextView tvName;
        TextView tvTime;
        Button btnp1;
        Button btnp2;
        Button btnPredict;
        ProgressBar pbChances;

        public InternalMatchesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnp1 = itemView.findViewById(R.id.btnBet1);
            btnp2 = itemView.findViewById(R.id.btnBet2);
            btnPredict = itemView.findViewById(R.id.btnPredict);
            pbChances = itemView.findViewById(R.id.pbChances);
        }

        public void bind(IMatch match) {
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MatchDetailActivity.class);
                    i.putExtra("match", Parcels.wrap(match));
                    ((Activity)context).startActivity(i);
                }
            });
            tvName.setText(match.getOpponent());
            tvTime.setText(match.getTime());
            IPredictable m = (IPredictable) match;
            if (!(m.getPredicted())) {
                btnp1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m.predict1();
                        pbChances.setProgress(pbChances.getProgress() + 1);
                        pbChances.setMax(pbChances.getMax() + 1);
                        btnp1.setEnabled(false);
                        btnp2.setEnabled(false);
                    }
                });
                btnp2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m.predict2();
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

            Pair<Integer, Integer> chances = m.getDistribution();
            if(chances.first + chances.second == 0)
                pbChances.setIndeterminate(true);
            else
            {
                pbChances.setMax(chances.first + chances.second);
                pbChances.setProgress(chances.first);
            }
        }
    }

    abstract class MatchesViewHolder extends RecyclerView.ViewHolder
    {

        public MatchesViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract public void bind(IMatch match);
    }
}
