package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sc2infoapp.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.Team;
import models.TeamMatch;

public class MatchInfoFragment extends Fragment {

    public static final String TAG = "MatchInfoFragment";

    IMatch match;
    String opponentLeft;
    String opponentRight;
    Team left;
    Team right;

    Button btnComment;
    TextView tvMatchDetailList;
    TextView tvTeamLeft;
    TextView tvTeamRight;
    TextView tvLineupLeft;
    TextView tvLineupRight;
    RelativeLayout rlLineup;

    public MatchInfoFragment(IMatch match, String opponentLeft, String opponentRight) {
        this.match = match;
        this.opponentLeft = opponentLeft;
        this.opponentRight = opponentRight;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_info, container, false);

        btnComment = view.findViewById(R.id.btnTornFollow);
        tvMatchDetailList = view.findViewById(R.id.tvMatchDetailList);
        tvTeamLeft = view.findViewById(R.id.tvLineupLeft);
        tvTeamRight = view.findViewById(R.id.tvLineupRight);
        tvLineupLeft = view.findViewById(R.id.tvLineupListLeft);
        tvLineupRight = view.findViewById(R.id.tvLineupListRight);
        rlLineup =  view.findViewById(R.id.rlLineup);

        if (match.getMatchType() == IMatch.TEAM) {
            left = findTeam(opponentLeft);
            right = findTeam(opponentRight);

            tvTeamLeft.setText(left.getTeamName());
            tvTeamRight.setText(right.getTeamName());
            tvLineupLeft.setText(lineupIntoString(left.getLineup()));
            tvLineupRight.setText(lineupIntoString(right.getLineup()));
        } else {
            rlLineup.setVisibility(View.GONE);
        }

        tvMatchDetailList.setText(setMatchDetail());
        return view;
    }

    public String setMatchDetail() {
        switch (match.getMatchType()) {
            case IMatch.EXTERNAL:
                return String.format(match.getTime() + "\n" + ((ExternalMatch) match).getTournament());
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