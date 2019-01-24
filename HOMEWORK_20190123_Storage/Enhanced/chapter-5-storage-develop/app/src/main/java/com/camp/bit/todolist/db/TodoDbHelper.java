package com.camp.bit.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "TodoDbHelper";
    // TODO 2 定义数据库名、版本；创建数据库
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "todo5.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            //执行SQL语句
            db.execSQL(TodoContract.SQL_CREATE_ENTRIES);
            Log.d(TAG, "Create Success");
        } catch (Exception e){
            Log.d(TAG, "Create Error!");
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++){
            switch (i){
                case 1:
                    try{
                        db.execSQL("ALTER TABLE " + TodoContract.Todo.TABLE_NAME + " ADD "
                        + TodoContract.Todo.COLUMN_NAME_PRIORITY + " INTEGER");
                        Log.d(TAG, "Upgrade Success!");
                    } catch (Exception e){
                        Log.d(TAG, "onUpgrade() called with: db = [" + db + "], oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
