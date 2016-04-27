package com.zj.weddingtool.base.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.zj.weddingtool.R;

/**
 * Created by ??? on 2015/4/15.
 */
public class BaseActivity extends UmengActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if(null == dialog) {
            dialog = new ProgressDialog(this);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void showProgressDialog() {
        if(dialog!=null && !dialog.isShowing())
            dialog.show();
    }

    public void dismissProgressDialog() {
        if(dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }

    /**
     *  设置 Dialog 颜色
     * @param context
     * @param dialog
     * @param color
     */
    protected  void dialogTitleLineColor(Context context, Dialog dialog, int color) {
        String dividers[] = {
                "android:id/titleDividerTop", "android:id/titleDivider"
        };

        for (int i = 0; i < dividers.length; ++i) {
            int divierId = context.getResources().getIdentifier(dividers[i], null, null);
            View divider = dialog.findViewById(divierId);
            if (divider != null) {
                divider.setBackgroundColor(color);
            }
        }
    }

    protected void dialogTitleLineColor(Context context, Dialog dialog) {
        if (dialog != null) {
            dialogTitleLineColor(context, dialog, context.getResources().getColor(R.color.green));
        }
    }

    protected final void dialogTitleLineColor(Dialog dialog) {
        dialogTitleLineColor(this, dialog);
    }

}
