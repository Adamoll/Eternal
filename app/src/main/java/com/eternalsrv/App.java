package com.eternalsrv;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.eternalsrv.models.QbConfigs;
import com.eternalsrv.models.SampleConfigs;
import com.eternalsrv.utils.ActivityLifecycle;
import com.eternalsrv.utils.Consts;
import com.eternalsrv.utils.ImageLoader;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.configs.ConfigUtils;
import com.eternalsrv.utils.configs.CoreConfigUtils;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.ServiceZone;

import java.io.IOException;
import java.util.Calendar;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();

    private static App instance;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private static QbConfigs qbConfigs;
    private static SampleConfigs sampleConfigs;
    private static ImageLoader imageLoader;

    private static Context context;
    public static int year = Calendar.getInstance().get(Calendar.YEAR);

    private static PreferencesManager preferencesManager;
    private static MyPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ActivityLifecycle.init(this);

        initQbConfigs();

        App.context = getApplicationContext();
        preferencesManager = new PreferencesManager(App.context);
        preferences = preferencesManager.getMyPreferences();
        imageLoader = new ImageLoader();
        initSampleConfigs();
        initCredentials();
    }

    private void initSampleConfigs() {
        try {
            sampleConfigs = ConfigUtils.getSampleConfigs(Consts.SAMPLE_CONFIG_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SampleConfigs getSampleConfigs() {
        return sampleConfigs;
    }

    private void initQbConfigs() {
        Log.e(TAG, "QB CONFIG FILE NAME: " + getQbConfigFileName());
        qbConfigs = CoreConfigUtils.getCoreConfigsOrNull(getQbConfigFileName());
    }

    public static synchronized App getInstance() {
        return instance;
    }

    public void initCredentials(){
        if (qbConfigs != null) {
            QBSettings.getInstance().init(getApplicationContext(), qbConfigs.getAppId(), qbConfigs.getAuthKey(), qbConfigs.getAuthSecret());
            QBSettings.getInstance().setAccountKey(qbConfigs.getAccountKey());

            if (!TextUtils.isEmpty(qbConfigs.getApiDomain()) && !TextUtils.isEmpty(qbConfigs.getChatDomain())) {
                QBSettings.getInstance().setEndpoints(qbConfigs.getApiDomain(), qbConfigs.getChatDomain(), ServiceZone.PRODUCTION);
                QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
            }
        }
    }

    public static QbConfigs getQbConfigs(){
        return qbConfigs;
    }

    public static String getQbConfigFileName(){
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static MyPreferences getPreferences() {
        return preferences;
    }

    public static PreferencesManager getPreferencesManager() {
        return preferencesManager;
    }
}