package com.example.sc2infoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TeamPlayerAdapter extends RecyclerView.Adapter<TeamPlayerAdapter.TeamPlayerViewHolder>{

    ArrayList<ParseUser> players;
    Context context;

    public TeamPlayerAdapter(ArrayList<ParseUser> players, Context context)
    {
        this.players = players;
        this.context = context;
    }

    @NonNull
    @Override
    public TeamPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_user, parent, false);
        return new TeamPlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamPlayerViewHolder holder, int position) {
        holder.bind(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class TeamPlayerViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvName;
        TextView tvRace;

        public TeamPlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRace = itemView.findViewById(R.id.tvRace);
        }

        public void bind(ParseUser user)
        {
            tvName.setText(user.getUsername());
            tvRace.setText(user.getString("inGameRace"));
        }
    }
}
