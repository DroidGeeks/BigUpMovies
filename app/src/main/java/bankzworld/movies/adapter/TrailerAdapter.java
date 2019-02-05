package bankzworld.movies.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import bankzworld.movies.R;
import bankzworld.movies.activity.DetailsActivity;
import bankzworld.movies.activity.TrailerDownloader;
import bankzworld.movies.activity.YoutubeActivity;
import bankzworld.movies.pojo.TrailerResult;
import bankzworld.movies.util.Config;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.DOWNLOAD_SERVICE;
import static bankzworld.movies.util.Config.DlFolder;
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

                    TrailerResult key = trailerResults.get(getLayoutPosition());
                    String link = "https://www.youtube.com/watch?v="+key.getKey();

                    Log.i("YTLINK", link);
                    String name = key.getName();
                    downloadTrailer(link, name);



//                    Intent intent = new Intent(context, TrailerDownloader.class);
//
//                    intent.putExtra("key", key);
//                    mContext.startActivity(intent);
//                    mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            builder.show();
        }


        //------------------------Download a Video Trailer----------------------//

        public void downloadTrailer(String url, final String name){
            @SuppressLint("StaticFieldLeak") YouTubeExtractor mExtractor = new YouTubeExtractor(mContext) {
                @Override
                protected void onExtractionComplete(SparseArray<YtFile> sparseArray, VideoMeta videoMeta) {
                    if (sparseArray != null) {

                        List<Integer> iTags = Arrays.asList(22, 137, 18, 17);

                        for (Integer iTag : iTags) {

                            YtFile ytFile = sparseArray.get(iTag);

                            if (ytFile != null) {

                                String downloadUrl = ytFile.getUrl();

                                if (downloadUrl != null && !downloadUrl.isEmpty()) {

                                    Uri youtubeUri = Uri.parse(downloadUrl);

                                    File root = new File(DlFolder);
                                    if (!root.exists()) {
                                        root.mkdirs();
                                    }

                                    String t = name+"Trailer";

                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(String.valueOf(youtubeUri)));
                                    request.setDescription("Downloading "+ t);
                                    request.setTitle("BigUp");
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                    String filename = t;


                                    request.setDestinationInExternalPublicDir("/BigUp/", filename + ".mp4");

                                    DownloadManager manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                    Toast.makeText(mContext, "Downloading...", Toast.LENGTH_SHORT).show();
                                    return;

                                }

                            }

                        }

                    }
                }
            };

            mExtractor.extract(url, true, true);


        }

    }
}
