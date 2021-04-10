package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.MatchDetailActivity;
import com.example.sc2infoapp.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.TournamentMatches;

public class MatchFeedAdapter extends RecyclerView.Adapter<MatchFeedAdapter.ViewHolder>{

    Context context;
    ArrayList<TournamentMatches> matches;

    public MatchFeedAdapter(Context context, ArrayList<TournamentMatches> matches){
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public MatchFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View matchView = LayoutInflater.from(context).inflate(R.layout.item_tournament_matches,parent,false);
        return new ViewHolder(matchView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentMatches match = matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTournament;
        RecyclerView rvMatches;
        MatchesAdapter adapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTournament = itemView.findViewById(R.id.tvTournamentName);
            rvMatches = itemView.findViewById(R.id.rvMatches);
        }

        public void bind(TournamentMatches tournamentMatches) {
            tvTournament.setText(tournamentMatches.getName());
            adapter = new MatchesAdapter(tournamentMatches.getMatches(), context);

            rvMatches.setAdapter(adapter);
            rvMatches.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}
