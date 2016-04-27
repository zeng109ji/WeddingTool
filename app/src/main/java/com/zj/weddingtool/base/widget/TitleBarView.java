package com.zj.weddingtool.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zj.weddingtool.R;

public class TitleBarView extends RelativeLayout {

    private Button leftBtn;
    private Button rightBtn;
    private TextView tvTitle;

    private int titleTextResId;
    private int leftBtnTextResId;
    private int rightBtnTextresId;
    private int leftBtnImgResId;
    private int rightBtnImgResId;


    public TitleBarView(Context context) {
        super(context);
        View.inflate(context, R.layout.title_bar, this);
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.title_bar, this);
        getAttrs(context, attrs);
        initView();
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.title_bar, this);
        getAttrs(context, attrs);
        initView();
    }

    /**
     * 得到属性值
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBarViewAttrs);
        titleTextResId = ta.getResourceId(R.styleable.TitleBarViewAttrs_titleText, -1);
        leftBtnTextResId = ta.getResourceId(R.styleable.TitleBarViewAttrs_leftBtnText, -1);
        rightBtnTextresId = ta.getResourceId(R.styleable.TitleBarViewAttrs_rightBtnText, -1);
        leftBtnImgResId = ta.getResourceId(R.styleable.TitleBarViewAttrs_leftBtnImg, -1);
        rightBtnImgResId = ta.getResourceId(R.styleable.TitleBarViewAttrs_rightBtnImg, -1);
        ta.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        leftBtn = (Button) findViewById(R.id.titlebar_left_btn);
        rightBtn = (Button) findViewById(R.id.titlebar_right_btn);

        tvTitle = (TextView) findViewById(R.id.titlebar_title_tv);

        initView();
    }

    private void initView() {
        if (null == leftBtn || null == tvTitle || null == rightBtn) {
            return;
        }

        tvTitle.setVisibility(View.VISIBLE);
        leftBtn.setVisibility(View.GONE);
        rightBtn.setVisibility(View.GONE);


        if (titleTextResId != -1) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(titleTextResId);
        } else {
            tvTitle.setVisibility(View.GONE);
        }


        if (leftBtnTextResId != -1) {
            leftBtn.setVisibility(VISIBLE);
            leftBtn.setText(leftBtnTextResId);
        } else {
            leftBtn.setVisibility(View.GONE);
        }


        if (leftBtnImgResId != -1) {
            leftBtn.setVisibility(View.VISIBLE);
            leftBtn.setBackgroundResource(leftBtnImgResId);
        } else {
            leftBtn.setVisibility(View.GONE);
        }

        if (rightBtnTextresId != -1) {
            rightBtn.setVisibility(View.VISIBLE);
            rightBtn.setText(rightBtnTextresId);
        } else {
            rightBtn.setVisibility(View.GONE);
        }


        if (rightBtnImgResId != -1) {
            rightBtn.setVisibility(View.VISIBLE);
            rightBtn.setBackgroundResource(rightBtnImgResId);
        } else {
            rightBtn.setVisibility(View.GONE);
        }

    }

    public TextView setTitle(String text) {
        tvTitle.setText(text);
        return tvTitle;
    }

    public TextView setTitle(int textId) {
        tvTitle.setText(textId);
        return tvTitle;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

}
