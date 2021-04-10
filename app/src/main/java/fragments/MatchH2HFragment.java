package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import adapters.H2HAdapter;
import interfaces.IMatch;
import models.TaskRunner;

public class MatchH2HFragment extends Fragment {

    public static final String TAG = "MatchH2HFragment";

    IMatch match;
    String opponentLeft;
    String opponentRight;

    ArrayList<IMatch> matches;
    H2HAdapter h2hAdapter;
    RecyclerView rvPreviousMeeting;


    public MatchH2HFragment(IMatch match, String opponentLeft, String opponentRight) {
        this.match = match;
        this.opponentLeft = opponentLeft;
        this.opponentRight = opponentRight;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_h2h, container, false);

        rvPreviousMeeting = view.findViewById(R.id.rvPreviousMeeting);

        matches = new ArrayList<>();
        h2hAdapter = new H2HAdapter(getContext(), matches);

        rvPreviousMeeting.setAdapter(h2hAdapter);
        rvPreviousMeeting.setLayoutManager(new LinearLayoutManager(getContext()));

        switch(match.getMatchType()) {
            case IMatch.EXTERNAL:
                TaskRunner taskRunner = new TaskRunner();
                taskRunner.executeAsync(new MatchH2HFragment.H2HTask(opponentLeft, opponentRight), (data) -> {
                    Log.i("H2H", data.toString());
                    try {
                        matches.addAll(H2HAdapter.fromJSONArray(data));
                        h2hAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case IMatch.INTERNAL:
                break;
            case IMatch.TEAM:
                break;
        }

        return view;
    }

    public class H2HTask implements Callable<JSONArray> {
        private final String input1;
        private final String input2;

        public H2HTask(String input1, String input2) {
            this.input1 = input1;
            this.input2 = input2;
        }

        @Override
        public JSONArray call() throws IOException, JSONException {
            // Some long running task
            int p1 = MainActivity.aligulacClient.getPlayer(input1).getInt("id");
            int p2 = MainActivity.aligulacClient.getPlayer(input2).getInt("id");
            Log.i(TAG, String.valueOf(p1));
            Log.i(TAG, String.valueOf(p2));
            return MainActivity.aligulacClient.getMatchesHistory(p1, p2);
        }
    }

}