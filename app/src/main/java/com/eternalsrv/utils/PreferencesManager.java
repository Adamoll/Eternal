package com.eternalsrv.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PreferencesManager {
    FileInputStream fin;
    ObjectInputStream in;
    MyPreferences myPreferences;

    Context context;

    String filename = "MyPreferences.dat";

    public PreferencesManager(Context context) {
        this.context = context;
        try {
            fin = context.openFileInput(filename);
            in = new ObjectInputStream(fin);
            myPreferences = (MyPreferences)in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            myPreferences = new MyPreferences();
        }
    }

    public boolean savePreferences() {
        FileOutputStream fout;
        ObjectOutputStream out;
        try{
            fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fout);
            out.writeObject(myPreferences);
            out.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void deletePreferences() {
        myPreferences.empty();
        savePreferences();
    }

    public MyPreferences getMyPreferences() {
        return myPreferences;
    }

    public void setMyPreferences(MyPreferences myPreferences) {
        this.myPreferences = myPreferences;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
