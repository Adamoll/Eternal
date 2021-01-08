package com.eternalsrv;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import androidx.test.runner.AndroidJUnit4;

import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UnitTests {
    @Test
    public void myPreferencesTest() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        PreferencesManager preferencesManager = new PreferencesManager(appContext);
        MyPreferences myPreferences = preferencesManager.getMyPreferences();
        myPreferences.setDescription("sample DESCRIPTION");
        myPreferences.setFbId(123123L);
        myPreferences.setLatitude(23.3221);
        Calendar calendar = new GregorianCalendar();
        calendar.set(2018,11,26);
        myPreferences.setBirthday(calendar.getTime());
        myPreferences.setGender(null);

        preferencesManager.savePreferences();

        PreferencesManager preferencesManagerTest = new PreferencesManager(appContext);
        MyPreferences myPreferencesTest = preferencesManagerTest.getMyPreferences();


    }
}
