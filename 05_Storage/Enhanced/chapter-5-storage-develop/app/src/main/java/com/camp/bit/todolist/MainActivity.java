package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    //定义DbHelper对象和数据库引用对象
    private TodoDbHelper mDbHelper;
    private SQLiteDatabase db_read;
    private SQLiteDatabase db_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //取得数据库
        mDbHelper = new TodoDbHelper(getApplicationContext());
        db_read = mDbHelper.getReadableDatabase();
        db_write = mDbHelper.getWritableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy()
    {
        //释放数据库
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 4 从数据库中查询数据，并转换成 JavaBeans
        //如果db为null
        if(db_read == null){
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        //数据库的指针
        Cursor cursor = null;
        try {
            cursor = db_read.query(TodoContract.Todo.TABLE_NAME,
                    new String[]{TodoContract.Todo._ID,
                            TodoContract.Todo.COLUMN_NAME_DATE,
                            TodoContract.Todo.COLUMN_NAME_STATE,
                            TodoContract.Todo.COLUMN_NAME_CONTENT,
                            TodoContract.Todo.COLUMN_NAME_PRIORITY},
                    null, null,
                    null, null,
                            TodoContract.Todo.COLUMN_NAME_PRIORITY+" DESC"
                            );
            //开始获取数据
            while (cursor.moveToNext()) {
                //取得id
                int id = cursor.getInt(cursor.getColumnIndex(TodoContract.Todo._ID));
                //将时间转换回来
                long date_mills = cursor.getLong(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_DATE));
                Date date = new Date(date_mills);
                //创建State对象
                int int_state = cursor.getInt(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_STATE));
                State state = State.from(int_state);
                //获得content
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_CONTENT));
                //获得priority
                int priority = cursor.getInt(cursor.getColumnIndex(TodoContract.Todo.COLUMN_NAME_PRIORITY));
                //根据之前的内容，建立Node
                Note note = new Note(id);
                note.setDate(date);
                note.setState(state);
                note.setContent(content);
                note.setPriority(priority);
                //添加入result
                result.add(note);
            }
            return result;
        } catch (Exception e){
            Log.d(TAG, "查询失败");
            e.printStackTrace();
        }
        return null;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        //根据ID删除数据
        try{
            //取得要删除的id
            long id = note.id;
            String selection = TodoContract.Todo._ID + " LIKE ?";
            String[] selection_args = {Long.toString(id)};
            int count = db_write.delete(
                    TodoContract.Todo.TABLE_NAME,
                    selection,
                    selection_args);
            if(count >= 0){
                Log.d(TAG, "Delete Success!");
                notesAdapter.refresh(loadNotesFromDatabase());
                recyclerView.setAdapter(notesAdapter);
            }
        } catch (Exception e){
            Log.d(TAG, "Delete Error!");
            e.printStackTrace();
        }
    }

    private void updateNode(Note note) {
        // 更新数据
        //根据ID更新内容
        try{
            //取得需要更新的ID
            long id = note.id;
            ContentValues values = new ContentValues();
            values.put("state", note.getState().intValue);
            //构建筛选规则
            String selection = TodoContract.Todo._ID + " LIKE ?";
            String[] selection_args = {Long.toString(id)};
            int count = db_write.update(
                    TodoContract.Todo.TABLE_NAME,
                    values,
                    selection,
                    selection_args);
            if(count >= 0){
                Log.d(TAG, "Update Success");
                notesAdapter.refresh(loadNotesFromDatabase());
                recyclerView.setAdapter(notesAdapter);
            }
        } catch (Exception e){
            Log.d(TAG, "Update Error!");
            e.printStackTrace();
        }
    }

}
