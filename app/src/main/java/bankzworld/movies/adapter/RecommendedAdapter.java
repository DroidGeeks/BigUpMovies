package bankzworld.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bankzworld.movies.R;
import bankzworld.movies.activity.DetailsActivity;
import bankzworld.movies.pojo.Results;
import butterknife.BindView;
import butterknife.ButterKnife;

import static bankzworld.movies.util.Config.POSTER_PATH;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.RecommendedViewHolder> {

    private Activity mContext;
    private List<Results> resultsList;
    private int lastPosition = -1;

    public RecommendedAdapter(Activity mContext, List<Results> resultsList) {
        this.mContext = mContext;
        this.resultsList = resultsList;
    }

    @NonNull
    @Override
    public RecommendedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recommendation_layout, parent, false);
        return new RecommendedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedViewHolder holder, int position) {
        holder.mName.setText(resultsList.get(position).getOriginalTitle());
        holder.mRating.setText(resultsList.get(position).getVoteAverage().toString());

        Picasso.get()
                .load(POSTER_PATH + resultsList.get(position).getPosterPath())
                .error(R.drawable.error_image_placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.mPoster);

        // sets an animation for the recyclerView
        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.slide_in_left
                        : R.anim.slide_in_right);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        if (resultsList == null)
            return 0;
        return resultsList.size();
    }

    class RecommendedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recom_poster)
        ImageView mPoster;
        @BindView(R.id.recom_movie_name)
        TextView mName;
        @BindView(R.id.recom_rating)
        TextView mRating;

        public RecommendedViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, DetailsActivity.class);
            Results data = resultsList.get(getLayoutPosition());
            intent.putExtra("data", data);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        mContext,
                        new Pair<>(view.findViewById(R.id.poster_image), "image"),
                        new Pair<>(view.findViewById(R.id.text_rating), "rating"));
                ActivityCompat.startActivity(context, intent, activityOptions.toBundle());
            }
        }
    }
}
