package fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import com.example.sc2infoapp.IPublished;
import com.example.sc2infoapp.Post;
import com.example.sc2infoapp.PostComposeActivity;
import com.example.sc2infoapp.R;
import com.example.sc2infoapp.Tournament;
import com.example.sc2infoapp.UserFeedAdapter;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFeedFragment extends Fragment {
    private static final String TAG = "Home feed fragemnt";
    Button btnCreatePost;
    RecyclerView rvFeed;
    UserFeedAdapter adapter;
    List<IPublished> published;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateUserFeed() {
        ParseQuery<Tournament> query1 =  ParseQuery.getQuery(Tournament.class);
        query1.include("userCreated");
        query1.include("userCreated.organizer");
        query1.setLimit(5);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.addDescendingOrder("created_at");

        try {
            published.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            List<Tournament> x = query1.find();
            x.removeIf(t -> !(t.getUserCreated().getOrganizer().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())));
            Log.i(TAG, String.valueOf(x.size()));
            published.addAll(x);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Collections.sort(published, new Comparator<IPublished>() {
            @Override
            public int compare(IPublished lhs, IPublished rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return rhs.getCreatedAt().compareTo(lhs.getCreatedAt());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        published.clear();
        populateUserFeed();
    }
}
