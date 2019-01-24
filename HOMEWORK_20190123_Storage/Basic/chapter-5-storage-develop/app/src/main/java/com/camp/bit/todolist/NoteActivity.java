package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NodeActivity";
    private EditText editText;
    private Button addBtn;
    //定义DbHelper对象和数据库引用对象
    private TodoDbHelper mDbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        //取得数据库
        mDbHelper = new TodoDbHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private boolean saveNote2Database(String content) {
        // TODO 3 插入一条新数据，返回是否插入成功
        try{
            //建立一个ContentValues对象
            ContentValues values = new ContentValues();
            //插入时间date，状态state，内容content
            values.put(TodoContract.Todo.COLUMN_NAME_DATE, System.currentTimeMillis());
            values.put(TodoContract.Todo.COLUMN_NAME_STATE, 0);
            values.put(TodoContract.Todo.COLUMN_NAME_CONTENT, content);
            long new_id = db.insert(TodoContract.Todo.TABLE_NAME, null, values);
            if(new_id >= 0)
            {
                return true;
            }
        } catch (Exception e){
            Log.d(TAG, "Insert Error!");
            e.printStackTrace();
        }
        return false;
    }
}
