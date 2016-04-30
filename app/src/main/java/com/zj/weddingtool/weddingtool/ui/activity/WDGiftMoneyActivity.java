package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.weddingtool.model.UserMe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * @date 2016-4-19
 * @author zj
 */
public class WDGiftMoneyActivity extends BaseActivity {

    public static final String TAG = "JHLiJinActivity";
    public static final String EXTRA_KEY_USER_ID = "";

    public ListEditTextAdapter mlAdapter;
    private UserMe showUser;
    private UserMe curUser;

    private ListView lv;
    private TextView tx;
    private TextView tx_num;
    private Button btn_add;

    private List<HashMap<String, Object>> mlist = null;

    private Integer mymoney_totel;
    private ArrayList<String> mlijinname=new ArrayList<String>();
    private ArrayList<Integer> mlijin=new ArrayList<Integer>();
    private ArrayList<Boolean> mtuilijin=new ArrayList<Boolean>();

    private Integer downloadFlag = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jhlijin);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //for (int i = 0; i < getResources().getStringArray(R.array.jhyusuan).length; i++) {
        //    mallarray.add(getResources().getStringArray(R.array.jhyusuan)[i]);
        //}

        findAll();//从服务器取数据需要时间，保证能完成数据下载的话最好延时一段时间，然后再调用initView()



        mHandler.sendEmptyMessageDelayed(0, 1000);
        //initView();

	}

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    initView();
                    break;
            }
        }
    };

    //初始化视图控件
    private void initView() {

        /* 实例化各个控件 */
        lv = (ListView) findViewById(R.id.listView_lijin);

        tx = (TextView) findViewById(R.id.jhlijin_heji);
        tx_num = (TextView) findViewById(R.id.jhlijin_heji_num);

        btn_add = (Button) findViewById(R.id.itemadd);

        // 为Adapter准备数据
        mlist = new ArrayList<HashMap<String, Object>>();

        mymoney_totel = 0;

        for (int i = 0; i < mlijinname.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("lijin_name", mlijinname.get(i));
            map.put("lijin_money", mlijin.get(i));
            map.put("lijin_back",mtuilijin.get(i));
            mlist.add(map);

            mymoney_totel += mlijin.get(i);//amymoney.toArray(new Integer[amymoney.size()])[i];
        }
        tx.setText("总共 "+ String.valueOf(mlijinname.size())+" 份礼金");
        tx_num.setText(String.valueOf(mymoney_totel));

        // 实例化自定义的MyAdapter
        mlAdapter = new ListEditTextAdapter(this, mlist, new String[] { "lijin_name"},new int[] {R.id.lijin_name},mlijin,tx,tx_num);

        // 绑定Adapter
        lv.setAdapter(mlAdapter);

        //列表点击响应
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                final ListEditTextHolder holder = (ListEditTextHolder) view.getTag();

                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.edit_user_dialog,
                        (ViewGroup) findViewById(R.id.edit_user_dialog_view));

                final int p = position;

                final CheckBox tui = (CheckBox) layout.findViewById(R.id.ck_back_money);
                tui.setChecked(mtuilijin.get(p));

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(holder.ItemLiJinName.getText() + " 礼金：￥" + holder.ItemLiJinMoney.getText())
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mtuilijin.set(p, tui.isChecked());

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("lijin_name", holder.ItemLiJinName.getText());
                        map.put("lijin_back", tui.isChecked());
                        mlist.set(p, map);

                        mlAdapter.notifyDataSetChanged();

                    }
                });
                builder.show();
            }

        });

        //列表长按响应
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                final ListEditTextHolder holder = (ListEditTextHolder) view.getTag();
                final int p = position;
                Log.d(TAG, "LongPress");
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("确定要删除 " + holder.ItemLiJinName.getText() + "?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        mlijinname.remove(p);
                        mlijin.remove(p);
                        mtuilijin.remove(p);
                        mlist.remove(p);

                        mlAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();

                return true;
            }
        });

        //”新增“按键的响应
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.create_user_dialog,
                        (ViewGroup) findViewById(R.id.create_user_dialog_view));

                //final EditText inputServer = new EditText(view.getContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("增加新项目").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        EditText name = (EditText)layout.findViewById(R.id.text_name);
                        EditText money = (EditText)layout.findViewById(R.id.text_money);

                        mlijinname.add(name.getText().toString());
                        mlijin.add(Integer.parseInt(money.getText().toString()));
                        mtuilijin.add(false);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("lijin_name", name.getText().toString());
                        map.put("lijin_money", Integer.parseInt(money.getText().toString()));
                        map.put("lijin_back",false);
                        mlist.add(map);

                        tx.setText("总共 " + String.valueOf(mlijinname.size()) + " 份礼金");

                        mlAdapter.notifyDataSetChanged();

                    }
                });
                builder.show();
            }
        });


    }

    //填充数据
    private void initDate(Integer[] ints,Boolean[] boos) {

    //    amymoney = new Integer[mallarray.size()];

        for(int i=0;i<mlijinname.size();i++) {
            Log.d(TAG, "initDate从云端同步下的数据,任务信息第 " + i + "任务为 " + ints[i]);
            mlijin.add(ints[i]);

            if(boos[i] == true) {
                mtuilijin.add(true);
            }
            else if(boos[i] == false){
                mtuilijin.add(false);
            }

        }

    }

    private void findAll() {
        Log.d(TAG, "从服务器获取用户信息");

        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
        BmobQuery<UserMe> query = new BmobQuery<>();
        query.getObject(this, curUser.getObjectId(), new GetListener<UserMe>() {
            @Override
            public void onSuccess(UserMe userme) {
                Integer[] temp = {};
                String[] temps = {};
                Boolean[] tempss = {};
                ArrayList<Integer> list1 = new ArrayList<Integer>();
                ArrayList<String> list2 = new ArrayList<String>();
                ArrayList<Boolean> list3 = new ArrayList<Boolean>();

                list1 = userme.getMylijin();
                list2 = userme.getMylijinname();
                list3 = userme.getTuilijin();

                if(list2 == null)
                {
                    Log.d(TAG, "没有信息在云端， mallarray " + mlijinname.size() + "  " + " 条");
                }else {
                    temps = (String[]) list2.toArray(new String[list2.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list2.size() + "  " + " 条");

                    for (int i = 0; i < temps.length; i++) {
                        mlijinname.add(temps[i]);
                    }
                }

                if(list1 == null)
                {
                    Log.d(TAG, "没有信息在云端");
                }else {
                    temp = (Integer[]) list1.toArray(new Integer[list1.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条" + userme.getObjectId());
                }

                if(list3 == null)
                {
                    Log.d(TAG, "没有信息在云端");
                }else {
                    tempss = (Boolean[]) list3.toArray(new Boolean[list3.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list3.size() + "  " + " 条" + userme.getObjectId());
                }

                if((list1 == null && list2 == null && list3 == null)) {
                    downloadFlag = 1;
                    Log.d(TAG, "无原始数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }else if(list1.size() == list2.size() && list1.size() > 0){
                    downloadFlag = 1;
                    Log.d(TAG, "有数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }

                initDate(temp,tempss);
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast("从云端同步数据获取失败，请稍后再试");
            }
        });
    }

    private void saveAll(){
        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);

        Log.d(TAG, "查询数据标志位 downloadFlag=" + downloadFlag);
        if(downloadFlag < 0)//如果读取数据库不成功，就不要上传空数据到云端，以免冲掉数据
            return;

        Boolean[] tuitemp = mtuilijin.toArray(new Boolean[mtuilijin.size()]);
        Integer[] moneytemp = mlijin.toArray(new Integer[mlijin.size()]);

        ArrayList list1 = new ArrayList();
        for(int i=0;i<mlijinname.size();i++) {
            list1.add(mlijinname.get(i));
        }

        ArrayList list2 = new ArrayList();
        for(int i=0;i<mlijin.size();i++) {
            Log.d(TAG, "上传数据到云,任务信息第 " + i + "任务为 " + moneytemp[i]);
            list2.add(mlijin.get(i));//toArray(new String[mallarray.size()])[i]);//mallarray是ArrayList，而add的参数必须是数组，所以此处要转换一下
        }

        ArrayList list3 = new ArrayList();
        for(int i=0;i<mtuilijin.size();i++){
            Log.d(TAG, "上传数据到云,任务信息第 " + i + "任务为 " + tuitemp[i]);
            list3.add(mtuilijin.get(i));
        }

        UserMe query = new UserMe();
        query.setMylijinname(list1);
        query.setMylijin(list2);
        query.setTuilijin(list3);
        query.update(this,curUser.getObjectId() , new UpdateListener() {    //query.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                //   ToastUtils.showToast("添加数据成功，返回objectId为：");
                Log.d(TAG, "更新数据成功");
            }

            @Override
            public void onFailure(int code, String arg0) {
                // 添加失败
                Log.d(TAG, "更新数据失败");
            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAll();
    }

    @Override
	public void onResume() {
	    super.onResume();
	}

    @Override
	public void onPause() {
        super.onPause();
        saveAll();
	}

    private class ListEditTextAdapter extends BaseAdapter {

        public HashMap<Integer, String> howMoney = null;
        private Context mcontext;
        private LayoutInflater mInflater = null;
        private List<HashMap<String, Object>> mmlist = null;
        private String keyString[] = null;
        private String itemString = null; // 记录每个item中textview的值
        private int idValue[] = null;// id值
        private ArrayList<Integer> mlijin = null;
        private TextView mtx = null;
        private TextView mtx_num = null;

        public ListEditTextAdapter(Context context, List<HashMap<String, Object>> list, String[] from, int[] to,ArrayList<Integer> mlijin,TextView tx,TextView tx_num) {
            this.mcontext = context;
            this.mmlist = list;
            this.mtx = tx;
            this.mtx_num = tx_num;
            mInflater = LayoutInflater.from(context);
            howMoney = new HashMap<Integer, String>();
            this.mlijin = mlijin;
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);

            initDate();
        }

        public void initDate() {
            for (int i = 0; i < mmlist.size(); i++) {
                getHowMoney().put(i, mlijin.get(i).toString());
            }
            //Log.d(TAG, "从服务器获取用户信息mmlist是："+mmlist.size()+"同时myCompleted是"+myCompleted.length);
        }

        public void setTotalMoney() {
            Integer mtotal = 0;
            Integer[] tempint = mlijin.toArray(new Integer[mlijin.size()]);
            for(int i=0;i<mmlist.size();i++) {
                mtotal += tempint[i];
            }

            mtx_num.setText(String.valueOf(mtotal));
        }

        @Override
        public int getCount() {
            return mmlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mmlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListEditTextHolder holder = null;
            if (holder == null) {
                holder = new ListEditTextHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_lijin_list, null);
                }

                holder.ItemLiJinName = (TextView) convertView.findViewById(R.id.lijin_name);
                holder.ItemLiJinMoney = (TextView) convertView.findViewById(R.id.lijin_money);
                holder.ItemLiJinBack = (TextView) convertView.findViewById(R.id.lijin_back);
                convertView.setTag(holder);
            } else {
                holder = (ListEditTextHolder) convertView.getTag();
            }

            final int p = position;
            final ListEditTextHolder myholder = holder;

            HashMap<String, Object> map = mmlist.get(position);
            if (map != null) {
                itemString = (String) map.get(keyString[0]);
                holder.ItemLiJinName.setText(itemString);

                Boolean isTui = (Boolean)map.get("lijin_back");
                if(isTui) {
                    holder.ItemLiJinBack.setText("礼金已退");
                    holder.ItemLiJinBack.setTextColor(Color.parseColor("#3BBD79"));

                }
                else{
                    holder.ItemLiJinBack.setText("礼金未退");
                    holder.ItemLiJinBack.setTextColor(Color.parseColor("#999999"));
                }
            }

            holder.ItemLiJinMoney.setText(String.valueOf(mlijin.get(position)));

            setTotalMoney();

            return convertView;
        }


        public HashMap<Integer, String> getHowMoney() {
            return howMoney;
        }

        public void setHowMoney(HashMap<Integer, String> howMoney) {
            mlAdapter.howMoney = howMoney;
        }
    }

    public static class ListEditTextHolder {
        public TextView ItemLiJinName;
        public TextView ItemLiJinMoney;
        public TextView ItemLiJinBack;
    }

}

