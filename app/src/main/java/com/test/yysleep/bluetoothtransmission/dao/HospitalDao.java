package com.test.yysleep.bluetoothtransmission.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.test.yysleep.bluetoothtransmission.model.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 医院用户数据库
 *
 * @author yysleep
 */

public class HospitalDao extends YMBaseDao {

    private static final String TAG = "HospitalDao";

    public static final String TABLE_HOSPITAL = "hospital";

    public static final String COL_ID = "_id";
    ;
    public static final String COL_URL = "url";

    public static final String SQL_TABLE_FAVORITE = " create table "
            + TABLE_HOSPITAL + " ("
            + COL_ID + " integer primary key autoincrement, "

            + COL_URL + " varchar(256), ";

    private static volatile HospitalDao instance;

    private HospitalDao() {

    }

    public static HospitalDao getInstance() {
        if (instance == null) {
            synchronized (HospitalDao.class) {
                if (instance == null)
                    instance = new HospitalDao();
            }
        }
        return instance;
    }

    @Override
    public void init(SQLiteDatabase db) {
        super.init(db);
        db.execSQL(" drop table if exists " + TABLE_HOSPITAL + ";");
        db.execSQL(SQL_TABLE_FAVORITE);
    }

    @Override
    public void upgrade() {
        super.upgrade();
    }

    @Override
    public void insert(SQLiteDatabase db, DbModel model) {
        super.insert(db, model);

        ContentValues values = new ContentValues();

        values.put(COL_URL, "");
        db.insert(TABLE_HOSPITAL, null, values);

    }

    @Override
    public void update(SQLiteDatabase db, DbModel model) {
        super.insert(db, model);

    }

    @Override
    public void delete(SQLiteDatabase db, DbModel model) {
        super.delete(db, model);

    }

    @Override
    public List<DbModel> query(SQLiteDatabase db, boolean firstInit, boolean outside) {

        if (db == null)
            return null;

        Cursor cursor = db.rawQuery("select * from " + TABLE_HOSPITAL, null);
        if (cursor == null)
            return null;

        String url = null;

        List<DbModel> list = new ArrayList<>();
        cursor.close();
        return list;

    }
}
