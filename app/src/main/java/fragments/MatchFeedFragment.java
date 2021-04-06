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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    List<Match> matches;

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
        matches = new ArrayList<>();
        adapter = new MatchFeedAdapter(getContext(), matches);

        //Recycler view setup
        rvMatchFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchFeed.setAdapter(adapter);

        populateMatchFeed();
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api= Build.VERSION_CODES.N)
    private void populateMatchFeed(){
        Log.i(TAG,"populating");
    }
}
