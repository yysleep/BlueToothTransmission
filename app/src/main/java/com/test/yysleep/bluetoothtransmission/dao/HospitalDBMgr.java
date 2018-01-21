package com.test.yysleep.bluetoothtransmission.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.test.yysleep.bluetoothtransmission.model.DbModel;

import java.util.List;

/**
 * 数据库 Manager
 *
 * @author yysleep
 */

public class HospitalDBMgr<T extends DbModel> {

    private static final String TAG = "HospitalDBMgr";
    private final String DB_NAME = "hospital.db";
    private final int DB_VERSION = 1;
    private SQLiteOpenHelper mHelper;
    private SQLiteDatabase mDbInstance;
    private static volatile HospitalDBMgr instance;

    private HospitalDBMgr() {

    }

    public static HospitalDBMgr getInstance() {
        if (instance == null) {
            synchronized (HospitalDBMgr.class) {
                if (instance == null)
                    instance = new HospitalDBMgr();
            }
        }
        return instance;
    }

    public void init(Context context) {
        mHelper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                HospitalDao.getInstance().init(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };
        mDbInstance = mHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDbInstance() {
        return mDbInstance;
    }

    public void insert(String table, DbModel model) {
        if (table == null || model == null)
            return;
        switch (table) {
            case HospitalDao.TABLE_HOSPITAL:
                HospitalDao.getInstance().insert(mDbInstance, model);
                break;

            default:
                break;
        }
    }

    public void delete(String table, DbModel model) {
        if (table == null || model == null)
            return;
        switch (table) {
            case HospitalDao.TABLE_HOSPITAL:
                HospitalDao.getInstance().delete(mDbInstance, model);
                break;

            default:
                break;
        }
    }

    public void update(String table, DbModel model) {
        if (table == null || model == null)
            return;
        switch (table) {
            case HospitalDao.TABLE_HOSPITAL:
                HospitalDao.getInstance().update(mDbInstance, model);
                break;

            default:
                break;
        }
    }

    public List<DbModel> query(String table, boolean firstInit, boolean outside) {
        List<DbModel> models = null;
        if (table == null)
            return null;
        switch (table) {
            case HospitalDao.TABLE_HOSPITAL:
                models = HospitalDao.getInstance().query(mDbInstance, firstInit, outside);
                break;
        }
        return models;
    }
}
