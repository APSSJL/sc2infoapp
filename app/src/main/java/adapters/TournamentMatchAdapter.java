package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import interfaces.IMatch;
import models.ExternalMatch;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tournamentView = LayoutInflater.from(context).inflate(R.layout.item_tournament_match,parent,false);
        return new ViewHolder(tournamentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    // Clean all elements of the recycler
    public void clear() {
        matches.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<IMatch> list) {
        matches.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View dvTournamentMatchItem;
        TextView tvTournTime;
        TextView tvTournScoreLeft;
        TextView tvTournScoreRight;
        TextView tvTournLeft;
        TextView tvTournRight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dvTournamentMatchItem = itemView.findViewById(R.id.dvTournamentMatchItem);
            tvTournTime = itemView.findViewById(R.id.tvTournTime);
            tvTournScoreLeft = itemView.findViewById(R.id.tvTournScoreLeft);
            tvTournScoreRight = itemView.findViewById(R.id.tvTournScoreRight);
            tvTournLeft = itemView.findViewById(R.id.tvTournLeft);
            tvTournRight = itemView.findViewById(R.id.tvTournRight);
        }

        public void bind(IMatch match) {
            if (match.getTime().isEmpty()) {
                tvTournTime.setVisibility(View.GONE);
                dvTournamentMatchItem.setVisibility(View.GONE);
            } else {
                tvTournTime.setText(match.getTime());
            }

            try {
                tvTournScoreLeft.setText(String.valueOf(match.getResult1()));
                tvTournScoreRight.setText(String.valueOf(match.getResult2()));
                try {
                    tvTournLeft.setText(match.getOpponent().split(" vs ")[0].split(" | player")[0]);
                    tvTournRight.setText(match.getOpponent().split(" vs ")[1].split(" | player")[0]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    tvTournLeft.setText(match.getOpponent().split(" vs ")[0]);
                    tvTournRight.setText(match.getOpponent().split(" vs ")[1]);
                }

            } catch(ArrayIndexOutOfBoundsException e) {
                tvTournTime.setText(match.getTime());
                tvTournScoreLeft.setText("0");
                tvTournScoreRight.setText("0");
                tvTournLeft.setText("TBD");
                tvTournRight.setText("TBD");
            }

        }
    }

}
