package fragments;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sc2infoapp.AligulacClient;
import com.example.sc2infoapp.LiquipediaParser;
import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.R;
import com.example.sc2infoapp.TeamActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
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
import models.TaskRunner;
import models.Team;
import models.Tournament;
import models.TournamentMatches;

public class MatchFeedFragment extends Fragment {
    public static final String TAG = "MATCH_FEED_FRAG";
    MatchFeedAdapter adapter;
    RecyclerView rvMatchFeed;
    ArrayList<TournamentMatches> tmatches;

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

        adapter = new MatchFeedAdapter(getContext(), tmatches);

        //Recycler view setup
        rvMatchFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchFeed.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);

        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new GetTournamentTask(), (data) -> {
            tmatches.addAll(data);
            adapter.notifyDataSetChanged();
        });
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

}
