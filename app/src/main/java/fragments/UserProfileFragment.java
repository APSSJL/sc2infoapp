package fragments;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.sc2infoapp.IPublished;
import com.example.sc2infoapp.Post;
import com.example.sc2infoapp.R;
import com.example.sc2infoapp.Team;
import com.example.sc2infoapp.UserFeedAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class UserProfileFragment extends Fragment {

    private static final String TAG = "User profile";
    TextView tvName;
    TextView tvRace;
    ImageView ivPicture;
    TextView tvLocation;
    TextView tvMmr;
    TextView tvBio;
    RecyclerView rvItems;
    Button btnEditProfile;
    TextView tvTeam;
    Button btnTeam;
    Button btnCreateTeam;
    ParseUser user;
    UserFeedAdapter adapter;
    List<IPublished> published;
    Team team;

    public UserProfileFragment() {
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        published = new ArrayList<>();
        tvName = view.findViewById(R.id.tvName);
        tvRace = view.findViewById(R.id.tvRace);
        ivPicture = view.findViewById(R.id.ivPicture);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvMmr = view.findViewById(R.id.tvMmr);
        tvBio = view.findViewById(R.id.tvBio);
        rvItems = view.findViewById(R.id.rvItems);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvTeam = view.findViewById(R.id.tvTeam);
        btnTeam = view.findViewById(R.id.btnTeam);
        btnCreateTeam = view.findViewById(R.id.btnCreateTeam);
        adapter = new UserFeedAdapter(getContext(), published);
        user = ParseUser.getCurrentUser();
        try {
            user.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvName.setText(user.getUsername());
        tvRace.setText(user.getString("inGameRace"));
        tvMmr.setText(user.get("MMR").toString());
        tvBio.setText(user.getString("userInfo"));


        try {
            ParseFile p = ((ParseFile) user.get("pic"));
            if(p != null && p == null)
            {
                Log.i(TAG, "loaded");
                Glide.with(this).load(p.getFile()).transform(new CircleCrop()).into(ivPicture);
            }
            else {
                Log.i(TAG, "null");
                Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivPicture);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : navigate to editProfile screen
            }
        });
        team = (Team) user.get("team");

        if(team == null)
        {
            tvTeam.setText("No team");
            setBtnJoin();
        }
        else
        {
            try {
                team.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            btnCreateTeam.setVisibility(View.GONE);
            tvTeam.setText(team.getTeamName());
            tvTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: navigate to team info page
                }
            });
            btnTeam.setText("Leave team");
            btnTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(team.getOwner() == ParseUser.getCurrentUser())
                    {
                        Toast.makeText(getContext(), "Owner cannot leave team", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.remove("team");
                    user.saveInBackground();
                    setBtnJoin();
                }
            });
        }

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvItems.setLayoutManager(manager);
        rvItems.setAdapter(adapter);

        populateUserFeed();
    }

    private void populateUserFeed() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(5);
        query.whereEqualTo(Post.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.addDescendingOrder("created_at");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null)
                {
                    Log.e(TAG, "Something went wrong", e);
                    return;
                }
                published.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setBtnJoin() {
        btnTeam.setText("Join team");
        btnCreateTeam.setVisibility(View.VISIBLE);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : REDIRECT TO TEAM CREATION PAGE
                Log.i(TAG, "team create");
            }
        });
        btnTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "team join");
                // TODO : redirect to team search page
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }
}