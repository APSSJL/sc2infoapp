package com.example.sc2infoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.IOException;

import fragments.HomeFeedFragment;
import fragments.UserProfileFragment;



public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    public static LiquipediaClient client = new LiquipediaClient();
    public static AligulacClient aligulacClient = new AligulacClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(MainActivity.this, TeamActivity.class);
        i.putExtra("teamName", "Alpha_X");
        startActivity(i);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFeedFragment();
                        Toast.makeText(MainActivity.this,"Home!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_matches:
                        fragment = new UserProfileFragment();
                        Toast.makeText(MainActivity.this,"Matches!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_user:
                    default:
                        fragment = new UserProfileFragment();
                        Toast.makeText(MainActivity.this,"Matches!",Toast.LENGTH_SHORT).show();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        //bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}