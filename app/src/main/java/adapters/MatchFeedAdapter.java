package adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.R;

import java.util.List;

import models.Match;

public class MatchFeedAdapter extends RecyclerView.Adapter<MatchFeedAdapter.ItemViewHolder>{

    Context context;
    List<Match> matches;

    public MatchFeedAdapter(Context context, List<Match> matches){
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
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Match match=matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class ViewHolder extends ItemViewHolder {

        TextView tvVersus;
        TextView tvTime;
        Button btnPredict;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVersus = itemView.findViewById(R.id.tvVersus);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnPredict = itemView.findViewById(R.id.btnPredict);
        }

        @Override
        public void bind(Match match) {
            tvVersus.setText(match.getOpponent());
            tvTime.setText(match.getTime());
        }
    }

    abstract class ItemViewHolder extends RecyclerView.ViewHolder{
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract public void bind(final Match match);
    }
}
