package chapter.android.aweme.ss.com.chapter1.xmlparser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import chapter.android.aweme.ss.com.chapter1.R;

public class XmlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml);
        TextView tv_result = findViewById(R.id.tv_xml);
        //load data from assets/data.xml
        try {
            InputStream assetInput = getAssets().open("data.xml");
            List<Message> messages = PullParser.pull2xml(assetInput);
            for (Message message : messages) {
                tv_result.append(message.toString());
                tv_result.append("\n\n");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
