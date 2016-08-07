package nl.codesheep.android.pagesforreddit.data.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;

public class SubReddit {

    private long mId = 0;
    @SerializedName("display_name")
    @Expose
    private String name;

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (mId != 0) {
            contentValues.put(SubRedditsTable.ID, mId);
        }
        contentValues.put(SubRedditsTable.NAME, name);
        return contentValues;
    }

    public static SubReddit createFromCursor(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(SubRedditsTable.ID);
        int nameIndex = cursor.getColumnIndexOrThrow(SubRedditsTable.NAME);

        SubReddit subReddit = new SubReddit();
        subReddit.setId(cursor.getInt(idIndex));
        subReddit.setName(cursor.getString(nameIndex));
        return subReddit;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
