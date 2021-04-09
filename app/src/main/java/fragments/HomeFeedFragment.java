package fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import models.Player;
import models.Post;

import com.example.sc2infoapp.MatchDetailActivity;
import models.Notification;
import com.example.sc2infoapp.PostComposeActivity;
import com.example.sc2infoapp.R;

import models.Team;
import models.TeamMatch;
import adapters.UserFeedAdapter;
import models.UserTournament;

import com.example.sc2infoapp.TeamActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
        getTournamentUpdate(hm.get("Tourn"));
        TeamUpdates(hm.get("Team"));
        PlayerUpdates(hm.get("Player"));
        Log.i(TAG, "Why the hell I can't set breakpoint at end statement?");
    }

    private void PlayerUpdates(ArrayList<String> playerId) {
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query.addDescendingOrder(Player.KEY_UPDATED_AT);
        query.include("user");
        query.whereContainedIn("objectId", playerId);

        query.findInBackground(new FindCallback<Player>() {
            @Override
            public void done(List<Player> objects, ParseException e) {
                for(Player p : objects)
                {
                    Activity a = getActivity();
                    CheckMatches(p.getParseUser("user"), () ->
                    {
                        published.add(new Notification("Player has matches updates", String.format("Player update: %s", p.getName()), p.getUpdatedAt(), "", () ->
                        {
                            Intent i = new Intent(a, TeamActivity.class);
                            i.putExtra("playerName", p.getName());
                            startActivity(i);
                        }, R.drawable.noun_team));
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void getMatchUpdate(ArrayList<String> matchId)
    {
        if(matchId == null)
            return;;
        ParseQuery<Match> query = ParseQuery.getQuery(Match.class);
        query.setLimit(5);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereLessThan("time", lastUpdated);
        query.whereContainedIn("objectId", matchId);

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

    private void getTournamentUpdate(ArrayList<String> tournId)
    {
        ParseQuery<UserTournament> query = ParseQuery.getQuery(UserTournament.class);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereContainedIn("objectId", tournId);

        query.findInBackground(new FindCallback<UserTournament>() {
            @Override
            public void done(List<UserTournament> objects, ParseException e) {
                for(UserTournament t : objects)
                {
                    ArrayList<String> matchList = t.getMatches();
                    CheckMatches(matchList, () ->
                    {
                        published.add(new Notification("Tournament you follow was updated", String.format("Tournament update: %s", t.get("name")), t.getUpdatedAt(), "", () ->
                        {
                            //TODO: Navigate tournament details screen
                        }, R.drawable.noun_tournament));
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void TeamUpdates(ArrayList<String> teamId)
    {
        ParseQuery<Team> query = ParseQuery.getQuery(Team.class);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereContainedIn("objectId", teamId);

        query.findInBackground(new FindCallback<Team>() {
            @Override
            public void done(List<Team> objects, ParseException e) {
                for(Team t : objects)
                {
                    Activity a = getActivity();
                    CheckTeamMatches(t, () ->
                    {
                        published.add(new Notification("Team has matches updates", String.format("Team update: %s", t.getTeamName()), t.getUpdatedAt(), "", () ->
                        {
                            Intent i = new Intent(a, TeamActivity.class);
                            i.putExtra("teamName", t.getTeamName());
                            startActivity(i);
                        }, R.drawable.noun_team));
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    public void CheckTeamMatches(Team t, Runnable callback)
    {
        ParseQuery<TeamMatch> query1 = ParseQuery.getQuery(TeamMatch.class);
        query1.whereLessThan("time", lastUpdated);
        query1.whereEqualTo("Team1", t);


        ParseQuery<TeamMatch> query2 = ParseQuery.getQuery(TeamMatch.class);
        query2.whereLessThan("time", lastUpdated);
        query2.whereEqualTo("Team2", t);
        ArrayList<ParseQuery<TeamMatch>> x = new ArrayList<ParseQuery<TeamMatch>>();
        x.add(query1);
        x.add(query2);
        ParseQuery.or(x).findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                if(objects.size() == 0)
                    return;
                callback.run();
            }
        });

    }
    public void CheckMatches(ParseUser p, Runnable callback)
    {
        ParseQuery<Match> query1 = ParseQuery.getQuery(Match.class);
        query1.whereLessThan("time", lastUpdated);
        query1.whereEqualTo("Player1", p);


        ParseQuery<Match> query2 = ParseQuery.getQuery(Match.class);
        query2.whereLessThan("time", lastUpdated);
        query2.whereEqualTo("Player2", p);
        ArrayList<ParseQuery<Match>> x = new ArrayList<ParseQuery<Match>>();
        x.add(query1);
        x.add(query2);
        ParseQuery.or(x).findInBackground(new FindCallback<Match>() {
            @Override
            public void done(List<Match> objects, ParseException e) {
                if(objects.size() == 0)
                    return;
                callback.run();
            }
        });
    }

    public void CheckMatches(ArrayList<String> matchList, Runnable callback)
    {
        ParseQuery<TeamMatch> query = ParseQuery.getQuery(TeamMatch.class);
        query.whereLessThan("time", lastUpdated);
        query.whereContainedIn("objectId", matchList);
        query.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                if(objects.size() == 0)
                    return;
                callback.run();
            }
        });

        ParseQuery<Match> query1 = ParseQuery.getQuery(Match.class);
        query1.whereLessThan("time", lastUpdated);
        query1.whereContainedIn("objectId", matchList);
        query1.findInBackground(new FindCallback<Match>() {
            @Override
            public void done(List<Match> entries, ParseException e) {
                if(entries.size() == 0)
                    return;
                callback.run();
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
        query.whereContainedIn("objectId", matchId);

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
