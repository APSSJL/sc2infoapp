package com.example.sc2infoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import fragments.HomeFeedFragment;
import fragments.MatchFeedFragment;
import fragments.UserProfileFragment;
import interfaces.NotificationDao;
import models.ExternalMatch;
import models.ExternalMatchNotification;


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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


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
                        fragment = new MatchFeedFragment();
                        Toast.makeText(MainActivity.this,"Matches!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_user:
                    default:
                        fragment = new UserProfileFragment();
                        Toast.makeText(MainActivity.this,"User Profile!",Toast.LENGTH_SHORT).show();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        ExternalMatch m = new ExternalMatch("SKillous vs Serral", "April 08, 2021 - 22:12 EDT", 3);
        ExternalMatchNotification not = new ExternalMatchNotification(m);

        final NotificationDao userDao = ((ParseApplication) getApplicationContext()).getDB().notDao();

        Thread t = new Thread(() -> {
            ((ParseApplication) getApplicationContext()).getDB().runInTransaction(new Runnable() {
                @Override
                public void run() {
                    //userDao.insertNotification(not);
                    ExternalMatchNotification x = userDao.selectUpcoming("2021-04-10 12:12 EDT");
                    Log.i("ATG", "got some data!");
                }
            });
        });
        t.start();

        //bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}