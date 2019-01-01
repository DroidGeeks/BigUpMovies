package bankzworld.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bankzworld.movies.R;
import bankzworld.movies.activity.TrailerDownloader;
import bankzworld.movies.activity.YoutubeActivity;
import bankzworld.movies.pojo.TrailerResult;
import butterknife.BindView;
import butterknife.ButterKnife;

import static bankzworld.movies.util.Config.YOU_TUBE_BASE_URL;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String TAG = "TrailerAdapter";

    private Activity mContext;
    private List<TrailerResult> trailerResults;
    private int lastPosition = -1;

    public TrailerAdapter(Activity context, List<TrailerResult> trailerResults) {
        this.mContext = context;
        this.trailerResults = trailerResults;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_list, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {

        Glide.with(mContext).load(YOU_TUBE_BASE_URL + trailerResults.get(position).getKey() + "/0.jpg")
                .apply(new RequestOptions().placeholder(R.drawable.trailer_placeholder).error(R.drawable.error_image_placeholder))
                .into(holder.mTrailerPoster);

        // sets an animation for the recyclerView
        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.up
                        : R.anim.down);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public int getItemCount() {
        if (trailerResults == null)
            return 0;
        return trailerResults.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_image)
        ImageView mTrailerPoster;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            selection(context);
        }

        private void selection(final Context context) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you wish to watch this trailer or download it for later view?");
            builder.setPositiveButton("Play Trailer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(context, YoutubeActivity.class);
                    TrailerResult key = trailerResults.get(getLayoutPosition());
                    intent.putExtra("key", key);
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            builder.setNegativeButton("Download Trailer", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(context, TrailerDownloader.class);
                    TrailerResult key = trailerResults.get(getLayoutPosition());
                    intent.putExtra("key", key);
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            builder.show();
        }

    }
}
