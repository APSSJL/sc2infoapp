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
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.MatchDetailActivity;
import com.example.sc2infoapp.R;

import org.parceler.Parcels;

import java.util.List;

import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;

public class MatchFeedAdapter extends RecyclerView.Adapter<MatchFeedAdapter.ViewHolder>{

    Context context;
    List<ExternalMatch> matches;

    public MatchFeedAdapter(Context context, List<ExternalMatch> matches){
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public MatchFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View matchView = LayoutInflater.from(context).inflate(R.layout.item_match,parent,false);
        return new ViewHolder(matchView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExternalMatch match=matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVersus;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVersus = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(ExternalMatch match) {
            tvVersus.setText(match.getOpponent());
            tvTime.setText(match.getTime());
            tvVersus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent((Activity)context, MatchDetailActivity.class);
                    i.putExtra("match", Parcels.wrap(match));
                    ((Activity)context).startActivity(i);
                }
            });
        }
    }
}