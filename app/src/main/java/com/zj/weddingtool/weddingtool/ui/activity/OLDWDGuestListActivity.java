package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


public class OLDWDGuestListActivity extends BaseActivity{
    public static final String TAG = "JHMingDanActivity";

    public ListGuestAdapter mlAdapter;

    private ListView lv;
    private TextView guest_total_number;
    private Button btn_add;

    private List<HashMap<String, Object>> mlist = null;

    private int sp_item_select = -1;

    private Integer guest_total;
    private ArrayList<String> guest_group=new ArrayList<String>();
    private ArrayList<String> guest_name=new ArrayList<String>();
    private ArrayList<Integer> guest_number=new ArrayList<Integer>();

    private Integer downloadFlag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_jhbinke);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < getResources().getStringArray(R.array.guest_group).length; i++) {
            guest_group.add(getResources().getStringArray(R.array.guest_group)[i]);
        }

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
        lv = (ListView) findViewById(R.id.listView_binke);

        guest_total_number = (TextView) findViewById(R.id.jhbinke_heji_numb);

        btn_add = (Button) findViewById(R.id.add_guest);

        // 为Adapter准备数据
        mlist = new ArrayList<HashMap<String, Object>>();

        guest_total = 0;

        for (int i = 0; i < guest_name.size(); i++) {

            guest_total += guest_number.get(i);//amymoney.toArray(new Integer[amymoney.size()])[i];
        }

        guest_total_number.setText(String.valueOf(guest_total));

        // 实例化自定义的MyAdapter
        mlAdapter = new ListGuestAdapter(this);

        // 绑定Adapter
        lv.setAdapter(mlAdapter);

        //列表点击响应
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                final ListGuestHolder holder = (ListGuestHolder) view.getTag();

                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.edit_guest_dialog_old,
                        (ViewGroup) findViewById(R.id.edit_guest_dialog_old_view));

                final int p = position;

                ArrayAdapter<CharSequence> mbb;
                final Spinner change_group = (Spinner) layout.findViewById(R.id.change_group);
                mbb = ArrayAdapter.createFromResource(OLDWDGuestListActivity.this,R.array.guest_group,android.R.layout.simple_spinner_item);
                mbb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                change_group.setAdapter(mbb);
                change_group.setSelection(guest_number.get(position),true);

                change_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        send_select_Item(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //修改人数和席位
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(holder.ItemGuestName.getText())
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        EditText change_number = (EditText) findViewById(R.id.change_number);

                        if (change_number.getText().toString().equals("")) {
                            Toast.makeText(OLDWDGuestListActivity.this, "修改人数失败，人数不能为空！", Toast.LENGTH_LONG).show();
                        } else {
                            guest_name.add(sp_item_select+"##"+holder.ItemGuestName.getText().toString());
                            guest_number.add(Integer.parseInt(change_number.getText().toString()));
                        }

                        for (int i = 0; i < guest_number.size(); i++) {
                            guest_total += guest_number.get(i);
                        }
                        guest_total_number.setText(String.valueOf(guest_total));


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
                final ListGuestHolder holder = (ListGuestHolder) view.getTag();
                final int p = position;
                Log.d(TAG, "LongPress");
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("确定要删除 " + holder.ItemGuestName.getText() + "?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

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
                final View layout = inflater.inflate(R.layout.add_guest_dialog_old,
                        (ViewGroup) findViewById(R.id.add_guest_dialog_old_view));

                ArrayAdapter<CharSequence> mbb;
                final Spinner set_group = (Spinner) layout.findViewById(R.id.set_guest_group);
                mbb = ArrayAdapter.createFromResource(view.getContext(),R.array.guest_group,android.R.layout.simple_spinner_item);
                mbb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                set_group.setAdapter(mbb);
                set_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        send_select_Item(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("增加新项目").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        EditText name = (EditText) layout.findViewById(R.id.add_guest_name);
                        EditText number = (EditText) layout.findViewById(R.id.add_guest_number);

                        if (name.getText().toString().equals("") || number.getText().toString().equals("")) {
                            Toast.makeText(OLDWDGuestListActivity.this, "新增宾客失败，姓名和人数需全部填写！", Toast.LENGTH_LONG).show();
                        } else {
                            guest_name.add(sp_item_select+"##"+name.getText().toString());
                            guest_number.add(Integer.parseInt(number.getText().toString()));

                        }

                        for (int i = 0; i < guest_number.size(); i++) {
                            guest_total += guest_number.get(i);
                        }
                        guest_total_number.setText(String.valueOf(guest_total));

                        Log.d(TAG, "" + sp_item_select);

                        mlAdapter.notifyDataSetChanged();

                        downloadFlag =1;
                    }
                });
                builder.show();
            }
        });


    }

    //填充数据
    private void initDate(Integer[] ints,Boolean[] boos) {

    }

    //获取下拉框Spinner
    private void send_select_Item(int i){
        sp_item_select = i;
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
                ArrayList<String> list1 = new ArrayList<String>();
                ArrayList<Integer> list2 = new ArrayList<Integer>();

                list1 = userme.getGuestname();
                list2 = userme.getGuestnumber();

                if (list1 == null) {
                    Log.d(TAG, "没有信息在云端");
                } else {
                    temps = (String[]) list1.toArray(new String[list1.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条");

                    for (int i = 0; i < temps.length; i++) {
                        guest_name.add(temps[i]);
                    }
                }

                if (list2 == null) {
                    Log.d(TAG, "没有信息在云端");
                } else {
                    temp = (Integer[]) list2.toArray(new Integer[list2.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list2.size() + "  " + " 条" + userme.getObjectId());
                    for (int i = 0; i < temps.length; i++) {
                        guest_number.add(temp[i]);
                    }
                }

                if ((list1 == null && list2 == null)) {
                    downloadFlag = 2;
                    Log.d(TAG, "无原始数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                } else if (list1.size() == list2.size() && list1.size() > 0) {
                    downloadFlag = 1;
                    Log.d(TAG, "有数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }

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
        if(downloadFlag < 0 || downloadFlag > 1)//如果读取数据库不成功，就不要上传空数据到云端，以免冲掉数据
            return;

        UserMe query = new UserMe();
        query.setGuestname(guest_name);
        query.setGuestnumber(guest_number);
        query.update(this, curUser.getObjectId(), new UpdateListener() {    //query.save(this, new SaveListener() {
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

    private class ListGuestAdapter extends BaseAdapter {

        private Context mcontext;
        private LayoutInflater mInflater = null;
        private List<HashMap<String, Object>> mmlist = null;
        private ArrayList<Integer> mlijin = null;

        public ListGuestAdapter(Context context) {
            this.mcontext = context;
            mInflater = LayoutInflater.from(context);

            initDate();
        }

        public void initDate() {
            //Log.d(TAG, "从服务器获取用户信息mmlist是："+mmlist.size()+"同时myCompleted是"+myCompleted.length);
        }


        @Override
        public int getCount() {
            return guest_name.size();
        }

        @Override
        public Object getItem(int position) {
            return guest_name.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListGuestHolder holder = null;
            if (holder == null) {
                holder = new ListGuestHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_binke_list_old, null);
                }

                holder.ItemGuestName = (TextView) convertView.findViewById(R.id.guest_name);
                holder.ItemGuestNumber = (TextView) convertView.findViewById(R.id.guest_number);
                holder.ItemGuestGroup = (TextView) convertView.findViewById(R.id.guest_group);
                convertView.setTag(holder);
            } else {
                holder = (ListGuestHolder) convertView.getTag();
            }

            int tmpgroup = Integer.parseInt(guest_name.get(position).split("##")[0]);
            holder.ItemGuestName.setText(guest_name.get(position).split("##")[1]);
            holder.ItemGuestNumber.setText(String.valueOf(guest_number.get(position)));
            holder.ItemGuestGroup.setText(String.valueOf(guest_group.get(tmpgroup)));

            return convertView;
        }

    }

    public static class ListGuestHolder {
        public TextView ItemGuestName;
        public TextView ItemGuestNumber;
        public TextView ItemGuestGroup;
    }

}