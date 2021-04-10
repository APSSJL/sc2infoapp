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

public class H2HAdapter extends RecyclerView.Adapter<H2HAdapter.ViewHolder> {

    Context context;
    List<IMatch> matches;

    public H2HAdapter(Context context, List<IMatch> matches) {
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View h2hView = LayoutInflater.from(context).inflate(R.layout.item_h2h,parent,false);
        return new ViewHolder(h2hView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IMatch match = matches.get(position);
        holder.bind(match);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public static List<ExternalMatch> fromJSONArray(JSONArray jsonArray)throws JSONException {
        List<ExternalMatch> matches = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            matches.add(new ExternalMatch(jsonArray.getJSONObject(i)));
        }
        return matches;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvH2HEventName;
        TextView tvH2HScoreLeft;
        TextView tvH2HScoreRight;
        TextView tvH2HLeft;
        TextView tvH2HRight;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvH2HEventName = itemView.findViewById(R.id.tvH2HEventName);
            tvH2HScoreLeft = itemView.findViewById(R.id.tvH2HScoreLeft);
            tvH2HScoreRight = itemView.findViewById(R.id.tvH2HScoreRight);
            tvH2HLeft = itemView.findViewById(R.id.tvH2HLeft);
            tvH2HRight = itemView.findViewById(R.id.tvH2HRight);
        }

        public void bind(IMatch match) {
            tvH2HLeft.setText(((ExternalMatch)match).getOpponent().split(" vs ")[0]);
            tvH2HRight.setText(((ExternalMatch)match).getOpponent().split(" vs ")[1]);
            tvH2HScoreLeft.setText(String.valueOf(((ExternalMatch)match).getResult1()));
            tvH2HScoreLeft.setText(String.valueOf(((ExternalMatch)match).getResult2()));
            if (match.getMatchType() == IMatch.EXTERNAL) {
                tvH2HEventName.setText(((ExternalMatch)match).getTournament());
            } else {
                tvH2HEventName.setText("Internal Match");
            }
        }
    }
}
