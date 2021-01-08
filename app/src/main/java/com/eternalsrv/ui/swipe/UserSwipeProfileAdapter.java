package com.eternalsrv.ui.swipe;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.ProfileActivity;
import com.eternalsrv.utils.ImageLoader;

public class UserSwipeProfileAdapter extends ArrayAdapter<UserProfileInfo> {
    public static final String EXTRA_USER_PROFILE = "987";
    public static final String EXTRA_SWIPE_VIEW_SOURCE = "420";
    private final ImageLoader imageLoader;
    private final FragmentActivity fragmentActivity;

    public UserSwipeProfileAdapter(Context context, FragmentActivity fragmentActivity) {
        super(context, 0);
        this.fragmentActivity = fragmentActivity;
        this.imageLoader = App.getImageLoader();
    }

    @NonNull
    @Override
    public View getView(int position, View contentView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.user_swipe_card, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        final UserProfileInfo profile = getItem(position);

        holder.name.setText(String.format("%s, %d", profile.getName(), profile.getAge()));
        holder.name.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                View cardView = fragmentActivity.findViewById(R.id.user_swipe_card_image);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(fragmentActivity,
                                Pair.create(cardView, "user_swipe_image_transition"));
                fragmentActivity.startActivity(getBundledIntent(profile), options.toBundle());
            }
        });

        imageLoader.downloadImage(profile.getPhotoLinks().get(0), holder.image);

        return contentView;
    }

    public UserProfileInfo getProfileByUserId(int userId) {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getUserId() == userId) {
                return getItem(i);
            }
        }
        return null;
    }

    private Intent getBundledIntent(UserProfileInfo profile) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(EXTRA_USER_PROFILE, profile);
        intent.putExtra(EXTRA_SWIPE_VIEW_SOURCE, true);
        return intent;
    }

    private static class ViewHolder {
        public TextView name;
        public ImageView image;

        ViewHolder(View view) {
            this.name = view.findViewById(R.id.user_swipe_card_name);
            this.image =  view.findViewById(R.id.user_swipe_card_image);
        }
    }
}

