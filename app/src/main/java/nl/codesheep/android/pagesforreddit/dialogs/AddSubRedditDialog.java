package nl.codesheep.android.pagesforreddit.dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import nl.codesheep.android.pagesforreddit.R;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;

/**
 * Created by Rien on 04/08/2016.
 */
public class AddSubRedditDialog extends AppCompatDialogFragment {

    private static final String TAG = AddSubRedditDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.add_subreddit_dialog)
                .setTitle(R.string.add_subreddit)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText subredditField = (EditText) AddSubRedditDialog.this.getDialog().findViewById(R.id.dialog_subreddit);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", subredditField.getText().toString());
                        getContext().getContentResolver().insert(RedditProvider.SubReddits.SUBREDDITS, contentValues);
                        AddSubRedditDialog.this.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddSubRedditDialog.this.dismiss();
                    }
                });

        return builder.create();
    }
}
