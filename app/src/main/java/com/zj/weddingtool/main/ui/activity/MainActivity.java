package com.zj.weddingtool.main.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.main.manager.LoginManager;
import com.zj.weddingtool.main.ui.fragment.NavigationDrawerFragment;
import com.zj.weddingtool.main.model.User;

/**
 * Created by zj on 15/4/16.
 */
public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavFragment;

    private DrawerLayout mDrawerLayout;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavFragment.setUp(R.id.navigation_drawer, mDrawerLayout);
        onNavigationDrawerItemSelected(0);


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        ToastUtils.showToast(position + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().updUserState(User.USER_STATE_LOGOUT);
    }

    private long exitTime = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showToast("再按一次退出他她工具");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

}
