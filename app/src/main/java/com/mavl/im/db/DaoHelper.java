package com.mavl.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by panwenjuan on 17-6-28.
 */
public class DaoHelper extends SQLiteOpenHelper {

    private volatile static DaoHelper mDaoHelper;

    public DaoHelper(Context context) {
        super(context, DaoConstants.DB_NAME, null,
                DaoConstants.DB_VERSION);
        // TODO Auto-generated constructor stub
    }

    protected synchronized static DaoHelper getInstance(Context context) {
        if (mDaoHelper == null) {
            synchronized (DaoHelper.class) {
                if (mDaoHelper == null) {
                    mDaoHelper = new DaoHelper(context);
                }
            }
        }
        return mDaoHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DaoConstants.CREATE_MESSAGES_TABLE);
        sqLiteDatabase.execSQL(DaoConstants.CREATE_MESSAGES2_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
        }
    }
}
