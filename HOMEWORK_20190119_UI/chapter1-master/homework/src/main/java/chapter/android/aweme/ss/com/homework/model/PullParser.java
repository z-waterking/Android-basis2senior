package chapter.android.aweme.ss.com.homework.model;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Pull解析Xml
 */
public class PullParser {

    /**
     * @param is inputStream
     * @return
     * @throws Exception
     */
    public static List<Message> pull2xml(InputStream is) throws Exception {
        List<Message> list = null;
        Message msg = null;
        //创建xmlPull解析器
        XmlPullParser parser = Xml.newPullParser();
        ///初始化xmlPull解析器
        parser.setInput(is, "utf-8");
        //读取文件的类型
        int type = parser.getEventType();
        //无限判断文件类型进行读取
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                //开始标签
                case XmlPullParser.START_TAG:
                    if ("messages".equals(parser.getName())) {
                        list = new ArrayList<>();
                    } else if ("message".equals(parser.getName())) {
                        msg = new Message();
                    } else if ("title".equals(parser.getName())) {
                        //获取title属性
                        String isOfficial = parser.getAttributeValue(null, "isOfficial");
                        msg.setOfficial("true".equals(isOfficial));
                        String title = parser.nextText();
                        msg.setTitle(title);
                    } else if ("time".equals(parser.getName())) {
                        //time
                        String time = parser.nextText();
                        msg.setTime(time);
                    } else if ("hashtag".equals(parser.getName())) {
                        //hashTag
                        String hashTag = parser.nextText();
                        msg.setDescription(hashTag);
                    } else if ("icon".equals(parser.getName())) {
                        //icon
                        String icon = parser.nextText();
                        msg.setIcon(icon);
                    }
                    break;
                //结束标签
                case XmlPullParser.END_TAG:
                    if ("message".equals(parser.getName())) {
                        list.add(msg);
                    }
                    break;
            }
            //继续往下读取标签类型
            type = parser.next();
        }
        return list;
    }
}
