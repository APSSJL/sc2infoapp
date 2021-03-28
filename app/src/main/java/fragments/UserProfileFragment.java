package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.sc2infoapp.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.w3c.dom.Text;


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

    public UserProfileFragment() {
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        user = ParseUser.getCurrentUser();
        tvName.setText(user.getUsername());
        tvRace.setText(user.getString("inGameRace"));
        tvMmr.setText(user.get("MMR").toString());
        tvBio.setText(user.getString("userInfo"));

        try {
            ParseFile p = ((ParseFile) user.get("pic"));
            if(p != null || p == null)
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }
}