package fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.AligulacClient;
import com.example.sc2infoapp.HomeFilterActivity;
import com.example.sc2infoapp.LiquipediaParser;
import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.R;
import com.example.sc2infoapp.TeamActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import adapters.MatchFeedAdapter;
import adapters.MatchesAdapter;
import adapters.UserFeedAdapter;
import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.Notification;
import models.TaskRunner;
import models.Team;
import models.TeamMatch;
import models.Tournament;
import models.TournamentMatches;
import models.UserTournament;

public class MatchFeedFragment extends Fragment {
    public static final String TAG = "MATCH_FEED_FRAG";
    MatchFeedAdapter adapter;
    RecyclerView rvMatchFeed;
    ArrayList<TournamentMatches> tmatches;
    Button btnFilter;
    SharedPreferences pref ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_feed,container,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //find recycler view
        rvMatchFeed = view.findViewById(R.id.rvMatches);
        //Initialize matches and adapter
        tmatches = new ArrayList<>();
        btnFilter = view.findViewById(R.id.btnFilter);
        adapter = new MatchFeedAdapter(getContext(), tmatches);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Recycler view setup
        rvMatchFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchFeed.setAdapter(adapter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), HomeFilterActivity.class);
                startActivity(i);
            }
        });

        super.onViewCreated(view, savedInstanceState);
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new GetTournamentTask(), (data) -> {
            tmatches.addAll(data);
            adapter.notifyDataSetChanged();
        });

        getTournamentUpdate();
    }


    class GetTournamentTask implements Callable<ArrayList<TournamentMatches>> {

        @SuppressLint("NewApi")
        @Override
        public ArrayList<TournamentMatches> call() throws IOException, JSONException {
            // Some long running task
            LiquipediaParser parser = new LiquipediaParser();
            ArrayList<ExternalMatch> matches = parser.parseUpcomingMatches(Jsoup.parse(MainActivity.client.getMatches()));

            HashMap<String, ArrayList<IMatch>> groupedMatches = new HashMap<>();
            for(ExternalMatch m : matches)
            {
                if(!groupedMatches.containsKey(m.getTournament()))
                {
                    groupedMatches.put(m.getTournament(), new ArrayList<>());
                }
                groupedMatches.get(m.getTournament()).add(m);
            }

            ArrayList<TournamentMatches> res = new ArrayList<>();
            for (Map.Entry<String, ArrayList<IMatch>> entry : groupedMatches.entrySet()) {
                res.add(new TournamentMatches(entry.getKey(), entry.getValue()));
            }
            return res;
        }
    }

    private void getTournamentUpdate() {
        String bantor = pref.getString("bantor", "");
        ArrayList<Object> bannedTourns = new ArrayList<>();
        if(!bantor.equals(""))
            Collections.addAll(bannedTourns, bantor.split(","));
        ParseQuery<UserTournament> query = ParseQuery.getQuery(UserTournament.class);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereNotContainedIn("name", bannedTourns);

        query.findInBackground(new FindCallback<UserTournament>() {
            @Override
            public void done(List<UserTournament> objects, ParseException e) {

                for (UserTournament t : objects) {
                    ParseQuery<Match> qmatches = ParseQuery.getQuery(Match.class);
                    qmatches.whereContainedIn("objectId",t.getMatches());
                    ParseQuery<TeamMatch> qtmatches = ParseQuery.getQuery(TeamMatch.class);
                    qtmatches.whereContainedIn("objectId",t.getMatches());
                    qmatches.whereGreaterThanOrEqualTo("rating", pref.getInt("rating", 0));
                    qtmatches.whereGreaterThanOrEqualTo("rating", pref.getInt("rating", 0));


                    qmatches.findInBackground(new FindCallback<Match>() {
                        @Override
                        public void done(List<Match> objects, ParseException e) {
                            if(objects.size() == 0)
                                return;
                            TournamentMatches tuorn = new TournamentMatches(t.getString("name"), new ArrayList<>(objects));
                            tuorn.setParseTournament(t);
                            tuorn.setUserCreated(true);
                            tmatches.add(tuorn);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    qtmatches.findInBackground(new FindCallback<TeamMatch>() {
                        @Override
                        public void done(List<TeamMatch> objects, ParseException e) {
                            if(objects.size() == 0)
                                return;
                            TournamentMatches tuorn = new TournamentMatches(t.getString("name"), new ArrayList<>(objects));
                            tuorn.setParseTournament(t);
                            tuorn.setUserCreated(true);
                            tmatches.add(tuorn);
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        });
    }

}
