package com.eternalsrv;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.holders.QbDialogHolder;
import com.quickblox.chat.model.QBChatDialog;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class UnitTestTest {
    @Test
    public void myPreferencesTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        PreferencesManager preferencesManager = new PreferencesManager(appContext);
        MyPreferences myPreferences = preferencesManager.getMyPreferences();

        Calendar calendar = new GregorianCalendar();
        calendar.set(2018,11,26);
        myPreferences.setDescription("sample DESCRIPTION");
        myPreferences.setFbId(123123L);
        myPreferences.setLatitude(23.3221);
        myPreferences.setBirthday(calendar.getTime());
        myPreferences.setGender(null);
        preferencesManager.savePreferences();

        PreferencesManager preferencesManagerTest = new PreferencesManager(appContext);
        MyPreferences myPreferencesTest = preferencesManagerTest.getMyPreferences();

        assertEquals(myPreferences.getDescription(), myPreferencesTest.getDescription());
        assertEquals(myPreferences.getFbId(), myPreferencesTest.getFbId());
        assertEquals(myPreferences.getLatitude(), myPreferencesTest.getLatitude(), 0.01);
        assertEquals(myPreferences.getBirthday(), myPreferencesTest.getBirthday());
        assertEquals(myPreferences.getGender(), myPreferencesTest.getGender());
    }

    @Test
    public void dialogLastMessageDateComparatorTest() throws Exception {
        Map<String, QBChatDialog> unsortedMap = new HashMap<>();
        QBChatDialog chatDialog1 = new QBChatDialog("1");
        chatDialog1.setLastMessageDateSent(System.currentTimeMillis());
        unsortedMap.put("1", chatDialog1);

        QBChatDialog chatDialog2 = new QBChatDialog("2");
        chatDialog2.setLastMessageDateSent(System.currentTimeMillis() + 1000);
        unsortedMap.put("2", chatDialog2);

        QBChatDialog chatDialog3 = new QBChatDialog("3");
        chatDialog3.setLastMessageDateSent(System.currentTimeMillis() - 1000);
        unsortedMap.put("3", chatDialog3);

        QBChatDialog chatDialog4 = new QBChatDialog("4");
        chatDialog4.setLastMessageDateSent(System.currentTimeMillis());
        unsortedMap.put("4", chatDialog4);

        Map<String, QBChatDialog> sortedMap = new TreeMap<>(new QbDialogHolder.LastMessageDateSentComparator(unsortedMap));

        List<QBChatDialog> dialogs = new ArrayList<>(sortedMap.values());

        for(int i = 0; i < dialogs.size() - 1; i++) {
            assertTrue(dialogs.get(i).getLastMessageDateSent() < dialogs.get(i + 1).getLastMessageDateSent());
        }
    }
}