package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 1 定义表结构和 SQL 语句常量
    //创建数据库，设置ID为自增的
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Todo.TABLE_NAME + " (" +
                    Todo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Todo.COLUMN_NAME_DATE + " LONG, " +
                    Todo.COLUMN_NAME_STATE + " INTEGER, " +
                    Todo.COLUMN_NAME_CONTENT + " TEXT)";
    //删除数据库
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Todo.TABLE_NAME;

    private TodoContract() {

    }

    public static class Todo implements BaseColumns{
        //定义表名
        public static final String TABLE_NAME = "todo5";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }
}
