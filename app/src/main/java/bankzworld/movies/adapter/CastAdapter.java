package bankzworld.movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bankzworld.movies.R;
import bankzworld.movies.pojo.Cast;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static bankzworld.movies.util.Config.PROFILE_PATH;


public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private static final String TAG = "CastAdapter";
    private Context mContext;
    private List<Cast> castsList;

    public CastAdapter(Context mContext, List<Cast> castsList) {
        this.mContext = mContext;
        this.castsList = castsList;
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
        Picasso.get()
                .load(PROFILE_PATH + castsList.get(position).getProfilePath())
                .error(R.drawable.cast_profile_error)
                .placeholder(R.drawable.cast_profile_placeholder)
                .into(holder.mCastImage);
    }

    @Override
    public int getItemCount() {
        if (castsList == null)
            return 0;
        return castsList.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cast_image)
        CircleImageView mCastImage;
        @BindView(R.id.cast_name)
        TextView mCastName;
        @BindView(R.id.cast_as_name)
        TextView mCastAsName;

        public CastViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
