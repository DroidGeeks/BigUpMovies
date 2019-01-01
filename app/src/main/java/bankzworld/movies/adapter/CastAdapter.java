package bankzworld.movies.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import javax.inject.Inject;

import bankzworld.movies.R;
import bankzworld.movies.injection.DaggerApplication;
import bankzworld.movies.network.Server;
import bankzworld.movies.pojo.Cast;
import bankzworld.movies.pojo.CastDetails;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static bankzworld.movies.util.Config.API_KEY;
import static bankzworld.movies.util.Config.POSTER_PATH;
import static bankzworld.movies.util.Config.PROFILE_PATH;


public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private static final String TAG = "CastAdapter";
    private Context mContext;
    private List<Cast> castsList;

    ProgressDialog progressDialog;

    @Inject
    Retrofit retrofit;
    Server server;

    public CastAdapter(Context mContext, List<Cast> castsList) {
        this.mContext = mContext;
        this.castsList = castsList;
        ((DaggerApplication) mContext.getApplicationContext()).getAppComponent().inject(this);
        server = retrofit.create(Server.class);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("loading");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cast_layout, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        holder.mCastName.setText(castsList.get(position).getName());
        holder.mCastAsName.setText(castsList.get(position).getCharacter());

        Glide.with(mContext).load(PROFILE_PATH + castsList.get(position).getProfilePath())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.cast_profile_error)
                        .error(R.drawable.cast_profile_placeholder))
                .into(holder.mCastImage);

    }

    @Override
    public int getItemCount() {
        if (castsList == null)
            return 0;
        return castsList.size();
    }

    private void characterDetails(String name, String birthday, Object deathday,
                                  String placeOfBirth, String poster, Double popularity, String bio, List<String> alsoKnownAs) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(name);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View character_layout = inflater.inflate(R.layout.character_details, null);

        final CircleImageView circleImageView = character_layout.findViewById(R.id.profile_pic);
        final TextView mActor = character_layout.findViewById(R.id.actor_name);
        final TextView mBday = character_layout.findViewById(R.id.actor_bday);
        final TextView mDeath = character_layout.findViewById(R.id.actor_dday);
        final TextView mPlace = character_layout.findViewById(R.id.actor_place_of_birth);
        final TextView mPopularity = character_layout.findViewById(R.id.actor_popularity);
        final TextView mBiography = character_layout.findViewById(R.id.txt_biography);
        final TextView mKnownAs = character_layout.findViewById(R.id.known_as);

        Glide.with(mContext).load(PROFILE_PATH + poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error_image_placeholder))
                .into(circleImageView);

        mActor.setText("Name: " + name);
        mBday.setText("Birth: " + birthday);
        mDeath.setText("Death: " + String.valueOf(deathday));
        mPlace.setText("Place of Birth: " + placeOfBirth);
        mPopularity.setText("Popularity: " + popularity.toString());
        mBiography.setText(bio);
        for (int i = 0; i < alsoKnownAs.size(); i++) {
            mKnownAs.append(alsoKnownAs.get(i) + "\n");
        }
        dialog.setView(character_layout);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class CastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cast_image)
        CircleImageView mCastImage;
        @BindView(R.id.cast_name)
        TextView mCastName;
        @BindView(R.id.cast_as_name)
        TextView mCastAsName;

        public CastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Cast data = castsList.get(getLayoutPosition());
            getCharacterDetails(data.getId(), API_KEY);
        }

        public void getCharacterDetails(int id, String key) {
            progressDialog.show();
            server.getCharacterId(id, key).enqueue(new Callback<CastDetails>() {
                @Override
                public void onResponse(Call<CastDetails> call, Response<CastDetails> response) {
                    if (response.isSuccessful()) {
                        characterDetails(
                                response.body().getName(),
                                response.body().getBirthday(),
                                response.body().getDeathday(),
                                response.body().getPlaceOfBirth(),
                                response.body().getProfilePath(),
                                response.body().getPopularity(),
                                response.body().getBiography(),
                                response.body().getAlsoKnownAs());
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<CastDetails> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    progressDialog.dismiss();
                }
            });
        }

    }
}
