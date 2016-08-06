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
import android.widget.Toast;

import nl.codesheep.android.pagesforreddit.R;
import nl.codesheep.android.pagesforreddit.data.RedditProvider;
import nl.codesheep.android.pagesforreddit.sync.redditapi.ListingResponse;
import nl.codesheep.android.pagesforreddit.sync.redditapi.RedditService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rien on 04/08/2016.
 */
public class AddSubRedditDialog extends AppCompatDialogFragment implements Callback<ListingResponse> {

    private static final String TAG = AddSubRedditDialog.class.getSimpleName();
    private String mAddedSubReddit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.add_subreddit_dialog)
                .setTitle(R.string.add_subreddit)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText subredditField = (EditText) AddSubRedditDialog.this.getDialog().findViewById(R.id.dialog_subreddit);
                        mAddedSubReddit = subredditField.getText().toString();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(RedditService.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RedditService.ListingCalls service = retrofit.create(RedditService.ListingCalls.class);
                        service.hotPosts(mAddedSubReddit, "").enqueue(AddSubRedditDialog.this);
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

    private void storeSubReddit(String subreddit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", subreddit);
        getContext().getContentResolver().insert(RedditProvider.SubReddits.SUBREDDITS, contentValues);
    }

    @Override
    public void onResponse(Call<ListingResponse> call, Response<ListingResponse> response) {
        if (isAdded()) {
            if (response.code() == 200) {
                storeSubReddit(mAddedSubReddit);
            } else {
                Toast.makeText(getContext(), getString(R.string.subreddit_not_found), Toast.LENGTH_SHORT)
                        .show();
            }
            dismiss();
            Log.d(TAG, "Success, code: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<ListingResponse> call, Throwable t) {
        Toast.makeText(getContext(),getString(R.string.couldnt_reach_server), Toast.LENGTH_SHORT)
                .show();
        dismiss();
    }
}
