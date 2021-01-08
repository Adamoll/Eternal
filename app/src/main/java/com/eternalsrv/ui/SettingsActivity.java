package com.eternalsrv.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.ui.widget.SettingsRangeSeekbar;
import com.eternalsrv.ui.widget.SettingsSeekbar;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.SharedPrefsHelper;
import com.eternalsrv.utils.asynctasks.AsyncTaskParams;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.chat.ChatHelper;
import com.eternalsrv.utils.constant.ServerMethodsConsts;
import com.eternalsrv.utils.holders.QbDialogHolder;
import com.facebook.login.LoginManager;
import com.quickblox.messages.services.SubscribeService;

public class SettingsActivity extends AppCompatActivity {
    public static final int LOGOUT_ACTION = 942;
    public static final int START_TEST_ACTION = 466;

    private PreferencesManager preferencesManager;
    private MyPreferences myPreferences;

    private SettingsSeekbar distanceSeekbar;
    private SettingsSeekbar matchValueSeekbar;
    private SettingsRangeSeekbar ageRangeSeekbar;

    private TextView distanceTextView;
    private TextView matchValueTextView;
    private TextView ageRangeTextView;
    private TextView sexPreferenceTextView;

    private Switch menSwitch;
    private Switch womenSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesManager = App.getPreferencesManager();
        myPreferences = App.getPreferences();
        menSwitch = findViewById(R.id.sex_preference_men_switch);
        womenSwitch = findViewById(R.id.sex_preference_women_switch);
        sexPreferenceTextView = findViewById(R.id.sex_preference_textview);
        distanceSeekbar = findViewById(R.id.settings_distance_seekbar);
        distanceTextView = findViewById(R.id.distance_preference_textview);
        matchValueSeekbar = findViewById(R.id.settings_match_value_seekbar);
        matchValueTextView = findViewById(R.id.match_value_preference_textview);
        ageRangeSeekbar = findViewById(R.id.settings_age_range_seekbar);
        ageRangeTextView = findViewById(R.id.age_range_preference_textview);

        CardView logoutCardView = findViewById(R.id.logout_cardview);
        CardView startTestCardView = findViewById(R.id.start_test_cardview);

        setActionBarSettings();

        menSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !womenSwitch.isChecked()) {
                    womenSwitch.setChecked(true);
                    sexPreferenceTextView.setText(R.string.women);
                } else if (isChecked && !womenSwitch.isChecked()) {
                    sexPreferenceTextView.setText(R.string.Men);
                } else if (isChecked && womenSwitch.isChecked()) {
                    sexPreferenceTextView.setText(R.string.men_and_women);
                } else if (!isChecked && womenSwitch.isChecked()) {
                    sexPreferenceTextView.setText(R.string.women);
                }
            }

        });

        womenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && !menSwitch.isChecked()) {
                    menSwitch.setChecked(true);
                    sexPreferenceTextView.setText(R.string.Men);
                } else if (isChecked && !menSwitch.isChecked()) {
                    sexPreferenceTextView.setText(R.string.women);
                } else if (isChecked && menSwitch.isChecked()) {
                    sexPreferenceTextView.setText(R.string.men_and_women);
                } else if (!isChecked && menSwitch.isChecked()) {
                    sexPreferenceTextView.setText(R.string.men);
                }
            }

        });

        if (myPreferences.getSexChoice().contains("M"))
            menSwitch.setChecked(true);
        if (myPreferences.getSexChoice().contains("F"))
            womenSwitch.setChecked(true);

        distanceSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                distanceTextView.setText(String.format("%s km", minValue));
            }
        });

        distanceSeekbar.setMinStartValue(myPreferences.getRadious());
        distanceSeekbar.apply();

        matchValueSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                matchValueTextView.setText(String.format("%s%%", minValue));
            }
        });

        matchValueSeekbar.setMinStartValue(myPreferences.getMinMatchValue());
        matchValueSeekbar.apply();

        ageRangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageRangeTextView.setText(String.format("%s-%s", minValue, maxValue));
            }
        });

        ageRangeSeekbar.setMinStartValue(myPreferences.getAgeRangeMin());
        ageRangeSeekbar.setMaxStartValue(myPreferences.getAgeRangeMax());
        ageRangeSeekbar.apply();

        logoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QBuserLogout();
                LoginManager.getInstance().logOut();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", LOGOUT_ACTION);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        startTestCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("action", START_TEST_ACTION);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveSettings();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    private void setActionBarSettings() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#555555' align='right'>Settings</font>"));
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.primary_purple), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
    }

    private void saveSettings() {
        myPreferences.setRadious(distanceSeekbar.getSelectedMinValue().intValue());
        String sexChoice = "";
        if (menSwitch.isChecked())
            sexChoice += "M";
        if (womenSwitch.isChecked())
            sexChoice += "F";
        myPreferences.setSexChoice(sexChoice);
        myPreferences.setMinMatchValue(matchValueSeekbar.getSelectedMinValue().intValue());
        myPreferences.setAgeRangeMin(ageRangeSeekbar.getSelectedMinValue().intValue());
        myPreferences.setAgeRangeMax(ageRangeSeekbar.getSelectedMaxValue().intValue());
        preferencesManager.savePreferences();

        AsyncTaskParams params = new AsyncTaskParams();
        params.put("radious", myPreferences.getRadious());
        params.put("min_match_value", myPreferences.getMinMatchValue());
        params.put("user_id", myPreferences.getUserId());
        params.put("sex_choice", myPreferences.getSexChoice());
        params.put("age_range_min", myPreferences.getAgeRangeMin());
        params.put("age_range_max", myPreferences.getAgeRangeMax());
        BaseAsyncTask saveSettingsTask = new BaseAsyncTask(ServerMethodsConsts.USERSETTINGS, params);
        saveSettingsTask.setHttpMethod("POST");
        saveSettingsTask.execute();
    }

    private void QBuserLogout() {
        // TODO LOGOUT
        ChatHelper.getInstance().destroy();
        SubscribeService.unSubscribeFromPushes(App.getAppContext());
        SharedPrefsHelper.getInstance().removeQbUser();
        QbDialogHolder.getInstance().clear();
        //finish();
    }
}
