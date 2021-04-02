package com.example.sc2infoapp;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
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
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_internal_match, parent, false);
            return new MatchesAdapter.InternalMatchesViewHolder(view);
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
            tvName.setText(match.getOpponent());
            tvTime.setText(match.getTime());
            btnPredict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskRunner taskRunner = new TaskRunner();
                    LiquipediaParser parser = new LiquipediaParser();
                    String[] s = match.getOpponent().split(" vs ");
                    taskRunner.executeAsync(new PredicitonTask(s[0],s[1]), (data) -> {
                        Log.i("PLAYER", data.toString());
                        try {
                            double prob = 0;
                            JSONArray d = data.getJSONArray("outcomes");
                            for(int i = 0 ; i < d.length() / 2; i++)
                                prob += d.getJSONObject(i).getDouble("prob");
                            Log.i("PROB", String.valueOf(prob));
                            if(prob > 0.5)
                            {
                                Toast.makeText(context, data.getJSONObject("pla").getString("tag"), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
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

    class PredicitonTask implements Callable<JSONObject> {
        private final String input1;
        private final String input2;

        public PredicitonTask(String input1, String input2) {
            this.input1 = input1;
            this.input2 = input2;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            int p1 = MainActivity.aligulacClient.getPlayer(input1).getInt("id");
            int p2 = MainActivity.aligulacClient.getPlayer(input2).getInt("id");
            return MainActivity.aligulacClient.getPrediction(p1, p2, 3);
        }
    }

    class InternalMatchesViewHolder extends MatchesViewHolder
    {

        TextView tvName;
        TextView tvTime;
        Button btnp1;
        Button btnp2;
        Button btnPredict;

        public InternalMatchesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnp1 = itemView.findViewById(R.id.btnBet1);
            btnp2 = itemView.findViewById(R.id.btnBet2);
            btnPredict = itemView.findViewById(R.id.btnPredict);
            btnPredict.setVisibility(View.GONE);
        }

        public void bind(IMatch match)
        {
            Match m = (Match) match;
            tvName.setText(match.getOpponent());
            tvTime.setText(match.getTime());
            btnp1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m.increment("p1PredictionVotes");
                    m.saveInBackground();
                }
            });
            btnp2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m.increment("p2PredictionVotes");
                    m.saveInBackground();
                }
            });
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