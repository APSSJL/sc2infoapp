package com.example.sc2infoapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import adapters.BaseCommentAdapter;
import models.Comment;

import static com.parse.ParseUser.getCurrentUser;

public class BaseCommentActivity extends AppCompatActivity {

    public static final String TAG = "BASE_COMMENT_ACTIVITY";
    RecyclerView rvComments;
    EditText etComment;
    Button btnPostComment;
    protected String info;
    public TextView tvInfo;

    List<Comment> allComments;
    BaseCommentAdapter adapter;
    ParseUser user;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base_comments);
        //tvInfo = findViewById(R.id.tvInfo);
        //tvInfo.setText(info);

        user = getCurrentUser();

        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);


        allComments = new ArrayList<>();
        adapter = new BaseCommentAdapter(this, allComments);

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        queryComments();



        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etComment.getText().toString();

                if (content.isEmpty()){
                    Toast.makeText(BaseCommentActivity.this,"Comment cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    Comment comment = new Comment();
                    Log.i(TAG, content);
                    comment.setAuthor(user);
                    comment.setContent(content);

                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while posting", e);
                                Toast.makeText(BaseCommentActivity.this, "Error while commenting", Toast.LENGTH_SHORT).show();
                            }
                            allComments.add(0, comment);
                            adapter.notifyItemInserted(0);
                            etComment.setText("");

                            Log.i(TAG, "commented successfully");
                        }
                    });
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include("author");
        query.setLimit(5);
        query.addDescendingOrder("createdAt");


        try {
            allComments.addAll(query.find());
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        Log.i(TAG, allComments.get(1).getContent());
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        allComments.clear();
        queryComments();
    }
}
