package com.example.sc2infoapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.PostViewHolder>{

    List<IPublished> published;
    Context context;

    public  UserFeedAdapter(Context context, List<IPublished> published)
    {}
    

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return published.get(position).getPublishedType();
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind((Post)published.get(position));
    }

    @Override
    public int getItemCount() {
        return published.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        TextView tvcategory;
        TextView tvAuthor;
        TextView tvContent;
        TextView tvTags;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvcategory = itemView.findViewById(R.id.tvCategory);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTags = itemView.findViewById(R.id.tvTags);
        }

        @SuppressLint("NewApi")
        public void bind(final Post post)
        {
            tvTitle.setText(post.getTitle());
            tvcategory.setText(post.getCategory());
            tvAuthor.setText(post.getAuthor().getUsername());
            tvContent.setText(post.getContent());
            tvTags.setText(String.join(",", post.getTags()));
        }
    }
}
