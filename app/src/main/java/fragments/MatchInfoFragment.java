package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.ParseApplication;
import com.example.sc2infoapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.NotificationDao;
import models.Match;
import models.Team;
import models.TeamMatch;

public class MatchInfoFragment extends Fragment {

    public static final String TAG = "MatchInfoFragment";

    IMatch match;
    Team left;
    Team right;

    Button btnComment;
    Button btnFollow;
    TextView tvMatchDetailList;
    TextView tvTeamLeft;
    TextView tvTeamRight;
    TextView tvLineupLeft;
    TextView tvLineupRight;
    RelativeLayout rlLineup;

    public MatchInfoFragment(IMatch match) {
        this.match = match;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_info, container, false);

        btnComment = view.findViewById(R.id.btnMatchComment);
        btnFollow = view.findViewById(R.id.btnMatchFollow);
        tvMatchDetailList = view.findViewById(R.id.tvMatchDetailList);
        tvTeamLeft = view.findViewById(R.id.tvLineupLeft);
        tvTeamRight = view.findViewById(R.id.tvLineupRight);
        tvLineupLeft = view.findViewById(R.id.tvLineupListLeft);
        tvLineupRight = view.findViewById(R.id.tvLineupListRight);
        rlLineup =  view.findViewById(R.id.rlLineup);

        if (match.getMatchType() == 2) {
            left = findTeam(match.getOpponent().split(" vs ")[0]);
            right = findTeam(match.getOpponent().split(" vs ")[1]);

            tvTeamLeft.setText(left.getTeamName());
            tvTeamRight.setText(right.getTeamName());
            tvLineupLeft.setText(lineupIntoString(left.getLineup()));
            tvLineupRight.setText(lineupIntoString(right.getLineup()));
        } else {
            rlLineup.setVisibility(View.GONE);
        }

        tvMatchDetailList.setText(setMatchDetail());

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Send user to comment detail activity
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((IFollowable) match).setFollow()) {
                    Log.i(TAG, "Follow successfully: " + match.getOpponent());
                    Toast.makeText(getActivity(), String.format("Successfully followed: " + match.getOpponent()), Toast.LENGTH_SHORT);
                } else {
                    Log.i(TAG, "Already followed: " + match.getOpponent());
                    Toast.makeText(getActivity(), String.format("Already followed: " + match.getOpponent()), Toast.LENGTH_SHORT);
                }
            }
        });

        return view;
    }

    public String setMatchDetail() {
        switch (match.getMatchType()) {
            case IMatch.EXTERNAL:
                return match.getTime();
            case IMatch.INTERNAL:
                return String.format(match.getTime() + "\n" + ((Match) match).getDetails());
            case IMatch.TEAM:
                return String.format(match.getTime() + "\n" + ((TeamMatch) match).getDetails());
        }
        return null;
    }

    public Team findTeam(String name) {
        Team res = null;
        ParseQuery<Team> query = new ParseQuery<Team>(Team.class);
        query.whereEqualTo(Team.KEY_NAME, name);
        try {
            List<Team> x = query.find();
            if(x == null)
                return null;
            return  x.get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String lineupIntoString(ArrayList<ParseUser> lineups) {
        StringBuilder sb = new StringBuilder();
        for (ParseUser player : lineups) {
            sb.append(player.getUsername() + "\n");
        }
        return sb.toString();
    }
}