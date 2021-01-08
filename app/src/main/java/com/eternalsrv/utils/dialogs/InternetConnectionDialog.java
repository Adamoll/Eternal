package com.eternalsrv.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

public class InternetConnectionDialog extends DialogFragment {

    public InternetConnectionDialog() {}

    public static InternetConnectionDialog newInstance() {
        InternetConnectionDialog internetConnectionDialog = new InternetConnectionDialog();
        return internetConnectionDialog;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("App required connection with internet")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }).create();
    }


}
