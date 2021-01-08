package com.eternalsrv.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

public class MatchDialog extends DialogFragment {
    public MatchDialog() {}

    public static MatchDialog newInstance(String name, int matchValue) {
        MatchDialog matchDialog = new MatchDialog();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("matchValue", matchValue);
        matchDialog.setArguments(args);
        return matchDialog;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String name = getArguments().getString("name", "error");
        int matchValue = getArguments().getInt("matchValue", -1);
        return new AlertDialog.Builder(getActivity())
                .setMessage(String.format("Ty i %s tworzycie parę z dopasowaniem %d %%!", name, matchValue))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
    }
}
