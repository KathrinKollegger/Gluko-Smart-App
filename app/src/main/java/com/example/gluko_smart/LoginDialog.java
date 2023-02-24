package com.example.gluko_smart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

//Creates Short interaktive Dialog while Login is ongoing
public class LoginDialog extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialBuilder = new AlertDialog.Builder(getActivity());
        dialBuilder.setTitle(getString(R.string.LoginOngoing))
                .setMessage(getString(R.string.PleaseWaitAMoment))
                .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return dialBuilder.create();
    }
}
