package com.example.gluko_smart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

//Creates Short interaktive Dialog while Registration is ongoing
public class RegisterDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialBuilder = new AlertDialog.Builder(getActivity());
        dialBuilder.setTitle(R.string.RegistrationOngoing)
                .setMessage(R.string.PleaseWaitAMoment)
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return dialBuilder.create();
    }
}
