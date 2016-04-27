package com.zj.weddingtool.base.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.zj.weddingtool.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BottomListDialog<E> extends Dialog {

    private ListView listview;
    protected Activity activity;
    private BottomAdapter adapter;
    protected LayoutInflater inflater;
    protected Button cancel;

    public BottomListDialog(Activity context) {
        super(context, R.style.SelectListDialog);
        this.activity = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setGravity(Gravity.BOTTOM);

        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        LayoutParams lay = this.getWindow().getAttributes();
        lay.width = wm.getDefaultDisplay().getWidth();
        lay.height = wm.getDefaultDisplay().getHeight() - getStatusHeight(activity);

        getWindow().setAttributes(lay);
        setContentView(R.layout.dlg_bottom_list);
        listview = (ListView) findViewById(R.id.bottom_list);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BottomListDialog.this.onItemClick(parent, view, position, adapter.getItem(position));
            }
        });
        if (adapter != null) listview.setAdapter(adapter);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(dismiss);
        findViewById(R.id.dialog_frame).setOnClickListener(dismiss);

        ViewGroup top = (ViewGroup) findViewById(R.id.top);
        View title = getTitleView(top);
        if (title != null)
            top.addView(title);
    }

    private int getStatusHeight(Activity context) {
        int y = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            y = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.getMessage();
        }
        return y;
    }

    public void setData(List<E> data) {
        if (adapter == null) {
            adapter = new BottomAdapter();
        }
        adapter.setList(data);

        if (listview != null) {
            if (listview.getAdapter() == null) {
                listview.setAdapter(adapter);
            }
        }
    }

    private View.OnClickListener dismiss = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            BottomListDialog.this.dismiss();
        }
    };

    protected abstract void onItemClick(AdapterView<?> parent, View view, int position, E item);

    protected abstract View getView(int position, View convertView, E item, ViewGroup parent);

    protected abstract View getTitleView(ViewGroup parent);

    private class BottomAdapter extends BaseAdapter {

        private List<E> data = new ArrayList<E>();

        private int listHeight;

        public BottomAdapter() {
        }

        public void setList(List<E> data_source) {
            data.clear();
            if (data_source != null) {
                data.addAll(data_source);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public E getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null && listHeight == 0) {
                int itemHeight = convertView.getLayoutParams().height;
                if (parent instanceof ListView) {
                    ListView listview = (ListView) parent;
                    listHeight = (int) ((itemHeight + listview.getDividerHeight()) * 6.5);
                    if (getCount() > 6) {
                        listview.getLayoutParams().height = listHeight;
                    }
                }
            }
            return BottomListDialog.this.getView(position, convertView, getItem(position), parent);
        }

    }

}
