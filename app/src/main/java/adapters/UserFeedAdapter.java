package adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import interfaces.IMatch;
import interfaces.IPublished;
import models.Post;

import com.example.sc2infoapp.MatchDetailActivity;
import models.Notification;
import com.example.sc2infoapp.PlayerActivity;
import com.example.sc2infoapp.PostDetailActivity;
import com.example.sc2infoapp.R;

import models.Tournament;
import models.UserInfo;
import models.UserTournament;

import com.example.sc2infoapp.TeamActivity;
import com.example.sc2infoapp.ViewProfileActivity;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.io.File;
import java.util.List;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.ItemViewHolder> {

    List<IPublished> published;
    Context context;
    Activity activity;

    public UserFeedAdapter(Context context, List<IPublished> published) {
        this.context = context;
        this.published = published;
        activity = (Activity)context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IPublished.POST) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }
        if (viewType == IPublished.TOURNAMENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tournament, parent, false);
            return new TournamentViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_summary, parent, false);
            return new SummaryViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return published.get(position).getPublishedType();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(published.get(position));
    }

    @Override
    public int getItemCount() {
        return published.size();
    }

    class PostViewHolder extends ItemViewHolder {
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
        public void bind(final IPublished published) {
            Post post = (Post) published;
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, PostDetailActivity.class);
                    i.putExtra("post", Parcels.wrap(post));
                    activity.startActivity(i);
                }
            });
            tvAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity, ViewProfileActivity.class);
                    i.putExtra("user", post.getUser());
                    activity.startActivity(i);
                }
            });
            tvTitle.setText(post.getTitle());
            tvcategory.setText(post.getCategory());
            tvAuthor.setText(post.getAuthor());
            tvContent.setText(post.getContent());
            tvTags.setText(String.join(",", post.getTags()));
        }
    }

    class SummaryViewHolder extends ItemViewHolder {
        TextView tvTitle;
        ImageView ivLogo;
        TextView tvAuthor;
        TextView tvDescription;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        @Override
        public void bind(IPublished published) {
            tvTitle.setText(published.getTitle());
            tvAuthor.setText(published.getAuthor());
            tvDescription.setText(published.getContent());
            if(published.getPublishedType() == IPublished.PLAYER_SUMMARY)
                tvDescription.setVisibility(View.GONE);
            File p = published.getImage();
            if (p != null) {
                Glide.with(context).load(p).transform(new CircleCrop()).into(ivLogo);
            } else {
                if(published.getPublishedType() == IPublished.NOTIFICATION)
                    ivLogo.setBackgroundResource(((Notification)published).getResource());
                else Glide.with(context).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivLogo);
            }

            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (published.getPublishedType())
                    {
                        case IPublished.PLAYER_SUMMARY:
                            Intent i = new Intent(activity, PlayerActivity.class);
                            i.putExtra("playerName", published.getTitle());
                            activity.startActivity(i);
                            break;
                        case IPublished.TEAM_SUMMARY:
                            i = new Intent(activity, TeamActivity.class);
                            i.putExtra("teamName", published.getTitle());
                            activity.startActivity(i);
                            break;
                        case IPublished.USER_SUMMARY:
                            i = new Intent(activity, ViewProfileActivity.class);
                            i.putExtra("user", ((UserInfo)published).getUser());
                            activity.startActivity(i);
                            break;
                        case IPublished.MATCH_SUMMARY:
                            i = new Intent(activity, MatchDetailActivity.class);
                            i.putExtra("match", Parcels.wrap((IMatch)published));
                            activity.startActivity(i);
                            break;
                        case IPublished.NOTIFICATION:
                            ((Notification)published).Enforce();
                        default:
                            break;
                    }
                }
            });

        }
    }

    class TournamentViewHolder extends ItemViewHolder {
        TextView tvTitle;
        ImageView ivLogo;
        TextView tvAuthor;
        TextView tvDescription;
        TextView tvRating;

        public TournamentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

        public void bind(final IPublished published) {
            Tournament tournament = (Tournament) published;
            tvTitle.setText(tournament.getName());
            tvRating.setText(String.format("%d/10", tournament.getRating()));
            UserTournament ut = tournament.getUserCreated();
            tvAuthor.setText(String.format("by %s", ut.getOrganizer().getUsername()));
            tvDescription.setText(ut.getDescription());
            try {
                ParseFile p = (ut.getParseFile("logo"));
                if (p != null) {
                    Glide.with(context).load(p.getFile()).transform(new CircleCrop()).into(ivLogo);
                } else {
                    Glide.with(context).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivLogo);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    abstract class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract public void bind(final IPublished published);
    }
}
