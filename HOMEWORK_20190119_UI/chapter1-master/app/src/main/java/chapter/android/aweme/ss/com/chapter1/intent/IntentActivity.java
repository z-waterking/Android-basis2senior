package chapter.android.aweme.ss.com.chapter1.intent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import chapter.android.aweme.ss.com.chapter1.R;

public class IntentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    private TextView mContactName;
    private Button mButtonSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);
        mContactName = findViewById(R.id.tv_contact);
        mButtonSetting = findViewById(R.id.btn_setting);
    }

    /**
     * 隐式intent预埋
     *
     * @param view
     */
    public void openBrowser(View view) {
        Intent it = new Intent();
        it.setAction(Intent.ACTION_VIEW);
        it.setData(Uri.parse("http://www.baidu.com"));
        startActivity(it);
    }

    /**
     * 自定义的隐式intent  携带data&type
     *
     * @param view
     */
    public void customImplicit(View view) {
        Intent it = new Intent();
        Uri uri = Uri.parse("ispring://blog.github.net/huohuo");
        it.setDataAndType(uri, "text/plain");
        it.setAction("my_action");
        it.addCategory("my_category");
        startActivity(it);
    }


    /**
     * startActivityForResult with Bundle  显式
     *
     * @param view
     */
    public void wechatAuthorize(View view) {
        Intent intent = new Intent(this, AuthorizeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("douyin_id", "huohuo666");
        bundle.putString("name", "CALL ME 火火");
        bundle.putInt("age", 18);
        intent.putExtra("user", bundle);
        startActivityForResult(intent, REQUEST_CODE);//带着讲 forResult
    }

    /**
     * startActivityForResult 隐式
     * 选择联系人姓名
     *
     * @param view
     */
    public void chooseContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //REQUEST_CODE为自定义的请求码
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == AuthorizeActivity.AUTHOR_CODE) {
            if (data != null) {
                AuthorizeActivity.Resource<String> result = (AuthorizeActivity.Resource) data.getSerializableExtra(AuthorizeActivity.AUTHOR_STATE);
                String authMsg = "";
                switch (result.status) {
                    case ERROR:
                        authMsg = "授权异常";
                        break;
                    case FAILED:
                        authMsg = "授权失败";
                        break;
                    case SUCCESS:
                        authMsg = "授权成功|" + result.data;
                        break;
                }
                Toast.makeText(this, authMsg, Toast.LENGTH_SHORT).show();
                mButtonSetting.setTextColor(getResources().getColor(R.color.colorAccent));
                mButtonSetting.setText(authMsg);
            }
        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            //content://com.android.contacts/contacts/lookup/3640i15de78e48cb38a25/419
            //具体的ContentProvider的内容 由专门的课程讲解，这里只是为了演示效果
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            //查询联系人姓名的方法
            if (cursor != null) {
                String contactName = getContactName(cursor);
                mContactName.setText(contactName);
            }
        }
    }

    private static String getContactName(Cursor cursor) {
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    }

}
