package com.eternalsrv.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.interfaces.OnLoginChangeView;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.ResourceUtils;
import com.eternalsrv.utils.SharedPrefsHelper;
import com.eternalsrv.utils.asynctasks.AsyncTaskParams;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.chat.ChatHelper;
import com.eternalsrv.utils.constant.GcmConsts;
import com.eternalsrv.utils.constant.ServerMethodsConsts;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FBLoginFragment extends Fragment {
    private OnLoginChangeView onLoginChangeView;
    private CallbackManager callbackManager;

    private final List<String> permissionNeeds = Arrays.asList("email", "user_birthday");

    private PreferencesManager preferencesManager;
    private MyPreferences myPreferences;

    private boolean logged;
    private boolean firstVisit;

    public FBLoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onLoginChangeView = (OnLoginChangeView) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(App.getAppContext());
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        callbackManager = CallbackManager.Factory.create();
        preferencesManager = App.getPreferencesManager();
        myPreferences = App.getPreferences();

        if(accessToken != null)
            startLogin(true);
        setProfileTracker();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = view.findViewById(R.id.facebook_login_button);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        myPreferences.setFbAccessToken(accessToken.getToken());
                        myPreferences.setFbId(Long.parseLong(AccessToken.getCurrentAccessToken().getUserId()));
                        preferencesManager.savePreferences();
                        onLoginChangeView.hideContent();
                        startLogin(false);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissionNeeds);
            }
        });

        ImageView imageView = view.findViewById(R.id.eternal_login_logo);
        Bitmap logoBitmap = drawableToBitmap(imageView.getDrawable());
        imageView.setImageBitmap(addGradient(logoBitmap));

        RelativeLayout loginLayout = view.findViewById(R.id.fragment_login_layout);
        if (AccessToken.getCurrentAccessToken() != null) {
            loginLayout.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap addGradient(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, 0, width/2, height, ResourceUtils.getColor(R.color.primary_blue), ResourceUtils.getColor(R.color.primary_purple), Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);

        return updatedBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void changeFragment(MainActivity mainActivity) {
        if (firstVisit) {
            firstVisit = false;
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_content, new TestFragment());
            fragmentTransaction.commit();
        } else {
            ContentFragment contentFragment = new ContentFragment();
            contentFragment.setOnMatchCreatedListener(mainActivity);
            if (mainActivity.getIntent() != null && mainActivity.getIntent().getExtras() != null) {
                if (mainActivity.getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_DIALOG_ID) != null) {
                    contentFragment.setDialogId(mainActivity.getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_DIALOG_ID));
                    contentFragment.setRecipientId(Integer.valueOf(mainActivity.getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_RECIPIENT_ID)));
                } else if (mainActivity.getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_NEW_PAIR) != null) {
                    contentFragment.setDialogId(mainActivity.getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_NEW_PAIR));
                }
            }

            mainActivity.setContentFragment(contentFragment);
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_content, contentFragment);
            transaction.commit();
        }
    }

    private void setProfileTracker() {
        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                this.stopTracking();
                Profile.setCurrentProfile(newProfile);

                if (newProfile == null)
                    logged = false;
                else if (newProfile != null) {
                    savePrefs(newProfile);
                }
            }

        };
        profileTracker.startTracking();
    }

    private void startLogin(boolean logFromSession) {
        if (!logged) {
            try {
                if(!logFromSession) {
                    AsyncTaskParams params = new AsyncTaskParams();
                    params.put("fb_id", myPreferences.getFbId());
                    params.put("accessToken", myPreferences.getFbAccessToken());

                    BaseAsyncTask task = new BaseAsyncTask(ServerMethodsConsts.LOGIN, params);
                    task.setHttpMethod("POST");
                    String result = task.execute().get();
                    handleLoginResponse(result);
                }
                onLoginChangeView.hideContent();
                logged = true;
                if(checkSignIn())
                    restoreChatSession();
                else {
                    QBUser user = new QBUser(myPreferences.getFbId().toString(), myPreferences.getFbId().toString());
                    user.setFullName(myPreferences.getFirstName() + " " + myPreferences.getLastName());
                    login(user);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleLoginResponse(String response) {
        try {
            if (response != null) {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString("status");
                if (status.equals("ok") || status.equals("registered")) {

                    String type = "";
                    try {
                        type = obj.getString("type");
                    } catch (JSONException json) {

                    }
                    myPreferences.setUserId(obj.getLong("user_id"));
                    myPreferences.setFirstName(obj.getString("first_name"));
                    myPreferences.setLastName(obj.getString("last_name"));
                    myPreferences.setBirthday(new SimpleDateFormat("MM/dd/yyyy").parse(obj.getString("birthday")));
                    myPreferences.setMinMatchValue(obj.getInt("min_match_value"));
                    myPreferences.setSexChoice(obj.getString("sex_choice"));
                    myPreferences.setRadious(obj.getInt("radius"));
                    myPreferences.setAgeRangeMin(obj.getInt("age_range_min"));
                    myPreferences.setAgeRangeMax(obj.getInt(("age_range_max")));
                    myPreferences.setDescription(obj.getString("description"));
                    myPreferences.setMbtiType(type);
                    preferencesManager.savePreferences();

                    if(status.equals("registered")) {
                        firstVisit = true;
                    }
                } else {
                    Toast.makeText(getContext(), "server error", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "unknown error" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void restoreChatSession(){
        if (!ChatHelper.getInstance().isLogged()) {
            QBUser currentUser = getUserFromSession();
            loginToChat(currentUser);
        } else {
            changeFragment((MainActivity)getActivity());
        }
    }

    private QBUser getUserFromSession(){
        QBUser user = SharedPrefsHelper.getInstance().getQbUser();
        user.setId(QBSessionManager.getInstance().getSessionParameters().getUserId());
        return user;
    }

    protected boolean checkSignIn() {
        return SharedPrefsHelper.getInstance().hasQbUser();
    }

    private void login(final QBUser user) {
        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                changeFragment((MainActivity)getActivity());
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Toast.makeText(App.getAppContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginToChat(final QBUser user) {

        ChatHelper.getInstance().loginToChat(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                changeFragment((MainActivity)getActivity());
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void savePrefs(Profile profile) {
        myPreferences.setImageURL(profile.getProfilePictureUri(200, 200).toString());
        preferencesManager.savePreferences();
    }

//    public boolean hasInternetConnection(Context context) {
//        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
//                ConnectivityManager.TYPE_WIFI};
//        try {
//            ConnectivityManager connectivityManager =
//                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            for (int networkType : networkTypes) {
//                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//                if (activeNetworkInfo != null &&
//                        activeNetworkInfo.getType() == networkType)
//                    return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        InternetConnectionDialog dialog = new InternetConnectionDialog();
//        dialog.show(fragmentManager, "Internet dialog");
//        return false;
//    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
