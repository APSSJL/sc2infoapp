package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sc2infoapp.R;

public class MatchH2HFragment extends Fragment {

    RecyclerView rvPreviousMeeting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_h2h, container, false);

        rvPreviousMeeting = view.findViewById(R.id.rvPreviousMeeting);
        //TODO: Recylcer View adapter for previous matches

        return view;
    }
}