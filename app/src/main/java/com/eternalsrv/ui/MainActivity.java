package com.eternalsrv.ui;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

import com.eternalsrv.R;
import com.eternalsrv.models.QBUserCustomData;
import com.eternalsrv.ui.chat.DialogsFragment;
import com.eternalsrv.ui.interfaces.OnChangeViewListener;
import com.eternalsrv.ui.interfaces.OnLoginChangeView;
import com.eternalsrv.ui.swipe.UserProfileInfo;
import com.eternalsrv.utils.GPSLocationListener;
import com.eternalsrv.utils.SharedPrefsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnChangeViewListener, SwipeFragment.OnMatchCreated, MatchDialogFragment.MatchDialogActionsListener, OnLoginChangeView {
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 20; // 20 minutes
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = DialogsFragment.REQUEST_DIALOG_ID_FOR_UPDATE;
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    public ContentFragment contentFragment = null;
    private FBLoginFragment fbLoginFragment = null;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    LocationListener locationListener;

    private MatchDialogFragment matchDialogFragment;
    private List<UserProfileInfo> matchDialogQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        fbLoginFragment = new FBLoginFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.main_content, fbLoginFragment).commit();
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);

        locationListener = new GPSLocationListener();
        locationDialog();
        matchDialogFragment = new MatchDialogFragment();
        matchDialogFragment.setActionsListener(this);

        matchDialogQueue = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void locationDialog() {

        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(MIN_TIME_BW_UPDATES);
        locationRequest.setFastestInterval(MIN_TIME_BW_UPDATES / 2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (googleApiClient != null) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode != RESULT_OK) {
                locationDialog();
            }
        } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
            Fragment fragment = contentFragment.getFragmentForPosition(2);
            fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == UserFragment.REQUEST_SETTINGS_CODE) {
            if (data != null) {
                if (data.getIntExtra("action", 0) == SettingsActivity.LOGOUT_ACTION) {
                    logout();
                } else if (data.getIntExtra("action", 0) == SettingsActivity.START_TEST_ACTION) {
                    showTest();
                }
            }
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationDialog();
            }
        }
    }


    @Override
    public void showTest() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new TestFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void logout() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new FBLoginFragment());
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit();
    }

    @Override
    public void showSettings() {

    }

    @Override
    public void showContent() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, new ContentFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public ContentFragment getContentFragment() {
        return contentFragment;
    }

    public void setContentFragment(ContentFragment contentFragment) {
        this.contentFragment = contentFragment;
    }

    public FBLoginFragment getFbLoginFragment() {
        return fbLoginFragment;
    }

    public void setFbLoginFragment(FBLoginFragment fbLoginFragment) {
        this.fbLoginFragment = fbLoginFragment;
    }

    @Override
    public void showMatchDialog(UserProfileInfo userProfileInfo, boolean fromQueue) {
        if (fromQueue) {
            showAndFillMatchDialog(matchDialogQueue.remove(0));
        } else {
            if (matchDialogFragment.isAddedToLayout() || matchDialogFragment.isAdded()) {
                matchDialogQueue.add(userProfileInfo);
            } else {
                showAndFillMatchDialog(userProfileInfo);
            }
        }
    }

    private void showAndFillMatchDialog(UserProfileInfo userProfileInfo) {
        QBUserCustomData userCustomData = new Gson().fromJson(SharedPrefsHelper.getInstance().getQbUser().getCustomData(), QBUserCustomData.class);
        matchDialogFragment.setImageLeftLink(userCustomData.getProfilePhotoData().get(0).getLink());
        matchDialogFragment.setImageRightLink(userProfileInfo.getPhotoLinks().get(0));
        matchDialogFragment.setRecipientId(userProfileInfo.getUserQbId());
        matchDialogFragment.setMatchUserName(userProfileInfo.getName());
        matchDialogFragment.setMatchValue(userProfileInfo.getMatchValue());
        matchDialogFragment.setAddedToLayout(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_content, matchDialogFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void keepSwipingButtonClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        matchDialogFragment.setAddedToLayout(false);
        ft.remove(matchDialogFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void sendMessageClicked(Integer recipientId) {
        matchDialogQueue.clear();
        keepSwipingButtonClicked();
        contentFragment.openChatDialog(recipientId);
    }

    @Override
    public void showMatchDialogIfAny() {
        if (!matchDialogQueue.isEmpty()) {
            showMatchDialog(null, true);
        }
    }

    @Override
    public void hideContent() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_content, new EmptyLoginFragment(), "HIDE_CONTENT");
        fragmentTransaction.commit();
    }
}
