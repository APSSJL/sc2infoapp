package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.LiquipediaParser;
import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.MatchDetailActivity;
import com.example.sc2infoapp.R;
import com.example.sc2infoapp.TournamentInfoActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.TaskRunner;
import models.TeamMatch;

public class TournamentMatchAdapter extends RecyclerView.Adapter<TournamentMatchAdapter.ViewHolder>{

    public static final String TAG = "TournamentMatchAdapter";

    Context context;
    List<IMatch> matches;

    public TournamentMatchAdapter(List<IMatch> matches, Context context) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public TournamentMatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tournamentView = LayoutInflater.from(context).inflate(R.layout.item_tournament_match,parent,false);
        return new TournamentMatchAdapter.ViewHolder(tournamentView);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentMatchAdapter.ViewHolder holder, int position) {
        IMatch match = matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {return matches.size();}

    public static List<ExternalMatch> fromJSONArray(JSONArray jsonArray)throws JSONException {
        List<ExternalMatch> matches = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            ExternalMatch externalMatch= new ExternalMatch(jsonArray.getJSONObject(i));
            if (!externalMatch.isTreated()) {
                matches.add(externalMatch);
            }
        }
        return matches;
    }

    public static List<Match> fromParsePlayerMatch(ArrayList<String> matchNames){
        List<Match> matches = new ArrayList<>();
        for (String name : matchNames) {
            ParseQuery<Match> q2 = ParseQuery.getQuery(Match.class);
            q2.include("objectId");
            q2.whereEqualTo("objectId", matchNames.get(0));
            q2.findInBackground(new FindCallback<Match>() {
                @Override
                public void done(List<Match> objects, ParseException e) {
                    matches.addAll(objects);
                }
            });
        }
        return matches;
    }

    public static List<TeamMatch> fromParseTeamMatch(ArrayList<String> matchNames){
        List<TeamMatch> matches = new ArrayList<>();
        for (String name : matchNames) {
            ParseQuery<TeamMatch> q2 = ParseQuery.getQuery(TeamMatch.class);
            q2.include("objectId");
            q2.whereEqualTo("objectId", matchNames.get(0));
            q2.findInBackground(new FindCallback<TeamMatch>() {
                @Override
                public void done(List<TeamMatch> objects, ParseException e) {
                    matches.addAll(objects);
                }
            });
        }
        return matches;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTournTime;
        TextView tvTournScoreLeft;
        TextView tvTournScoreRight;
        TextView tvTournLeft;
        TextView tvTournRight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTournTime = itemView.findViewById(R.id.tvTournTime);
            tvTournScoreLeft = itemView.findViewById(R.id.tvTournScoreLeft);
            tvTournScoreRight = itemView.findViewById(R.id.tvTournScoreRight);
            tvTournLeft = itemView.findViewById(R.id.tvTournLeft);
            tvTournRight = itemView.findViewById(R.id.tvTournRight);
        }

        public void bind(IMatch match) {
            tvTournTime.setText(match.getTime());
            tvTournLeft.setText(match.getOpponent().split(" vs ")[0]);
            tvTournRight.setText(match.getOpponent().split(" vs ")[1]);
            tvTournScoreLeft.setText(match.getResult1());
            tvTournScoreRight.setText(match.getResult2());
        }
    }

}
