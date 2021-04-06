package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sc2infoapp.R;

import interfaces.IMatch;

public class MatchRankingFragment extends Fragment {

    IMatch match;

    Button btnMatchMakePredict;
    Button btnMatchMakeComment;
    ProgressBar pbMatchPrediction;
    RatingBar rbMatchRanking;
    RelativeLayout rlMatchRanking;
    RecyclerView rvMatchComment;

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

        if (match.getMatchType() == 1) {
            rlMatchRanking.setVisibility(View.GONE);
        } else {

            btnMatchMakePredict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnMatchMakePredict.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        btnMatchMakeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: send user to new comment screen
            }
        });
        return view;
    }
}