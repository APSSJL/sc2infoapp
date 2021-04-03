package fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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
import com.example.sc2infoapp.CreateTeamActivity;
import interfaces.IPublished;
import models.Post;

import com.example.sc2infoapp.LoginActivity;
import com.example.sc2infoapp.R;
import models.Team;
import com.example.sc2infoapp.TeamActivity;
import models.Tournament;
import com.example.sc2infoapp.UpdateProfileActivity;
import adapters.UserFeedAdapter;
import models.UserInfo;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class UserProfileFragment extends Fragment {

    private static final String TAG = "User profile";
    TextView tvName;
    TextView tvRace;
    ImageView ivPicture;
    TextView tvLocation;
    TextView tvMmr;
    TextView tvBio;
    LocationManager locationManager;
    RecyclerView rvItems;
    Button btnEditProfile;
    TextView tvTeam;
    Button btnTeam;
    Button btnCreateTeam;
    ParseUser user;
    Button btnLogout;
    UserFeedAdapter adapter;
    List<IPublished> published;
    Team team;

    public UserProfileFragment() {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        published = new ArrayList<>();
        tvName = view.findViewById(R.id.tvName);
        btnLogout = view.findViewById(R.id.btnLogout);
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

        setupUserInfo();

    }

    @Override
    public void onResume() {
        super.onResume();
        published.clear();
        teamButtonSetup();
        setupUserInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupUserInfo()
    {
        user = ParseUser.getCurrentUser();
        try {
            user.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvName.setText(user.getUsername());
        tvRace.setText(user.getString("inGameRace"));
        tvMmr.setText(String.valueOf(user.getInt("MMR")));
        tvBio.setText(user.getString("userInfo"));


        try {
            ParseFile p = (user.getParseFile("pic"));
            if (p != null) {
                Log.i(TAG, "loaded");
                Glide.with(this).load(p.getFile()).transform(new CircleCrop()).into(ivPicture);
            } else {
                Log.i(TAG, "null");
                Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivPicture);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UpdateProfileActivity.class);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        teamButtonSetup();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvItems.setLayoutManager(manager);
        rvItems.setAdapter(adapter);

        populateUserFeed(ParseUser.getCurrentUser(), published, adapter);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.i(TAG, "fail");
            return;
        }
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
    }

    private void teamButtonSetup()
    {

            try {

                user.fetch();
                UserInfo info = (UserInfo) user.getParseObject("Additional");
                if(info != null) {
                    info.fetch();
                    team = (Team) info.get("team");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        if (team == null) {
            tvTeam.setText("No team");
            setBtnJoin();
        } else {
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
                    Intent i = new Intent(getActivity(), TeamActivity.class);
                    i.putExtra("teamName", team.getTeamName());
                    startActivity(i);
                }
            });
            btnTeam.setText("Leave team");

            btnTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (team.getOwner() == ParseUser.getCurrentUser()) {
                        Toast.makeText(getContext(), "Owner cannot leave team", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    user.remove("team");
                    user.saveInBackground();
                    setBtnJoin();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void populateUserFeed(ParseUser currentUser, List<IPublished> published, UserFeedAdapter adapter) {
        ParseQuery<Tournament> query1 =  ParseQuery.getQuery(Tournament.class);
        query1.include("userCreated");
        query1.include("userCreated.organizer");
        query1.setLimit(5);

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(5);
        query.whereEqualTo(Post.KEY_AUTHOR, currentUser);
        query.addDescendingOrder("createdAt");

        try {
            published.addAll(query.find());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            List<Tournament> x = query1.find();
            x.removeIf(t -> !(t.getUserCreated().getOrganizer().getObjectId().equals(currentUser.getObjectId())));
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

    private void setBtnJoin() {
        btnTeam.setText("Join team");
        btnCreateTeam.setVisibility(View.VISIBLE);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateTeamActivity.class);
                startActivity(i);
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

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            tvLocation.setText(getAddress(getContext(),location.getLatitude(), location.getLongitude()));
        }
    };

    public static String getAddress(Context context, double LATITUDE, double LONGITUDE) {

//Set Address
        try {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {


                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Log.d(TAG, "getAddress:  address" + address);
                Log.d(TAG, "getAddress:  city" + city);
                Log.d(TAG, "getAddress:  state" + state);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);
                return city + ", " + country;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }
}