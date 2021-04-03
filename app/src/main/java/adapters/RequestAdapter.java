package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.R;
import com.example.sc2infoapp.TeamManageActivity;
import com.example.sc2infoapp.ViewProfileActivity;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.nio.file.attribute.AclFileAttributeView;
import java.util.ArrayList;

import models.Team;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{

    Context context;
    Activity activity;
    ArrayList<ParseUser> requests;
    Team team;

    public RequestAdapter(Context context, ArrayList<ParseUser> requests, Team team, Activity a)
    {
        this.context = context;
        this.requests = requests;
        this.activity = a;
        this.team = team;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.bind(requests.get(position), position);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvName;
        Button btnApprove;
        Button btnDecline;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnApprove = itemView.findViewById(R.id.btnApprove);
        }

        public void bind(ParseUser user, int position)
        {
            tvName.setText(user.getUsername());
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ViewProfileActivity.class);
                    i.putExtra("user", user);
                    activity.startActivity(i);
                }
            });
            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    team.addToLineup(user);
                    team.saveInBackground();
                    int p = requests.indexOf(user);
                    requests.remove(p);
                    notifyItemRemoved(p);
                    ParseObject x = user.getParseObject("Additional");
                    x.put("team", team);
                    x.saveInBackground();
                }
            });

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    team.removeRequest(user.getObjectId());
                    int p = requests.indexOf(user);
                    requests.remove(p);
                    notifyItemRemoved(p);
                }
            });
        }
    }
}
