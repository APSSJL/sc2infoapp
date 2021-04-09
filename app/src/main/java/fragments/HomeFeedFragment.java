package fragments;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import interfaces.IPublished;
import models.Match;
import models.Post;

import com.example.sc2infoapp.MatchDetailActivity;
import com.example.sc2infoapp.Notification;
import com.example.sc2infoapp.PostComposeActivity;
import com.example.sc2infoapp.R;

import models.TeamMatch;
import models.Tournament;
import adapters.UserFeedAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class HomeFeedFragment extends Fragment {
    private static final String TAG = "HOME_FEED";
    Button btnCreatePost;
    RecyclerView rvFeed;
    UserFeedAdapter adapter;
    List<IPublished> published;
    Date lastUpdated;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_feed, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnCreatePost = view.findViewById(R.id.btnCreatePost);
        rvFeed = view.findViewById(R.id.rvFeed);
        published = new ArrayList<>();

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,getContext().toString());
                Intent i = new Intent(getContext(), PostComposeActivity.class);
                startActivity(i);
            }
        });


        adapter = new UserFeedAdapter(getContext(), published);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvFeed.setLayoutManager(manager);
        rvFeed.setAdapter(adapter);

        populateUserFeed();
    }

    private void getLastUpdate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm z");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String date = pref.getString("lastUpdate", "");
        if(date.equals(""))
            lastUpdated = new Date(System.currentTimeMillis());
        else {
            try {
                lastUpdated = formatter.parse(date);
            } catch (java.text.ParseException e) {
                lastUpdated = new Date(System.currentTimeMillis());
            }
        }

        SharedPreferences.Editor edit = pref.edit();
        edit.putString("lastUpdate", formatter.format(new Date(System.currentTimeMillis())));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateUserFeed() {
        getLastUpdate();

        ParseUser user = ParseUser.getCurrentUser();
        try {
            user.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray follows = user.getJSONArray("follows");
        Log.i("asd","asd");
        getLastUpdate();

        HashMap<String, ArrayList<String>> hm = new HashMap<>();
        for(int i = 0; i < follows.length(); i++)
        {
            try {
                String[] follow = follows.getString(i).split(":");
                if(!hm.containsKey(follow[0]))
                {
                    hm.put(follow[0], new ArrayList<>());
                }
                hm.get(follow[0]).add(follow[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        getUserUpdate(hm.get("User"));
        getMatchUpdate(hm.get("Match"));
        getTeamMatchUpdate(hm.get("TeamMatch"));
        Log.i(TAG, "Why the hell I can't set breakpoint at end statement?");
    }

    private void getMatchUpdate(ArrayList<String> matchId)
    {
        if(matchId == null)
            return;;
        ParseQuery<Match> query = ParseQuery.getQuery(Match.class);
        query.setLimit(5);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereLessThan("time", lastUpdated);

        query.findInBackground(new FindCallback<Match>() {
            @Override
            public void done(List<Match> objects, ParseException e) {
                for(Match m : objects)
                {
                    Activity a = getActivity();
                    String content = (m.getWinner() != null) ? "Match you were following has ended. Tap to see the winner."  : "Match you're following have started!";
                    published.add(new Notification(content, String.format("Match update: %s", m.getOpponent()), m.getDate("time"), "", () ->
                    {
                        Intent i = new Intent(a, MatchDetailActivity.class);
                        i.putExtra("match", Parcels.wrap(m));
                        startActivity(i);
                    }, R.drawable.noun_versus
                    ));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getTeamMatchUpdate(ArrayList<String> matchId)
    {
        if(matchId == null)
            return;;
        ParseQuery<TeamMatch> query = ParseQuery.getQuery(TeamMatch.class);
        query.setLimit(5);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereLessThan("time", lastUpdated);

        query.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                for(TeamMatch m : objects)
                {
                    Activity a = getActivity();
                    String content = (m.getWinner() != null) ? "Team Match you were following has ended. Tap to see the winner."  : "Match you're following have started!";
                    published.add(new Notification(content, String.format("Team Match update: %s", m.getOpponent()), m.getDate("time"), "", () ->
                    {
                        Intent i = new Intent(a, MatchDetailActivity.class);
                        i.putExtra("match", Parcels.wrap(m));
                        startActivity(i);
                    },R.drawable.noun_versus
                    ));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getUserUpdate(ArrayList<String> userId)
    {
        if(userId == null)
            return;
        List<ParseUser> user = null;
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereContainedIn(ParseUser.KEY_OBJECT_ID, userId);
        try {
            user = userQuery.find();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(5);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.include("author");
        query.whereContainedIn("author", user);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                published.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    //@Override
    //public void onResume() {
    //    super.onResume();
    //    published.clear();
    //    populateUserFeed();
    //}
}
