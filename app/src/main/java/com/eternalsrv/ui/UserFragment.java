package com.eternalsrv.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.models.QBUserCustomData;
import com.eternalsrv.utils.ImageLoader;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.SharedPrefsHelper;

import com.google.gson.Gson;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment{
    public static final int REQUEST_SETTINGS_CODE = 231;

    private final MyPreferences myPreferences = App.getPreferences();

    private ImageLoader imageLoader;
    private CircleImageView profilePhoto;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.imageLoader = App.getImageLoader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        ImageView settingsImage = view.findViewById(R.id.settings);
        ImageView profileEditImage = view.findViewById(R.id.profile_edit_image);
        TextView profileName = view.findViewById(R.id.fragment_user_profile_name);
        profilePhoto = view.findViewById(R.id.fragment_user_profile_image);

        QBUserCustomData userCustomData = new Gson().fromJson(SharedPrefsHelper.getInstance().getQbUser().getCustomData(), QBUserCustomData.class);
        if (userCustomData != null) {
            if (userCustomData.getProfilePhotoData().size() > 0) {
                String link = userCustomData.getProfilePhotoData().get(0).getLink();
                imageLoader.downloadImage(link, profilePhoto);
            }
        }
        String age = "";
        if (myPreferences.getBirthday() != null) {
            age = ", " + (new Date().getYear() - myPreferences.getBirthday().getYear());
        }
        profileName.setText(String.format("%s%s", myPreferences.getFirstName(), age));

        settingsImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_SETTINGS_CODE);
            }
        });

        profileEditImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        QBUserCustomData userCustomData = new Gson().fromJson(SharedPrefsHelper.getInstance().getQbUser().getCustomData(), QBUserCustomData.class);
        if (userCustomData != null) {
            if (userCustomData.getProfilePhotoData().size() > 0) {
                String link = userCustomData.getProfilePhotoData().get(0).getLink();
                imageLoader.downloadImage(link, profilePhoto);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
