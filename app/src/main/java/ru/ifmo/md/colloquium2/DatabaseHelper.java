package ru.ifmo.md.colloquium2;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author volhovm
 *         Created on 11/11/14
 */

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "election.db";
    public static final String DATABASE_CANDIDATES_TABLE = "candidates";
    public static final String CANDIDATE_NAME = "candidate_name";
    public static final String CANDIDATE_VOTES = "candidate_votes";

    public static final String DATABASE_STATS_TABLE = "stats";
    public static final String VOTING_STARTED = "voting_started";

    public static final String CREATE_TABLE_1 =
            "create table " + DATABASE_CANDIDATES_TABLE + " (" +
            BaseColumns._ID + " integer primary key autoincrement, "
            + CANDIDATE_NAME + " text not null, "
            + CANDIDATE_VOTES + " integer);";
    public static final String CREATE_TABLE_2 =
            "create table " + DATABASE_STATS_TABLE + " (" +
            VOTING_STARTED + " integer);";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Updating from version " + oldVersion + " to version " + newVersion);
        db.execSQL("DROP TABLE IF IT EXIST " + DATABASE_CANDIDATES_TABLE);
        onCreate(db);
    }
}
