package bankzworld.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bankzworld.movies.R;
import bankzworld.movies.activity.DetailsActivity;
import bankzworld.movies.pojo.Results;
import butterknife.BindView;
import butterknife.ButterKnife;

import static bankzworld.movies.util.Config.POSTER_PATH;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Activity mContext;
    private List<Results> resultsList;
    private int lastPosition = -1;

    public MovieAdapter(Activity mContext, List<Results> resultsList) {
        this.mContext = mContext;
        this.resultsList = resultsList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_layout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, final int position) {
        holder.mRating.setText(String.valueOf(resultsList.get(position).getVoteAverage()));

        Glide.with(mContext).load(POSTER_PATH + resultsList.get(position)
                .getPosterPath())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.error_image_placeholder))
                .into(holder.mPoster);

    }


    @Override
    public int getItemCount() {
        if (resultsList == null)
            return 0;
        return resultsList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.poster_image)
        ImageView mPoster;
        @BindView(R.id.text_rating)
        TextView mRating;

        MovieViewHolder(View itemView) {
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
            mContext.startActivity(intent);
            mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void addMovies(List<Results> resultsList) {
        this.resultsList.addAll(resultsList);
        notifyDataSetChanged();
    }
}
