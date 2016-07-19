package nl.codesheep.android.pagesforreddit.data.models;

import android.content.ContentValues;
import android.database.Cursor;

import nl.codesheep.android.pagesforreddit.data.SubRedditsTable;

public class SubReddit {

    private long id = 0;
    private String name;

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        if (id != 0) {
            contentValues.put(SubRedditsTable.ID, id);
        }
        contentValues.put(SubRedditsTable.NAME, name);
        return contentValues;
    }

    public static SubReddit fromCursor(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(SubRedditsTable.ID);
        int nameIndex = cursor.getColumnIndexOrThrow(SubRedditsTable.NAME);

        SubReddit subReddit = new SubReddit();
        subReddit.setId(cursor.getInt(idIndex));
        subReddit.setName(cursor.getString(nameIndex));
        return subReddit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
