package fragments;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;
import java.util.concurrent.Callable;

import adapters.MatchFeedAdapter;
import adapters.MatchesAdapter;
import adapters.UserFeedAdapter;
import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.TaskRunner;
import models.Team;

public class MatchFeedFragment extends Fragment {
    public static final String TAG = "MATCH_FEED_FRAG";
    MatchFeedAdapter adapter;
    RecyclerView rvMatchFeed;
    List<ExternalMatch> externalMatches;

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
        externalMatches = new ArrayList<>();
        adapter = new MatchFeedAdapter(getContext(), externalMatches);

        //Recycler view setup
        rvMatchFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchFeed.setAdapter(adapter);

        getUpcomingMatches();
        super.onViewCreated(view, savedInstanceState);
    }

    private void getUpcomingMatches(){
        Log.i("tester2", "in");
//        JSONObject cur = null;
        try {
            LiquipediaParser parser = new LiquipediaParser();
            externalMatches.addAll(parser.parseUpcomingMatches(Jsoup.parse(MainActivity.client.getMatches())));
            adapter.notifyDataSetChanged();
            Log.i("tester2", externalMatches.get(1).getTournament());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
