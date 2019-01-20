package chapter.android.aweme.ss.com.chapter1.recycleview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import chapter.android.aweme.ss.com.chapter1.R;
import chapter.android.aweme.ss.com.chapter1.xmlparser.Message;

/**
 *
 */
public class CustomListView extends ViewGroup {


    public CustomListView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * 方式1：直接设置
     * 面临的问题，itemview和 CustomListView 严重耦合，且数据类型也必须明确，
     *
     * @param messageList
     */
    public void setDataSource(List<Message> messageList) {
        for (int i = 0; i < messageList.size(); i++) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            View itemview = mInflater.inflate(R.layout.support_simple_spinner_dropdown_item, this, false);
            addView(itemview);
        }
    }

    /**
     * 方式2，通过抽象的方式,依赖倒置，itemview和CustomListView 彻底解耦
     */
    abstract class AbstractAdapter {

        abstract View getView(int position);

        abstract int getViewCounts();

    }

    private AbstractAdapter mAdapter;

    public void setAdapter(AbstractAdapter adapter) {
        mAdapter = adapter;

        //add views
        for (int i = 0; i < adapter.getViewCounts(); i++) {
            View child = mAdapter.getView(i);
            addView(child);
        }
    }

}
