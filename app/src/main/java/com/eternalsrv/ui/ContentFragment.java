package com.eternalsrv.ui;

import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eternalsrv.R;
import com.eternalsrv.ui.chat.ChatActivity;
import com.eternalsrv.ui.chat.DialogsFragment;
import com.eternalsrv.ui.login.RevealAnimationSetting;
import com.eternalsrv.ui.swipe.UserProfileInfo;
import com.eternalsrv.utils.AnimationUtils;
import com.eternalsrv.utils.asynctasks.QBGetChatDialogById;
import com.eternalsrv.utils.constant.GcmConsts;
import com.eternalsrv.utils.holders.QbDialogHolder;
import com.eternalsrv.utils.holders.QbUsersHolder;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.users.model.QBUser;

import java.util.concurrent.ExecutionException;

import static com.eternalsrv.ui.chat.DialogsFragment.REQUEST_DIALOG_ID_FOR_UPDATE;
import static com.eternalsrv.utils.ResourceUtils.getColor;

public class ContentFragment extends Fragment implements SwipeFragment.OnMatchCreated {
    private ContentPagerAdapter contentPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String dialogId;
    private Integer recipientId;

    private SwipeFragment.OnMatchCreated onMatchCreatedListener;

    public ContentFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        if(contentPagerAdapter == null)
            contentPagerAdapter = new ContentPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.content_container);
        tabLayout = (TabLayout) view.findViewById(R.id.content_tabs);

        if (!contentPagerAdapter.fragments.isEmpty()) {
            UserFragment userFragment = contentPagerAdapter.fragments.get(0) != null ? (UserFragment) contentPagerAdapter.fragments.get(0) : new UserFragment();
            SwipeFragment swipeFragment = contentPagerAdapter.fragments.get(1) != null ? (SwipeFragment) contentPagerAdapter.fragments.get(1) : new SwipeFragment();
            DialogsFragment dialogsFragment = contentPagerAdapter.fragments.get(2) != null ? (DialogsFragment) contentPagerAdapter.fragments.get(2) : new DialogsFragment();
        } else {
            contentPagerAdapter.fragments.add(new UserFragment());
            SwipeFragment swipeFragment = new SwipeFragment();
            swipeFragment.setOnMatchCreatedListener(this);
            contentPagerAdapter.fragments.add(swipeFragment);
            contentPagerAdapter.fragments.add(new DialogsFragment());
        }

        viewPager.setAdapter(contentPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Drawable tabProfileDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_profile_animated_icon);
                        tabLayout.getTabAt(0).setIcon(tabProfileDrawable);
                        ((Animatable) tab.getIcon()).start();
                        break;
                    case 1:
                        Drawable tabSwipeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_swipe_animated_icon);
                        tabLayout.getTabAt(1).setIcon(tabSwipeDrawable);
                        ((Animatable) tab.getIcon()).start();
                        break;
                    case 2:
                        Drawable tabChatDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_chat_animated_icon);
                        tabLayout.getTabAt(2).setIcon(tabChatDrawable);
                        ((Animatable) tab.getIcon()).start();
                        break;
                    default:
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Drawable tabProfileDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_profile_animated_icon_reverse);
                        tabLayout.getTabAt(0).setIcon(tabProfileDrawable);
                        ((Animatable) tab.getIcon()).start();
                        break;
                    case 1:
                        Drawable tabSwipeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_swipe_animated_icon_reverse);
                        tabLayout.getTabAt(1).setIcon(tabSwipeDrawable);
                        ((Animatable) tab.getIcon()).start();
                        break;
                    case 2:
                        Drawable tabChatDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_chat_animated_icon_reverse);
                        tabLayout.getTabAt(2).setIcon(tabChatDrawable);
                        ((Animatable) tab.getIcon()).start();
                        break;
                    default:
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();
        TabLayout.Tab selectedTab = tabLayout.getTabAt(1);
        selectedTab.select();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        RevealAnimationSetting revealAnimationSetting = RevealAnimationSetting.with(width/2, height/2,
                width, height);
        AnimationUtils.registerCircularRevealAnimation(getActivity(), view, revealAnimationSetting, getColor(R.color.white), getColor(R.color.white));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupTabIcons() {
        AnimatedVectorDrawable tabProfileDrawable = (AnimatedVectorDrawable) ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_profile_animated_icon);
        tabLayout.getTabAt(0).setIcon(tabProfileDrawable);

        Drawable tabSwipeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_swipe_animated_icon);
        tabLayout.getTabAt(1).setIcon(tabSwipeDrawable);
        Drawable tabChatDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tab_layout_chat_animated_icon);
        tabLayout.getTabAt(2).setIcon(tabChatDrawable);
    }

    public @Nullable Fragment getFragmentForPosition(int position)
    {
        return contentPagerAdapter.getItem(position);
    }

    @Override
    public void onStart() {
        super.onStart();
        String d = dialogId;
        Integer r = recipientId;
        dialogId = null;
        recipientId = null;
        if(d != null && !d.equals(GcmConsts.EXTRA_GCM_NEW_PAIR)) {
            viewPager.setCurrentItem(2);
            openChatDialog(d, r);
        } else if (d != null && d.equals(GcmConsts.EXTRA_GCM_NEW_PAIR)) {
            viewPager.setCurrentItem(2);
        }
    }

    public void openChatDialog(String dialogId, Integer recipientId) {
        Object[] result = null;
        QBUser user = null;
        QBChatDialog chatDialog = null;
        try {
            QBGetChatDialogById getChatDialog = new QBGetChatDialogById();
            result = getChatDialog.execute(dialogId, recipientId.toString()).get();
            chatDialog = (QBChatDialog) result[0];
            user = (QBUser) result[1];
            Log.d("USER", user.toString());
            QbUsersHolder.getInstance().putUser(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, chatDialog);
    }

    public void openChatDialog(Integer recipientId) {
        QBChatDialog chatDialog =  QbDialogHolder.getInstance().getDialogWithRecipientId(recipientId);
        if (chatDialog != null)
            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, chatDialog);
        else {
            ((DialogsFragment) getFragmentForPosition(2)).setOpenDialogFromSwiping(true);
        }
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public SwipeFragment.OnMatchCreated getOnMatchCreatedListener() {
        return onMatchCreatedListener;
    }

    public void setOnMatchCreatedListener(SwipeFragment.OnMatchCreated onMatchCreatedListener) {
        this.onMatchCreatedListener = onMatchCreatedListener;
    }

    @Override
    public void showMatchDialog(UserProfileInfo userProfileInfo, boolean fromQueue) {
        onMatchCreatedListener.showMatchDialog(userProfileInfo, fromQueue);
    }
}
