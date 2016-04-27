package com.zj.weddingtool.base.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zj.weddingtool.base.util.LocalBroadcasts;


public abstract class BaseFragment extends Fragment {

    // root view是否已经创建，如果没有创建，而想使用应该创建后才能使用的方法时，将抛出异常
    private boolean mIsRootViewCreated = false;
    // 根View，外部提供的View——Activity的根View其实是内置的FrameLayout
    private View mRootView;
    /**
     * 持有该Fragment的FragmentActivity的引用
     */
    protected FragmentActivity fragmentActivity;

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(android.app.Activity)} and before
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 不推荐再重写此方法.所以加上了final<br/><br/>
     * Not recommend that you override this method.<br/><br/>
     * use {@link #onViewCreated(android.view.View, android.os.Bundle)} to do some thing
     *
     * @see {@link #onViewCreated(android.view.View, android.os.Bundle)}
     */
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        Log.i("BaseFragment", "execute onCreateView!!! ");
        // 为了实现findViewById
        mRootView = inflater.inflate(provideLayoutResId(), container, false);
        // 保证RootView加载完成
        mIsRootViewCreated = true;
        return mRootView;
    }

    /**
     * Called immediately after {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 给一些变量赋值
        fragmentActivity = getActivity();

        // 细分生命周期
        initView(mRootView);
        initListener();
        initBroadcast();
        initData(mRootView, savedInstanceState);
    }

    /**
     * 获取跟View
     */
    protected View getRootView() {
        return mRootView;
    }


    /**
     * 提供给BaseActivity，并在initView方法调用之前setContentView(int id)
     *
     * @return 如果不通过BaseActivity自动设置ContentView，则-1
     */
    protected abstract int provideLayoutResId();


    /**
     * 步骤一：初始化View，比如findViewById等操作
     */
    protected abstract void initView(View rootView);

    /**
     * 步骤二：初始化View的Listener，比如onClick等监听器
     */
    protected abstract void initListener();

    /**
     * 步骤三：初始化数据
     */
    protected abstract void initData(View rootView, Bundle savedInstanceState);

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private BroadcastReceiver mPrReceiver;

    protected void initBroadcast() {
        String[] actions = provideBroadcastActions();
        if (null == actions || actions.length == 0) {
            return;
        }
        registerLocalBroadcastReceiver(mPrReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveBroadcast(intent.getAction(), intent);
            }
        }, actions);
    }

    protected String[] provideBroadcastActions() {
        return null;
    }

    /**
     * 根据{@link #provideBroadcastActions()}中提供的
     *
     * @param action Intent中的action，方便调用
     * @param intent 广播Intent
     */
    protected void onReceiveBroadcast(String action, Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLocalReceiver(mPrReceiver);
    }

    protected void registerLocalBroadcastReceiver(BroadcastReceiver receiver, String... actions) {
        LocalBroadcasts.registerLocalReceiver(receiver, actions);
    }

    protected void registerLocalBroadcastReceiver(BroadcastReceiver receiver, IntentFilter... filters) {
        LocalBroadcasts.registerLocalReceiver(receiver, filters);
    }

    protected void unregisterLocalReceiver(BroadcastReceiver receiver) {
        LocalBroadcasts.unregisterLocalReceiver(receiver);
    }
}
