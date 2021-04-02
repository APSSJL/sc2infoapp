package com.example.sc2infoapp;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHolder> {

    ArrayList<Pair<String, String>> opponents;
    Context context;

    public MatchesAdapter(ArrayList<Pair<String, String>> opponents, Context context)
    {
        this.opponents = opponents;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesAdapter.MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
        return new MatchesAdapter.MatchesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.MatchesViewHolder holder, int position) {
        holder.bind(opponents.get(position));
    }

    @Override
    public int getItemCount() {
        return opponents.size();
    }

    class MatchesViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvName;
        TextView tvTime;

        public MatchesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Pair<String, String> player)
        {
            tvName.setText(player.first);
            tvTime.setText(player.second);
        }
    }
}
