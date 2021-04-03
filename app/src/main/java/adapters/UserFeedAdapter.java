package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import interfaces.IPublished;
import models.Post;
import com.example.sc2infoapp.R;
import models.Tournament;
import models.UserTournament;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.ItemViewHolder>{

    List<IPublished> published;
    Context context;

    public  UserFeedAdapter(Context context, List<IPublished> published)
    {
        this.context = context;
        this.published = published;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == IPublished.POST){
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }
        if(viewType == IPublished.TOURNAMENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tournament, parent, false);
            return new TournamentViewHolder(view);
        }
        return null;
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

    class PostViewHolder extends ItemViewHolder
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
        public void bind(final IPublished published)
        {
            Post post = (Post)published;
            tvTitle.setText(post.getTitle());
            tvcategory.setText(post.getCategory());
            tvAuthor.setText(post.getAuthor().getUsername());
            tvContent.setText(post.getContent());
            tvTags.setText(String.join(",", post.getTags()));
        }
    }

    class TournamentViewHolder extends ItemViewHolder
    {
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

        public void bind(final IPublished published)
        {
            Tournament tournament = (Tournament)published;
            tvTitle.setText(tournament.getName());
            tvRating.setText(String.format("%d/10", tournament.getRating()));
            UserTournament ut = tournament.getUserCreated();
            tvAuthor.setText(String.format("by %s", ut.getOrganizer().getUsername()));
            tvDescription.setText(ut.getDescription());
            try {
                ParseFile p = (ut.getParseFile("logo"));
                if(p != null)
                {
                    Glide.with(context).load(p.getFile()).transform(new CircleCrop()).into(ivLogo);
                }
                else {
                    Glide.with(context).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivLogo);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    abstract class ItemViewHolder extends RecyclerView.ViewHolder
    {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract public void bind(final IPublished published);
    }
}
