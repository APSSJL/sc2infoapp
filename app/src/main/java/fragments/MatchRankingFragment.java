package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sc2infoapp.AligulacClient;
import com.example.sc2infoapp.LiquipediaParser;
import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.MatchCommentActivity;
import com.example.sc2infoapp.R;
import com.example.sc2infoapp.TournamentInfoActivity;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import adapters.BaseCommentAdapter;
import adapters.MatchesAdapter;
import interfaces.IMatch;
import interfaces.IPredictable;
import interfaces.IRateable;
import models.Comment;
import models.ExternalMatch;
import models.TaskRunner;

public class MatchRankingFragment extends Fragment {

    BaseCommentAdapter adapter;
    double leftDistribution;
    List<Comment> allComments;
    IMatch match;
    String opponentLeft;
    String opponentRight;

    Button btnMatchMakePredict;
    Button btnMatchMakeComment;
    Button btnMatchRate;
    ProgressBar pbMatchPrediction;
    RatingBar rbMatchRanking;
    RelativeLayout rlMatchRanking;
    RecyclerView rvMatchComment;
    TextView tvPredictionHead;


    public MatchRankingFragment(IMatch match, String opponentLeft, String opponentRight) {
        this.match = match;
        this.opponentLeft = opponentLeft;
        this.opponentRight = opponentRight;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_ranking, container, false);

        btnMatchMakePredict = view.findViewById(R.id.btnMatchMakePredict);
        btnMatchMakeComment = view.findViewById(R.id.btnMatchMakeComment);
        btnMatchRate = view.findViewById(R.id.btnMatchRate);
        pbMatchPrediction = view.findViewById(R.id.pbMatchPrediction);
        rbMatchRanking = view.findViewById(R.id.rbMatchRanking);
        rlMatchRanking = view.findViewById(R.id.rlMatchRanking);
        rvMatchComment = view.findViewById(R.id.rvMatchComment);
        tvPredictionHead = view.findViewById(R.id.tvPredictionHead);

        if (match.getMatchType() == IMatch.EXTERNAL) {
            rlMatchRanking.setVisibility(View.GONE);
            btnMatchMakePredict.setVisibility(View.GONE);

            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new PredicitonTask(opponentLeft, opponentRight, ((ExternalMatch)match).getBo()), (data) -> {
                Log.i("PLAYER", data.toString());
                try {
                    double prob = data.getDouble("proba");
                    if (prob > 0.5) {
                        Toast.makeText(getContext(), data.getJSONObject("pla").getString("tag"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), data.getJSONObject("plb").getString("tag"), Toast.LENGTH_LONG).show();
                    }
                    leftDistribution = prob * 100;
                    setProgressBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } else {
            rbMatchRanking.setRating((float) (((IRateable)match).getRatingSum()/((IRateable)match).getRatingVotes()));

            btnMatchRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            btnMatchMakePredict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            Pair<Integer, Integer> distribution = ((IPredictable)match).getDistribution();
            leftDistribution = ((double)distribution.first / (distribution.first + distribution.second)) * 100;
            setProgressBar();
        }

        btnMatchMakeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MatchCommentActivity.class);
                i.putExtra("opponent", match.getOpponent());
                i.putExtra("time", match.getTime());
                i.putExtra("type", match.getMatchType());

                getActivity().startActivity(i);
            }
        });

        allComments = new ArrayList<>();
        adapter = new BaseCommentAdapter(getContext(), allComments);

        rvMatchComment.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMatchComment.setAdapter(adapter);

        queryComments();

        return view;
    }

    private void setProgressBar() {
        pbMatchPrediction.setProgress((int) Math.round(leftDistribution));
        if (leftDistribution > 50) {
            tvPredictionHead.setText(opponentLeft + " is predicted as winner!");
        } else if (leftDistribution < 50) {
            tvPredictionHead.setText(opponentRight + " is predicted as winner!");
        } else {
            tvPredictionHead.setText("Fierce fight! Nobody know who will going to win!");
        }
    }

    public void showDialog() {
        AlertDialog.Builder popDialog = new AlertDialog.Builder(getContext());
        RatingBar ratingBar = new RatingBar(getContext());
        RelativeLayout relativeLayout = new RelativeLayout(getContext());

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setMax(5);

        RelativeLayout.LayoutParams ratingBarParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ratingBarParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ratingBar.setLayoutParams(ratingBarParam);
        relativeLayout.addView(ratingBar);

        popDialog.setTitle("Rate this tournament!");
        popDialog.setView(relativeLayout);

        popDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((IRateable)match).setRate((float)ratingBar.getRating());
                        Toast.makeText(getContext(), "Successfully rated match!", Toast.LENGTH_SHORT);
                        dialogInterface.dismiss();
                    }
                });

        popDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        popDialog.create();
        popDialog.show();
    }

    protected void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include("author");
        query.setLimit(5);
        query.whereEqualTo(Comment.KEY_COMMENT_TO, String.format("%s, %s, %d", match.getOpponent(), match.getTime(), match.getMatchType()));
        query.addDescendingOrder("createdAt");

        try {
            allComments.addAll(query.find());
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    public class PredicitonTask implements Callable<JSONObject> {
        private final String input1;
        private final String input2;
        private final Integer bo;

        public PredicitonTask(String input1, String input2, Integer bo) {
            this.input1 = input1;
            this.input2 = input2;
            this.bo = bo;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            int p1 = MainActivity.aligulacClient.getPlayer(input1).getInt("id");
            int p2 = MainActivity.aligulacClient.getPlayer(input2).getInt("id");
            return MainActivity.aligulacClient.getPrediction(p1, p2, bo);
        }
    }

}