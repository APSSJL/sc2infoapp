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
import models.ExternalMatch;
import models.ExternalMatchNotification;
import models.Match;
import models.Player;
import models.Post;

import com.example.sc2infoapp.HomeFilterActivity;
import com.example.sc2infoapp.LiquipediaParser;
import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.MatchDetailActivity;

import models.Notification;

import com.example.sc2infoapp.PlayerActivity;
import com.example.sc2infoapp.PostComposeActivity;
import com.example.sc2infoapp.R;

import models.TaskRunner;
import models.Team;
import models.TeamMatch;
import adapters.UserFeedAdapter;
import models.UserTournament;

import com.example.sc2infoapp.SearchActivity;
import com.example.sc2infoapp.TeamActivity;
import com.example.sc2infoapp.TournamentInfoActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class HomeFeedFragment extends Fragment {
    private static final String TAG = "HOME_FEED";
    Button btnCreatePost;
    RecyclerView rvFeed;
    UserFeedAdapter adapter;
    List<IPublished> published;
    Date lastUpdated;
    Button btnSearch;
    Button btnHomeFilter;
    SharedPreferences pref;


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
        btnHomeFilter = view.findViewById(R.id.btnHomeFilter);
        rvFeed = view.findViewById(R.id.rvFeed);
        published = new ArrayList<>();
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, getContext().toString());
                Intent i = new Intent(getContext(), PostComposeActivity.class);
                startActivity(i);
            }
        });

        btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivity(i);
            }
        });

        btnHomeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HomeFilterActivity.class);
                startActivity(i);
            }
        });


        adapter = new UserFeedAdapter(getContext(), published);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvFeed.setLayoutManager(manager);
        rvFeed.setAdapter(adapter);

        populateUserFeed();
    }

    private void getLastUpdate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm z");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String date = pref.getString("lastUpdate", "");
        if (date.equals(""))
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
        Log.i("asd", "asd");
        getLastUpdate();

        HashMap<String, ArrayList<String>> hm = new HashMap<>();
        for (int i = 0; i < follows.length(); i++) {
            try {
                String[] follow = follows.getString(i).split(":");
                if (!hm.containsKey(follow[0])) {
                    hm.put(follow[0], new ArrayList<>());
                }
                hm.get(follow[0]).add(follow[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        getUserUpdate(hm.get("User"));
        getMatchUpdate(hm.get("Match"));
        getExternalMatchesNotification();
        getTeamMatchUpdate(hm.get("TeamMatch"));
        getTournamentUpdate(hm.get("Tourn"));
        TeamUpdates(hm.get("Team"));
        ExternalTeamUpdates(hm.get("ExternalTeam"));
        PlayerUpdates(hm.get("Player"));
        Log.i(TAG, "Why the hell I can't set breakpoint at end statement?");
    }

    private void ExternalTeamUpdates(ArrayList<String> names)
    {
        if(names == null)
            return;
        final int[] c = {0};
        Activity a = getActivity();
        for(String name : names) {
            if(c[0] > 5)
                return;
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new MatchTask(name), (data) ->
            {
                if (data.size() > 0) {
                    c[0]++;
                    published.add(new Notification("Team has matches updates", String.format("Team update: %s", name), new Date(System.currentTimeMillis()), "", () ->
                    {
                        Intent i = new Intent(a, TeamActivity.class);
                        i.putExtra("teamName", name);
                        startActivity(i);
                    }, R.drawable.noun_team));
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }

    private void PlayerUpdates(ArrayList<String> playerId) {
        if (playerId == null)
            return;
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query.addDescendingOrder(Player.KEY_UPDATED_AT);
        query.include("user");
        query.whereContainedIn("objectId", playerId);

        query.findInBackground(new FindCallback<Player>() {
            @Override
            public void done(List<Player> objects, ParseException e) {
                final int[] c = {0};
                for (Player p : objects) {
                    if (c[0] > 5)
                        break;
                    Activity a = getActivity();
                    CheckMatches(p.getParseUser("user"), p.getName(), () ->
                    {
                        c[0]++;
                        published.add(new Notification("Player has matches updates", String.format("Player update: %s", p.getName()), p.getUpdatedAt(), "", () ->
                        {
                            Intent i = new Intent(a, PlayerActivity.class);
                            i.putExtra("playerName", p.getName());
                            startActivity(i);
                        }, R.drawable.noun_team));
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void getMatchUpdate(ArrayList<String> matchId) {
        if (matchId == null)
            return;
        ParseQuery<Match> query = ParseQuery.getQuery(Match.class);
        query.setLimit(5);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereLessThan("time", lastUpdated);
        query.whereContainedIn("objectId", matchId);

        query.findInBackground(new FindCallback<Match>() {
            @Override
            public void done(List<Match> objects, ParseException e) {
                for (Match m : objects) {
                    Activity a = getActivity();
                    String content = (m.getWinner() != null) ? "Match you were following has ended. Tap to see the winner." : "Match you're following have started!";
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

    private void getExternalMatchesNotification() {
        Thread t = new Thread(() ->
        {
            List<ExternalMatchNotification> x = MainActivity.notDao.selectUpcoming(ExternalMatch.DATE_FORMATTER.format(new Date(System.currentTimeMillis())),ParseUser.getCurrentUser().getUsername());
            Log.i("xx", "xx");
        });
        t.start();

        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new MatchRequestTask(), (data) ->
        {
            for (ExternalMatchNotification not : data) {
                Activity a = getActivity();
                not.setContent("There is an external match update!");
                not.setCallback(() ->
                {
                    Intent i = new Intent(a, MatchDetailActivity.class);
                    i.putExtra("match", Parcels.wrap(new ExternalMatch(not.opponents, ExternalMatch.DATE_FORMATTER.format(not.date), not.bo)));
                    startActivity(i);
                });
                published.add(not);
            }
            adapter.notifyDataSetChanged();
        });
    }

    class MatchRequestTask implements Callable<List<ExternalMatchNotification>> {
        @Override
        public List<ExternalMatchNotification> call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.notDao.selectUpcoming(ExternalMatch.DATE_FORMATTER.format(new Date(System.currentTimeMillis())),ParseUser.getCurrentUser().getUsername());
        }
    }

    private void getTournamentUpdate(ArrayList<String> tournId) {
        if (tournId == null)
            return;
        ParseQuery<UserTournament> query = ParseQuery.getQuery(UserTournament.class);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereContainedIn("objectId", tournId);

        query.findInBackground(new FindCallback<UserTournament>() {
            @Override
            public void done(List<UserTournament> objects, ParseException e) {
                for (UserTournament t : objects) {
                    ArrayList<String> matchList = t.getMatches();
                    CheckMatches(matchList, () ->
                    {
                        published.add(new Notification("Tournament you follow was updated", String.format("Tournament update: %s", t.get("name")), t.getUpdatedAt(), "", () ->
                        {
                            Intent i = new Intent(getContext(), TournamentInfoActivity.class);
                            i.putExtra("userCreated ", true);
                            i.putExtra("tournament", Parcels.wrap(t));
                            startActivity(i);
                        }, R.drawable.noun_tournament));
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void TeamUpdates(ArrayList<String> teamId) {
        if (teamId == null)
            return;
        ParseQuery<Team> query = ParseQuery.getQuery(Team.class);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereContainedIn("objectId", teamId);

        query.findInBackground(new FindCallback<Team>() {
            @Override
            public void done(List<Team> objects, ParseException e) {
                final Integer[] c = {0};
                for (Team t : objects) {
                    if (c[0] > 5)
                        break;
                    Activity a = getActivity();
                    CheckTeamMatches(t, t.getTeamName(),() ->
                    {
                        c[0] += 1;
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

    public void CheckTeamMatches(Team t, String name,Runnable callback) {
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
                if (objects.size() == 0) {
                    TaskRunner taskRunner = new TaskRunner();
                    taskRunner.executeAsync(new MatchTask(name), (data) ->
                    {
                        if (data.size() > 0) {
                            callback.run();
                            return;
                        }
                    });

                    return;
                }
                callback.run();
            }
        });

    }

    public void CheckMatches(ParseUser p, String name, Runnable callback) {
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
                if (objects.size() == 0) {

                    TaskRunner taskRunner = new TaskRunner();
                    taskRunner.executeAsync(new MatchTask(name), (data) ->
                    {
                        if (data.size() > 0) {
                            callback.run();
                            return;
                        }
                    });

                    return;
                }
                callback.run();
            }
        });
    }

    class MatchTask implements Callable<ArrayList<ExternalMatch>> {
        public MatchTask(String input) {
            this.input = input;
        }

        final String input;

        @Override
        public ArrayList<ExternalMatch> call() throws IOException, JSONException {
            // Some long running task
            LiquipediaParser parse = new LiquipediaParser();
            return parse.getRecentMatches(Jsoup.parse(MainActivity.client.getPageByName(input)), input);
        }
    }

    public void CheckMatches(ArrayList<String> matchList, Runnable callback) {
        ParseQuery<TeamMatch> query = ParseQuery.getQuery(TeamMatch.class);
        query.whereLessThan("time", lastUpdated);
        query.whereContainedIn("objectId", matchList);
        query.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                if (objects.size() == 0)
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
                if (entries.size() == 0)
                    return;
                callback.run();
            }
        });
    }

    private void getTeamMatchUpdate(ArrayList<String> matchId) {
        if (matchId == null)
            return;
        ;
        ParseQuery<TeamMatch> query = ParseQuery.getQuery(TeamMatch.class);
        query.setLimit(5);
        query.addDescendingOrder(Match.KEY_UPDATED_AT);
        query.whereLessThan("time", lastUpdated);
        query.whereContainedIn("objectId", matchId);

        query.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                for (TeamMatch m : objects) {
                    Activity a = getActivity();
                    String content = (m.getWinner() != null) ? "Team Match you were following has ended. Tap to see the winner." : "Match you're following have started!";
                    published.add(new Notification(content, String.format("Team Match update: %s", m.getOpponent()), m.getDate("time"), "", () ->
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

    private void getUserUpdate(ArrayList<String> userId) {
        if (userId == null)
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
        query.whereNotContainedIn(Post.KEY_CATEGORY, getArrayFromPrefs("hidcat"));
        query.whereNotContainedIn(Post.KEY_TAGS, getArrayFromPrefs("bantag"));
        query.include("author");
        query.whereGreaterThanOrEqualTo("rating", pref.getInt("rating", 5));
        query.whereContainedIn("author", user);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                published.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<String> getArrayFromPrefs(String key)
    {
        String res = pref.getString(key, "");
        ArrayList<String> arr = new ArrayList<>();
        if(!res.equals(""))
            Collections.addAll(arr, res.split(","));
        return arr;
    }

    //@Override
    //public void onResume() {
    //    super.onResume();
    //    published.clear();
    //    populateUserFeed();
    //}
}
