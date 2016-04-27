package com.zj.weddingtool.base.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.zj.weddingtool.R;

public class TextListDialog extends BottomListDialog<String> {

    private Context context;
    private IObjectSelectListener listener;

    private String title = "";

    public TextListDialog(Activity context, String title) {
        super(context);
        this.title = title;
    }

    public void setDialogTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cancel.setText("返 回");
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, String item) {
        if (listener != null)
            listener.onObjectSelected(item);
        this.dismiss();
    }

    @Override
    protected View getView(int position, View convertView, String item, ViewGroup parent) {
        if ( convertView == null ) {
            convertView = inflater.inflate( R.layout.dlg_list_item, parent, false );
        }

        if ( convertView instanceof TextView) {
            TextView tv = (TextView) convertView;
            tv.setText(item);
        }
        return convertView;
    }

    @Override
    protected View getTitleView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.dlg_title_label, parent, false);
        if(title.equals("")) {
            title = "请选择";
        }
        if (view instanceof TextView) {
            TextView title = (TextView) view;
            title.setText(this.title);
        }
        return view;
    }

    public void setSelectListener(IObjectSelectListener listener) {
        this.listener = listener;
    }

    public static interface IObjectSelectListener {
        void onObjectSelected(String Object);
    }


}
