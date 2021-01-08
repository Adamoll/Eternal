package com.eternalsrv.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.chat.DialogsFragment;
import com.eternalsrv.ui.swipe.UserProfileInfo;
import com.eternalsrv.ui.swipe.UserSwipeProfileAdapter;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.PushUtils;
import com.eternalsrv.utils.asynctasks.AsyncTaskParams;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.constant.ServerMethodsConsts;
import com.eternalsrv.utils.holders.UserProfileInfoHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import link.fls.swipestack.SwipeStack;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SwipeFragment extends Fragment implements SwipeStack.SwipeStackListener {

    private SwipeStack mSwipeStack;
    private LinearLayout swipeDislikeButtonLayout;
    private LinearLayout swipeLikeButtonLayout;
    private UserSwipeProfileAdapter userSwipeProfileAdapter;
    private OnMatchCreated onMatchCreatedListener;

    private GetSwipeUsers getSwipeUsers;
    private AsyncTaskParams asyncTaskParams;
    private SwipeUser swipeUser;
    private AsyncTaskParams swipeParams;

    private MyPreferences preferences;

    public SwipeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_swipe, container, false);
        setup(viewRoot);
        return viewRoot;
    }

    private void setup(View viewRoot) {
        preferences = App.getPreferences();

        asyncTaskParams = new AsyncTaskParams();
        asyncTaskParams.put("user_id", preferences.getUserId());
        getSwipeUsers = new GetSwipeUsers(ServerMethodsConsts.USERSTOSWIPE + "/" + preferences.getUserId(), asyncTaskParams);

        userSwipeProfileAdapter = new UserSwipeProfileAdapter(App.getAppContext(), getActivity());
        swipeDislikeButtonLayout = viewRoot.findViewById(R.id.swipe_dislike_button_layout);
        swipeLikeButtonLayout = viewRoot.findViewById(R.id.swipe_like_button_layout);

        checkUserList();

        mSwipeStack = viewRoot.findViewById(R.id.swipeStack);
        mSwipeStack.setAdapter(userSwipeProfileAdapter);
        mSwipeStack.setListener(this);

        swipeDislikeButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeStack.swipeTopViewToLeft();
            }
        });
        swipeLikeButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeStack.swipeTopViewToRight();
            }
        });
    }

    private void checkUserList() {
        if (userSwipeProfileAdapter.isEmpty()) {
            swipeDislikeButtonLayout.setVisibility(View.INVISIBLE);
            swipeLikeButtonLayout.setVisibility(View.INVISIBLE);
            getSwipeUsers = new GetSwipeUsers(ServerMethodsConsts.USERSTOSWIPE + "/" + preferences.getUserId(), asyncTaskParams);
            getSwipeUsers.execute();
        }
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        UserProfileInfo profile = userSwipeProfileAdapter.getItem(position);
        swipeParams = new AsyncTaskParams();
        swipeParams.put("user_id", preferences.getUserId());
        swipeParams.put("swiped_id", profile.getUserId());
        swipeParams.put("wanna_meet", 0);
        swipeParams.put("name", profile.getName());
        swipeParams.put("match_value", profile.getMatchValue());
        swipeUser = new SwipeUser(ServerMethodsConsts.SWIPED, swipeParams);
        swipeUser.setHttpMethod("POST");
        swipeUser.execute();
    }

    @Override
    public void onViewSwipedToRight(int position) {
        UserProfileInfo profile = userSwipeProfileAdapter.getItem(position);
        swipeParams = new AsyncTaskParams();
        swipeParams.put("user_id", preferences.getUserId());
        swipeParams.put("swiped_id", profile.getUserId());
        swipeParams.put("wanna_meet", 1);
        swipeParams.put("name", profile.getName());
        swipeParams.put("match_value", profile.getMatchValue());
        swipeUser = new SwipeUser(ServerMethodsConsts.SWIPED, swipeParams);
        swipeUser.setHttpMethod("POST");
        swipeUser.execute();
    }

    @Override
    public void onStackEmpty() {
        checkUserList();
    }

    private class GetSwipeUsers extends BaseAsyncTask {

        public GetSwipeUsers(String urn, AsyncTaskParams params) {
            super(urn, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("status").equals("ok")) {
                        JSONArray users = obj.getJSONArray("users_found");
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            UserProfileInfo profile = new UserProfileInfo();
                            profile.setUserId(user.getInt("swiped_id"));
                            profile.setUserQbId(user.getInt("swiped_qb_id"));
                            profile.setName(user.getString("name"));
                            profile.setAge(user.getInt("age"));
                            profile.setMatchValue(user.getInt("match_value"));
                            profile.setDistance(user.getInt("distance"));
                            profile.setDescription(user.getString("description"));
                            profile.setPhotoLinks(copyLinks(user.getString("photo_links")));
                            userSwipeProfileAdapter.add(profile);
                        }
                        userSwipeProfileAdapter.notifyDataSetChanged();
                        if (userSwipeProfileAdapter.getCount() > 0) {
                            swipeDislikeButtonLayout.setVisibility(View.VISIBLE);
                            swipeLikeButtonLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        private List<String> copyLinks(String links) throws JSONException {
            if (links != null) {
                String[] list = links.split(";");
                return Arrays.asList(list);
            }
            return null;
        }
    }

    public void setOnMatchCreatedListener(OnMatchCreated onMatchCreatedListener) {
        this.onMatchCreatedListener = onMatchCreatedListener;
    }

    public interface OnMatchCreated {
        void showMatchDialog(UserProfileInfo userProfileInfo, boolean fromQueue);
    }

    private class SwipeUser extends BaseAsyncTask {

        public SwipeUser(String urn, AsyncTaskParams params) {
            super(urn, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    boolean isMatch = obj.has("match");
                    if (isMatch) {
                        String name = obj.getString("name");
                        int matchValue = obj.getInt("match_value");
                        int recipientId = obj.getInt("qb_id");
                        int userId = obj.getInt("user_id");

                        MainActivity mainActivity = (MainActivity)getContext();
                        UserProfileInfo userProfile = userSwipeProfileAdapter.getProfileByUserId(userId);
                        UserProfileInfoHolder.getInstance().putProfileInfo(userProfile);
                        ((DialogsFragment)mainActivity.getContentFragment().getFragmentForPosition(2)).createNewDialog(recipientId, matchValue);
                        PushUtils.sendPushAboutNewPair(recipientId);
                        onMatchCreatedListener.showMatchDialog(userProfile, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
