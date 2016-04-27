package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.base.widget.TitleBarView;
import com.zj.weddingtool.weddingtool.model.UserMe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * @date 2016-4-19
 * @author Stone
 */
public class WDTaskActivity extends BaseActivity {

    public static final String TAG = "JHrenwuActivity";
    public static final String EXTRA_KEY_USER_ID = "";

//    private TitleBarView titleBar;
//    private CircleImageView imgUserIcon;
//    private TextView tvUserName;
//    private Button btnUserPay;

//    private UserMe showUser;
//    private UserMe curUser;


    private ListView lv;
    private ListCheckboxAdapter mlAdapter;
    private List<HashMap<String, Object>> mlist = null;
    private List<String> mlist_tags = null;
    private Boolean[] acompleted;

    private Boolean findDataOK = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jhrenwu);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        acompleted = new Boolean[getResources().getStringArray(R.array.jhrenwu).length];

        findAll();//从服务器取数据需要时间，保证能完成数据下载的话最好延时一段时间，然后再调用initView()
        mHandler.sendEmptyMessageDelayed(0, 800);
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
        /**
        imgUserIcon = (CircleImageView) findViewById(R.id.img_user_info_icon);
        tvUserName = (TextView) findViewById(R.id.tv_user_info_name);
        btnUserPay = (Button) findViewById(R.id.btn_user_info_pay);
        btnUserPay.setText("￥ 1.00 " + btnUserPay.getText());

        btnUserPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Pay
            }
        });
         */

        if(findDataOK == false) {
            finish();
            return;
        }
        /* 实例化各个控件 */
        lv = (ListView) findViewById(R.id.listView_renwu);
        // 为Adapter准备数据
        //initDate();

        mlist = new ArrayList<HashMap<String, Object>>();
        mlist_tags = new ArrayList<String>();

        for (int i = 0; i < getResources().getStringArray(R.array.jhrenwu_tags).length; i++) {
            mlist_tags.add(getResources().getStringArray(R.array.jhrenwu_tags)[i]);
        }

        for (int i = 0; i < getResources().getStringArray(R.array.jhrenwu).length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ren_wu_xijie", getResources().getStringArray(R.array.jhrenwu)[i]);
            map.put("item_checkbox1", acompleted[i]);
            mlist.add(map);
        }
            // 实例化自定义的MyAdapter
            mlAdapter = new ListCheckboxAdapter(this, mlist, new String[] { "ren_wu_xijie", "item_checkbox1" },
                            new int[] {R.id.ren_wu_xijie, R.id.item_checkbox1 },mlist_tags,acompleted);

            // 绑定Adapter
            lv.setAdapter(mlAdapter);

            //listString = new ArrayList<String>();

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long arg3) {
                    final ListCheckViewHolder holder = (ListCheckViewHolder) view.getTag();
                    holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
                    //mmlAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
                    ListCheckboxAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态

                    if (holder.cb.isChecked() == true) {
                        //    listString.add(getResources().getStringArray(R.array.jhrenwu)[position]);
                        holder.ItemRenWuxijie.setTextColor(Color.parseColor("#3bbd79"));
                    } else {
                        //    listString.remove(getResources().getStringArray(R.array.jhrenwu)[position]);
                        holder.ItemRenWuxijie.setTextColor(Color.parseColor("#999999"));

                    }
                }

            });

    }


    //填充数据
    private void initDate(Boolean[] bool) {

        for(int i=0;i<getResources().getStringArray(R.array.jhrenwu).length;i++) {
            acompleted[i] = bool[i];
        //    Log.d(TAG, "initDate从云端同步下的数据,任务信息第 " + i + "任务为 " + acompleted[i]);
        }

    }

    private void findAll() {
        Log.d(TAG, "从服务器获取用户信息");

        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
        BmobQuery<UserMe> query = new BmobQuery<>();
        query.getObject(this, curUser.getObjectId(), new GetListener<UserMe>() {
            @Override
            public void onSuccess(UserMe userme) {
                Boolean[] temp = new Boolean[getResources().getStringArray(R.array.jhrenwu).length];
                ArrayList<Boolean> list1 = new ArrayList<Boolean>();
                list1 = userme.getCompleted();
                if(list1 == null)
                {
                    for (int i = 0; i < getResources().getStringArray(R.array.jhrenwu).length; i++) {
                        temp[i] = false;
                    }
                    Log.d(TAG, "没有信息在云端");
                }else {
                    temp = (Boolean[]) list1.toArray(new Boolean[list1.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条" + userme.getObjectId());
                }

                findDataOK = true;

                initDate(temp);
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast("从云端同步数据获取失败，请稍后再试");
            }
        });
    }

    private void saveAll(){

        if(findDataOK == false)
            return;

        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
        ArrayList list1 = new ArrayList();
        for(int i=0;i<getResources().getStringArray(R.array.jhrenwu).length;i++) {
            acompleted[i] = mlAdapter.getIsSelected().get(i);
            //Log.d(TAG, "上传数据到云,任务信息第 " + i + "任务为 " + acompleted[i]);
            list1.add(acompleted[i]);
        }

        UserMe query = new UserMe();
        query.setCompleted(list1);
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

    public static class ListCheckboxAdapter extends BaseAdapter {

        public static HashMap<Integer, Boolean> isSelected = null;
        private Context mcontext;
        private LayoutInflater mInflater = null;
        private List<HashMap<String, Object>> mmlist = null;
        private List<String> mmlist_tags = null;
        private String keyString[] = null;
        private String itemString = null; // 记录每个item中textview的值
        private int idValue[] = null;// id值
        private Boolean[] myCompleted = null;
        private String keyString_tags[] = null;
        public ListCheckboxAdapter(Context context, List<HashMap<String, Object>> list, String[] from, int[] to,List<String> list_tags, Boolean[] completed) {
            this.mcontext = context;
            this.mmlist = list;
            this.mmlist_tags = list_tags;
            mInflater = LayoutInflater.from(context);
            isSelected = new HashMap<Integer, Boolean>();
            this.myCompleted = completed;
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);
            initDate();
        }

        public void initDate() {
            for (int i = 0; i < mmlist.size(); i++) {
                getIsSelected().put(i, myCompleted[i]);
            }
            //Log.d(TAG, "从服务器获取用户信息mmlist是："+mmlist.size()+"同时myCompleted是"+myCompleted.length);
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
        public boolean isEnabled(int position) {
            if(mmlist_tags.contains(mmlist.get(position).get(keyString[0]).toString())){
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListCheckViewHolder holder = null;
            if (holder == null) {
                holder = new ListCheckViewHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_checkbox_list, null);
                }

                holder.cb = (CheckBox) convertView.findViewById(R.id.item_checkbox1);
                holder.ItemRenWuxijie = (TextView) convertView.findViewById(R.id.ren_wu_xijie);

                if(mmlist_tags.contains(mmlist.get(position).get(keyString[0]).toString())) {
                    holder.cb.setVisibility(View.INVISIBLE);
                    holder.ItemRenWuxijie.setTextSize(20);
                    holder.ItemRenWuxijie.setTextColor(Color.parseColor("#FF8833"));
                }
                else {
                    holder.cb.setVisibility(View.VISIBLE);
                    holder.ItemRenWuxijie.setTextSize(16);
                    holder.ItemRenWuxijie.setTextColor(Color.parseColor("#999999"));
                }

                convertView.setTag(holder);
            } else {
                holder = (ListCheckViewHolder) convertView.getTag();
            }

            HashMap<String, Object> map = mmlist.get(position);
            if (map != null) {
                itemString = (String) map.get(keyString[0]);
                holder.ItemRenWuxijie.setText(itemString);
            //    if(itemString.endsWith(":")){//if(itemString == "G6" || itemString == "G12"){       //(holder.ItemRenWuxijie.getText() == "G6"){
            //        holder.cb.setWidth(0);
            //    }
            }

            final int p = position;

            final ListCheckViewHolder myholder = holder;
            holder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    isSelected.put(p, cb.isChecked()); //每次只点击checkbox时，修改map的值保存状态
                    setIsSelected(isSelected);

                    if (myholder.cb.isChecked() == true && myholder.ItemRenWuxijie.getCurrentTextColor() == Color.parseColor("#999999")) { //每次只点击checkbox时，同时修改本条的配套文字
                        myholder.ItemRenWuxijie.setTextColor(Color.parseColor("#3bbd79"));
                    }
                    else if(myholder.cb.isChecked() == false && myholder.ItemRenWuxijie.getCurrentTextColor() == Color.parseColor("#3bbd79"))
                    {
                        myholder.ItemRenWuxijie.setTextColor(Color.parseColor("#999999"));
                    }
                }
            });

            holder.cb.setChecked(isSelected.get(position));

            if (holder.cb.isChecked() == true && holder.ItemRenWuxijie.getCurrentTextColor() == Color.parseColor("#999999")) { //在此重复这一次的原因就是防止在滚动页面时，文字的position项没有跟着checkbox走，造成上色错乱
                holder.ItemRenWuxijie.setTextColor(Color.parseColor("#3bbd79"));
            }
            else if(holder.cb.isChecked() == false && holder.ItemRenWuxijie.getCurrentTextColor() == Color.parseColor("#3bbd79"))
            {
                holder.ItemRenWuxijie.setTextColor(Color.parseColor("#999999"));
            }

            /*
            String  a = new String();
            a = (String) holder.ItemRenWuxijie.getText();
            if(a.endsWith(":")){//if(p == 6 || p == 12){

                holder.cb.setWidth(0);
            }
            */
            return convertView;
        }

        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelected;
        }

        public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
            ListCheckboxAdapter.isSelected = isSelected;
        }
    }

    public static class ListCheckViewHolder {
        public CheckBox cb;
        public TextView ItemRenWuxijie;
    }
}
