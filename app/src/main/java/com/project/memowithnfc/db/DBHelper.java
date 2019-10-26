package com.project.memowithnfc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project.memowithnfc.vo.Category;
import com.project.memowithnfc.vo.Memo;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DBHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "text.db";

    // Table Names
    private static final String TABLE_MEMO = "memo";
    private static final String TABLE_CATEGORY = "category";

    // Common column names
    private static final String KEY_ID = "id";
    //private static final String KEY_CREATED_AT = "created_at";

    // memo Table - column names
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_ALARMSETTING = "alarmSetting";
    private static final String KEY_CATEGORY_ID = "category_id";

    // category Table - column names
    private static final String KEY_CATEGORY_NAME = "name";

    // Table Create Statements
    // memo table create statement
    private static final String CREATE_TABLE_MEMO =
            "CREATE TABLE " + TABLE_MEMO + "("
                    + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + KEY_DATE + " TEXT NOT NULL,"
                    + KEY_TIME + " TEXT NOT NULL,"
                    + KEY_CONTENT + " TEXT NOT NULL,"
                    + KEY_ALARMSETTING + " INTEGER NOT NULL,"
                    + KEY_CATEGORY_ID + " INTEGER NOT NULL,"
                    + "UNIQUE(" + KEY_DATE + "," + KEY_TIME + "," + KEY_CONTENT + "),"
                    + "CONSTRAINT category_id_fk FOREIGN KEY (" + KEY_CATEGORY_ID + ") "
                    + "REFERENCES " + TABLE_CATEGORY + "(" + KEY_ID + ") ON DELETE CASCADE"
                    + ")";

    // category table create statement
    private static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + TABLE_CATEGORY + "("
                    + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + KEY_CATEGORY_NAME + " TEXT NOT NULL UNIQUE " + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!db.isReadOnly())
            db.execSQL("PRAGMA foreign_key=ON;");
        // creating required tables
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_MEMO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        // create new tables
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    /*
     * count category exist
     */
    public int DoseCategoryExist(long category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT count(*) FROM " + TABLE_CATEGORY + " WHERE "
                + KEY_ID + " = " + category_id;

        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        return c.getInt(0);
    }

    /*
     * inserting a category
     */
    public long insertCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, category.getName());

        // insert row
        long category_id = db.insert(TABLE_CATEGORY, null, values);

        return category_id;
    }

    /*
     * get single category
     */
    public Category getCategory(long category_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE "
                + KEY_ID + " = " + category_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        Category ct = new Category();

        if (c != null && c.moveToFirst()) {
            ct.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            ct.setName(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));
        }

        return ct;
    }

    /*
     * getting Category By Name
     * */
    public Category getCategoryByName(String name) {
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY
                + " WHERE " + KEY_CATEGORY_NAME + " = '" + name + "'" ;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Category ct = new Category();

        if (c != null && c.moveToFirst()) {
            ct.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            ct.setName(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));
        }

        return ct;
    }

    /*
     * getting all Category
     * */
    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category ct = new Category();

                ct.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                ct.setName(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));

                // adding to memo list
                categories.add(ct);
            } while (c.moveToNext());
        }

        return categories;
    }

    /*
     * Updating a category
     */
    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, category.getName());

        // updating row
        return db.update(TABLE_CATEGORY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
    }

    /*
     * Deleting a category
     */
    public void deleteCategory(long category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.delete(TABLE_CATEGORY, KEY_ID + " = ?",
                new String[] { String.valueOf(category_id) });
    }

    /*
     * count memo column
     */
    public int countMemoColumn() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT count(*) FROM " + TABLE_MEMO;

        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        return c.getInt(0);
    }

    /*
     * inserting a memo
     */
    public long insertMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("PRAGMA foreign_keys = ON;");

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, memo.getDate());
        values.put(KEY_TIME, memo.getTime());
        values.put(KEY_CONTENT, memo.getContent());
        values.put(KEY_ALARMSETTING, memo.getAlarmSetting());
        values.put(KEY_CATEGORY_ID, memo.getCategory_id());

        // insert row
        long memo_id = db.insert(TABLE_MEMO, null, values);

        return memo_id;
    }

    /*
     * get single memo
     */
    public Memo getMemo(long memo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  " + TABLE_MEMO +".*, "+ TABLE_CATEGORY + "."+ KEY_CATEGORY_NAME +" FROM " + TABLE_MEMO + ", " + TABLE_CATEGORY +
                " WHERE " + TABLE_MEMO + "." + KEY_ID + " = " + memo_id + " AND " + TABLE_MEMO + "." + KEY_CATEGORY_ID + " = " + TABLE_CATEGORY + "." + KEY_ID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        Memo mm = new Memo();

        if (c != null && c.moveToFirst()) {
            mm.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            mm.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
            mm.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
            mm.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
            mm.setAlarmSetting(c.getInt(c.getColumnIndex(KEY_ALARMSETTING)));
            mm.setCategory_id(c.getInt(c.getColumnIndex(KEY_CATEGORY_ID)));
            mm.setCategory_name(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));
        }

        return mm;
    }

    /*
     * getting all Memo
     * */
    public ArrayList<Memo> getAllMemos() {
        ArrayList<Memo> memos = new ArrayList<Memo>();
        // select memo.*, category.name from memo, category where memo.category_id = category.id;
        String selectQuery = "SELECT " + TABLE_MEMO + ".*, "+ TABLE_CATEGORY + "."+ KEY_CATEGORY_NAME + " FROM " + TABLE_MEMO + ", " + TABLE_CATEGORY +
                " WHERE " + TABLE_MEMO + "." + KEY_CATEGORY_ID + " = " + TABLE_CATEGORY + "." + KEY_ID;

        //Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Memo mm = new Memo();
                mm.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                mm.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                mm.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
                mm.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                mm.setAlarmSetting(c.getInt(c.getColumnIndex(KEY_ALARMSETTING)));
                mm.setCategory_id(c.getInt(c.getColumnIndex(KEY_CATEGORY_ID)));
                mm.setCategory_name(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));

                // adding to memo list
                memos.add(mm);
            } while (c.moveToNext());
        }

        return memos;
    }

    /*
     * getting all memos under single category
     * */
    public ArrayList<Memo> getAllMemosByCategory(int category_id) {
        ArrayList<Memo> memos = new ArrayList<Memo>();

        //SELECT * FROM memo mm, category ct WHERE ct.id = category_id AND mm.category_id = ct.id;
        String selectQuery = "SELECT  mm.*, ct." + KEY_CATEGORY_NAME + " FROM " + TABLE_MEMO + " mm, " + TABLE_CATEGORY + " ct "
                + "WHERE ct." + KEY_ID + " = " + category_id
                + " AND mm." + KEY_CATEGORY_ID + " = " + "ct." + KEY_ID ;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Memo mm = new Memo();
                mm.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                mm.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                mm.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
                mm.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                mm.setAlarmSetting(c.getInt(c.getColumnIndex(KEY_ALARMSETTING)));
                mm.setCategory_id(c.getInt(c.getColumnIndex(KEY_CATEGORY_ID)));
                mm.setCategory_name(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));

                // adding to memo list
                memos.add(mm);
            } while (c.moveToNext());
        }

        return memos;
    }

    /*
     * getting next memos under single category
     * */
    public ArrayList<Memo> getNextMemosByCategory(int category_id) {
        ArrayList<Memo> memos = new ArrayList<Memo>();

        //SELECT * FROM memo mm, category ct WHERE ct.id = category_id AND mm.category_id = ct.id;
        String selectQuery = "SELECT  mm.*, ct." + KEY_CATEGORY_NAME + " FROM " + TABLE_MEMO + " mm, " + TABLE_CATEGORY + " ct "
                + "WHERE ct." + KEY_ID + " = " + category_id
                + " AND mm." + KEY_CATEGORY_ID + " = " + "ct." + KEY_ID
                + " AND CAST(strftime('%s', " + KEY_DATE + ") AS integer )"
                + " + (60 * 60 * CAST(strftime('%H', " + KEY_TIME +") AS integer))"
                + " + (60 * CAST(strftime('%M', " + KEY_TIME +") AS INTEGER))"
                + " + 60 >= CAST(strftime('%s', 'now', 'localtime') AS integer) "
                + "order by " + KEY_DATE + " asc, " + KEY_TIME +" asc";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Memo mm = new Memo();
                mm.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                mm.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                mm.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
                mm.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                mm.setAlarmSetting(c.getInt(c.getColumnIndex(KEY_ALARMSETTING)));
                mm.setCategory_id(c.getInt(c.getColumnIndex(KEY_CATEGORY_ID)));
                mm.setCategory_name(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));

                // adding to memo list
                memos.add(mm);
            } while (c.moveToNext());
        }

        return memos;
    }

    /*
     * getting previous memos under single category
     * */
    public ArrayList<Memo> getPreviousMemosByCategory(int category_id) {
        ArrayList<Memo> memos = new ArrayList<Memo>();

        //SELECT * FROM memo mm, category ct WHERE ct.id = category_id AND mm.category_id = ct.id;
        String selectQuery = "SELECT  mm.*, ct." + KEY_CATEGORY_NAME + " FROM " + TABLE_MEMO + " mm, " + TABLE_CATEGORY + " ct "
                + "WHERE ct." + KEY_ID + " = " + category_id
                + " AND mm." + KEY_CATEGORY_ID + " = " + "ct." + KEY_ID
                + " AND CAST(strftime('%s', " + KEY_DATE + ") AS integer )"
                + " + (60 * 60 * CAST(strftime('%H', " + KEY_TIME +") AS integer))"
                + " + (60 * CAST(strftime('%M', " + KEY_TIME +") AS INTEGER))"
                + " + 60 < CAST(strftime('%s', 'now', 'localtime') AS integer) "
                + "order by " + KEY_DATE + " desc, " + KEY_TIME +" desc";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Memo mm = new Memo();
                mm.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                mm.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                mm.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
                mm.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                mm.setAlarmSetting(c.getInt(c.getColumnIndex(KEY_ALARMSETTING)));
                mm.setCategory_id(c.getInt(c.getColumnIndex(KEY_CATEGORY_ID)));
                mm.setCategory_name(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));

                // adding to memo list
                memos.add(mm);
            } while (c.moveToNext());
        }

        return memos;
    }


    /*
     * getting all memos under single content
     * */
    public ArrayList<Memo> getAllMemosByContent(String content) {
        ArrayList<Memo> memos = new ArrayList<Memo>();

        //SELECT * FROM memo WHERE content like '%content%'
        String selectQuery = "SELECT  mm.*, ct." + KEY_CATEGORY_NAME + " FROM " + TABLE_MEMO + " mm, " + TABLE_CATEGORY + " ct "
                + "WHERE mm." + KEY_CATEGORY_ID + " = " + "ct." + KEY_ID
                + " AND mm." + KEY_CONTENT + " LIKE " + "'%" + content + "%'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Memo mm = new Memo();
                mm.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                mm.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                mm.setTime(c.getString(c.getColumnIndex(KEY_TIME)));
                mm.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                mm.setAlarmSetting(c.getInt(c.getColumnIndex(KEY_ALARMSETTING)));
                mm.setCategory_id(c.getInt(c.getColumnIndex(KEY_CATEGORY_ID)));
                mm.setCategory_name(c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)));

                // adding to memo list
                memos.add(mm);
            } while (c.moveToNext());
        }

        return memos;
    }

    /*
     * Updating a memo
     */
    public int updateMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, memo.getDate());
        values.put(KEY_TIME, memo.getTime());
        values.put(KEY_CONTENT, memo.getContent());
        values.put(KEY_ALARMSETTING, memo.getAlarmSetting());
        values.put(KEY_CATEGORY_ID, memo.getCategory_id());

        // updating row
        return db.update(TABLE_MEMO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(memo.getId()) });
    }

    /*
     * Deleting a memo
     */
    public void deleteMemo(long memo_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMO, KEY_ID + " = ?",
                new String[] { String.valueOf(memo_id) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
