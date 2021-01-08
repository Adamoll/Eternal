package com.eternalsrv.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;

import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.interpolator.SmoothInterpolator;
import com.eternalsrv.ui.interpolator.SpringInterpolator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchDialogFragment extends Fragment {
    private MatchDialogActionsListener actionsListener;

    private TextView matchText;
    private TextView matchValueText;
    private CircleImageView imageViewLeft;
    private CircleImageView imageViewRight;
    private ViewGroup viewGroupContainer;
    private Button buttonSendMessage;
    private Button buttonKeepSwipping;

    private String imageLeftLink;
    private String imageRightLink;
    private String matchUserName;

    private Integer recipientId;
    private Integer matchValue;
    private boolean addedToLayout;

    public MatchDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View viewRoot = inflater.inflate(R.layout.fragment_match_dialog, container, false);
        viewGroupContainer = viewRoot.findViewById(R.id.contentPanel);
        matchText = viewRoot.findViewById(R.id.match_dialog_match_text);
        matchText.setVisibility(View.INVISIBLE);
        matchValueText = viewRoot.findViewById(R.id.match_dialog_match_value_text);
        String matchValueTextString = String.format("Your and %s's\npersonality fit together in %d%%!", matchUserName, matchValue);
        matchValueText.setText(matchValueTextString);
        matchValueText.setVisibility(View.INVISIBLE);
        imageViewLeft = viewRoot.findViewById(R.id.match_dialog_image_left);
        imageViewLeft.setImageBitmap(App.getImageLoader().getBitmapFromMemCache(imageLeftLink));
        imageViewLeft.setVisibility(View.INVISIBLE);
        imageViewRight = viewRoot.findViewById(R.id.match_dialog_image_right);
        imageViewRight.setImageBitmap(App.getImageLoader().getBitmapFromMemCache(imageRightLink));
        imageViewRight.setVisibility(View.INVISIBLE);
        buttonKeepSwipping = viewRoot.findViewById(R.id.match_dialog_keep_swiping);
        buttonSendMessage = viewRoot.findViewById(R.id.match_dialog_send_message);
        buttonSendMessage.setVisibility(View.INVISIBLE);
        buttonKeepSwipping.setVisibility(View.INVISIBLE);
        int colorFrom = Color.parseColor("#00000000");

        int colorTo = Color.parseColor("#ee222222");
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(850); // milliseconds
        colorAnimation.setInterpolator(new SmoothInterpolator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean slideTransitions;
            boolean fadeTransitions;

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                viewRoot.findViewById(R.id.match_dialog_overlay).setBackgroundColor((int) animator.getAnimatedValue());
                if (!fadeTransitions && animator.getCurrentPlayTime() > 350) {
                    Fade fade = new Fade();
                    fade.setDuration(200);
                    fade.addTarget(matchText);
                    fade.addTarget(matchValueText);
                    TransitionManager.beginDelayedTransition(viewGroupContainer, fade);
                    matchText.setVisibility(View.VISIBLE);
                    matchValueText.setVisibility(View.VISIBLE);
                    fadeTransitions = true;
                }

                if (!slideTransitions && animator.getCurrentPlayTime() > 600) {
                    Slide slide = new Slide();
                    slide.setSlideEdge(Gravity.RIGHT);
                    slide.setDuration(1200);
                    slide.setInterpolator(new SpringInterpolator(1.3f));
                    slide.addTarget(imageViewLeft);

                    Slide slide2 = new Slide();
                    slide2.setSlideEdge(Gravity.LEFT);
                    slide2.setDuration(1200);
                    slide2.setInterpolator(new SpringInterpolator(1.3f));
                    slide2.addTarget(imageViewRight);

                    Slide slide3 = new Slide();
                    slide3.setSlideEdge(Gravity.LEFT);
                    slide3.setDuration(1000);
                    slide3.setInterpolator(new SpringInterpolator(2));
                    slide3.addTarget(buttonSendMessage);

                    Slide slide4 = new Slide();
                    slide4.setSlideEdge(Gravity.RIGHT);
                    slide4.setDuration(1000);
                    slide4.setInterpolator(new SpringInterpolator(2));
                    slide4.addTarget(buttonKeepSwipping);

                    Fade fade = new Fade();
                    fade.setDuration(200);
                    fade.addTarget(matchText);

                    TransitionSet transitionSet = new TransitionSet()
                            .addTransition(slide)
                            .addTransition(slide2)
                            .addTransition(slide3)
                            .addTransition(slide4)
                            .addTransition(fade);
                    TransitionManager.beginDelayedTransition(viewGroupContainer, transitionSet);
                    imageViewRight.setVisibility(View.VISIBLE);
                    imageViewLeft.setVisibility(View.VISIBLE);
                    buttonKeepSwipping.setVisibility(View.VISIBLE);
                    buttonSendMessage.setVisibility(View.VISIBLE);

                    slideTransitions = true;
                }
            }
        });

        buttonKeepSwipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animationDialog = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.match_dialog_dismiss);
                Animation animationOverlay = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.match_dialog_overlay_dismiss);
                animationDialog.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        actionsListener.keepSwipingButtonClicked();
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                getView().findViewById(R.id.match_dialog_overlay).startAnimation(animationOverlay);
                getView().findViewById(R.id.match_dialog_container).startAnimation(animationDialog);
            }
        });

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animationDialog = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.match_dialog_dismiss);
                Animation animationOverlay = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.match_dialog_overlay_dismiss);
                animationDialog.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        actionsListener.sendMessageClicked(recipientId);
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                getView().findViewById(R.id.match_dialog_overlay).startAnimation(animationOverlay);
                getView().findViewById(R.id.match_dialog_container).startAnimation(animationDialog);
            }
        });
        colorAnimation.start();

        return viewRoot;
    }

    public String getImageLeftLink() {
        return imageLeftLink;
    }

    public void setImageLeftLink(String imageLeftLink) {
        this.imageLeftLink = imageLeftLink;
    }

    public String getImageRightLink() {
        return imageRightLink;
    }

    public void setImageRightLink(String imageRightLink) {
        this.imageRightLink = imageRightLink;
    }

    public MatchDialogActionsListener getActionsListener() {
        return actionsListener;
    }

    public boolean isAddedToLayout() {
        return addedToLayout;
    }

    public void setAddedToLayout(boolean addedToLayout) {
        this.addedToLayout = addedToLayout;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public void setActionsListener(MatchDialogActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Integer getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(Integer matchValue) {
        this.matchValue = matchValue;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        actionsListener.showMatchDialogIfAny();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface MatchDialogActionsListener {
        void keepSwipingButtonClicked();

        void sendMessageClicked(Integer recipientId);

        void showMatchDialogIfAny();
    }
}
