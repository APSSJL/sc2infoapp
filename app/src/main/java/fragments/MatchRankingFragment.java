package fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sc2infoapp.AligulacClient;
import com.example.sc2infoapp.R;

import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import interfaces.IMatch;
import interfaces.IPredictable;
import interfaces.IRateable;

public class MatchRankingFragment extends Fragment {

    IMatch match;
    double leftDistribution;

    Button btnMatchMakePredict;
    Button btnMatchMakeComment;
    ProgressBar pbMatchPrediction;
    RatingBar rbMatchRanking;
    RelativeLayout rlMatchRanking;
    RecyclerView rvMatchComment;
    TextView tvPredictionHead;

    public MatchRankingFragment(IMatch match) {
        this.match = match;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_ranking, container, false);

        btnMatchMakePredict = view.findViewById(R.id.btnMatchMakePredict);
        btnMatchMakeComment = view.findViewById(R.id.btnMatchMakeComment);
        pbMatchPrediction = view.findViewById(R.id.pbMatchPrediction);
        rbMatchRanking = view.findViewById(R.id.rbMatchRanking);
        rlMatchRanking = view.findViewById(R.id.rlMatchRanking);
        rvMatchComment = view.findViewById(R.id.rvMatchComment);
        tvPredictionHead = view.findViewById(R.id.tvPredictionHead);

        String opponentLeft = match.getOpponent().split("vs")[0];
        String opponentRight = match.getOpponent().split("vs")[1];

        if (match.getMatchType() == IMatch.EXTERNAL) {
            rlMatchRanking.setVisibility(View.GONE);
            btnMatchMakePredict.setVisibility(View.GONE);

            AligulacClient client = new AligulacClient();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            Callable<Double> callable = new Callable<Double>() {
                @Override
                public Double call() throws Exception {
                    JSONObject result = client.getPrediction(client.getPlayer(opponentLeft).getInt("id"), client.getPlayer(opponentRight).getInt("id"), 1);
                    return result.getDouble("proba");
                }
            };
            Future<Double> future = executor.submit(callable);
            try {
                leftDistribution = future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            rbMatchRanking.setRating((float) (((IRateable)match).getRatingSum()/((IRateable)match).getRatingVotes()));
            btnMatchMakePredict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            Pair<Integer, Integer> distribution = ((IPredictable)match).getDistribution();
            leftDistribution = (distribution.first / (distribution.first + distribution.second)) * 100;

        }

        pbMatchPrediction.setProgress((int) Math.round(leftDistribution));
        if (leftDistribution > 50) {
            tvPredictionHead.setText(opponentLeft + "is predicted as winner!");
        } else if (leftDistribution < 50) {
            tvPredictionHead.setText(opponentRight + "is predicted as winner!");
        } else {
            tvPredictionHead.setText("Fierce fight! Nobody know who will going to win!");
        }

        btnMatchMakeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: send user to new comment screen
            }
        });

        //TODO: Set Recycler View adapter for rvMatchComment

        return view;
    }

    public void showDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(getActivity());
        final RatingBar ratingBar = new RatingBar(getActivity());
        ratingBar.setMax(5);

        popDialog.setTitle("Rate this match!");
        popDialog.setView(ratingBar);

        popDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((IRateable)match).setRate((float)ratingBar.getRating());
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
}