package com.brettsun.jcdecauxcycling;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Dialog for prompting users to get directions to a station
 */
public class MapsDirectionDialog extends DialogFragment {

    public interface MapsDirectionDialogListener {
        public void onGetDirectionsClick(DialogFragment dialog);
    }

    private static final String TAG = "MapsDirectionDialog";
    private MapsDirectionDialogListener mListener = null;

    // Attach maps activity as listener so it can send the intent for getting directions
    // Adapted from http://developer.android.com/guide/topics/ui/dialogs.html
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MapsDirectionDialogListener so we can send events to the host
            mListener = (MapsDirectionDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement MapsDirectionDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.maps_directions_message)
                .setPositiveButton(R.string.maps_get_directions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onGetDirectionsClick(MapsDirectionDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return dialogBuilder.create();
    }

}
