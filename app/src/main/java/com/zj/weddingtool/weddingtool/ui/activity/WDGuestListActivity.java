package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.BaseExpandableListAdapter;
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


public class WDGuestListActivity extends BaseActivity{
    public static final String TAG = "JHMingDanActivity";

    public ExListGuestAdapter mlAdapter;

    private ExpandableListView elv;//可伸缩列表
    private ListView lv;
    private TextView guest_total_number;

    private List<HashMap<String, Object>> mlist = null;

    private Integer guest_total;

    private ArrayList<ArrayList<String>> itemProcess = new ArrayList<ArrayList<String>>();

    private ArrayList<String> guest_group=new ArrayList<String>();
    private ArrayList<String> guest_name=new ArrayList<String>();
    private ArrayList<Integer> guest_number=new ArrayList<Integer>();

    private Integer downloadFlag = -1;

    private List<ArrayList<String>> generals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jhbinke);

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

        if(downloadFlag < 0) {
            finish();
            return;
        }
        /* 实例化各个控件 */
        elv = (ExpandableListView) findViewById(R.id.listView_binke_new);

        guest_total_number = (TextView) findViewById(R.id.jhbinke_heji_numb);

        guest_total = 0;

        if(downloadFlag > 1)
            guest_total = 0;
        else {
            for (int i = 0; i < guest_name.size(); i++) {

                guest_total += guest_number.get(i);//amymoney.toArray(new Integer[amymoney.size()])[i];
            }
        }

        guest_total_number.setText(String.valueOf(guest_total));

        // 实例化自定义的MyAdapter
        mlAdapter = new ExListGuestAdapter(this,itemProcess);

        // 绑定Adapter
        elv.setAdapter(mlAdapter);

        //elv.expandGroup(0);// 0 代表第一个Group，只用展开第一个分组就行
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Log.d(TAG, "groupPosition = " + groupPosition + ";childPosition = " + childPosition);
                LayoutInflater inflater = getLayoutInflater();

                final ListGuestChildHolder holder = (ListGuestChildHolder) view.getTag();

                final View layout = inflater.inflate(R.layout.edit_guest_dialog,
                        (ViewGroup) findViewById(R.id.edit_guest_dialog_view));

                final int Gp = groupPosition;
                final int Cp = childPosition;

                final EditText change_name = (EditText) layout.findViewById(R.id.change_name_new);
                change_name.setText(itemProcess.get(Gp).get(Cp).split("--")[0]);
                final EditText change_number = (EditText) layout.findViewById(R.id.change_number_new);
                change_number.setText(itemProcess.get(Gp).get(Cp).split("--")[1]);

                final ImageButton ibt_add = (ImageButton) layout.findViewById(R.id.ibtn_add);
                final ImageButton ibt_remove = (ImageButton) layout.findViewById(R.id.ibtn_remove);

                ibt_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int temp_number = Integer.parseInt(change_number.getText().toString());

                        if(temp_number < 200)
                            temp_number++;

                        change_number.setText(String.valueOf(temp_number));
                    }
                });

                ibt_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int temp_number = Integer.parseInt(change_number.getText().toString());

                        if(temp_number > 0)
                            temp_number--;

                        change_number.setText(String.valueOf(temp_number));
                    }
                });

                //修改人数和席位
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("修改姓名人数！")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        if (change_number.getText().toString().equals("")) {
                            Toast.makeText(WDGuestListActivity.this, "修改人数失败，人数不能为空！", Toast.LENGTH_LONG).show();
                        } else {
                        //    itemProcess.get(Gp).set(Cp, itemProcess.get(Gp).get(Cp).split("--")[0] + "--" + change_number.getText().toString() + "--" + itemProcess.get(Gp).get(Cp).split("--")[2]);
                            itemProcess.get(Gp).set(Cp, change_name.getText().toString() + "--" + change_number.getText().toString() + "--" + itemProcess.get(Gp).get(Cp).split("--")[2]);
                        }

                        mlAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();

                return true;//true 响应点击，反之则不响应点击
            }
        });

        for(int i=0;i<generals.size();i++) {
            if (generals.get(i).size() > 0)
                elv.expandGroup(i);         //如果Group有子项，就展开Group
        }

    }

    //填充数据
    private void initDate() {

        for(int i=0;i<guest_group.size();i++)
        {
            ArrayList<String> tempStr = new ArrayList<String>();
            if(downloadFlag == 1)//有数据
            {
                for (int j = 0; j < guest_name.size(); j++) {

                    if (String.valueOf(i).equals(guest_name.get(j).split("##")[0])) {
                        //    Log.d(TAG,"i = "+i+";j = "+j+";guest_name[0] ="+guest_name.get(j).toString().split("##")[0]);
                        tempStr.add(guest_name.get(j).split("##")[1] + "--" + guest_number.get(j).toString() + "--" + guest_name.get(j).split("##")[2]); //guest_name的第三段字符是标示所属席位的
                        //    Log.d(TAG, "tempStr.size()=" + tempStr.size() );
                    }
                }
            }
            itemProcess.add(tempStr);
        }
    }

    private void findAll() {
        Log.d(TAG, "从服务器获取用户信息");

        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
        BmobQuery<UserMe> query = new BmobQuery<>();
        query.getObject(this, curUser.getObjectId(), new GetListener<UserMe>() {
            @Override
            public void onSuccess(UserMe userme) {

                String[] temp_name = {};
                Integer[] temp_number = {};

                ArrayList<String> list1 = new ArrayList<String>();
                ArrayList<Integer> list2 = new ArrayList<Integer>();

                list1 = userme.getGuestname();
                list2 = userme.getGuestnumber();

                if(list1 == null)
                {
                    Log.d(TAG, "没有信息在云端, list1" );
                }else {
                    temp_name = (String[]) list1.toArray(new String[list1.size()]);

                    for (int i = 0; i < temp_name.length; i++) {
                        guest_name.add(temp_name[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条");
                }

                if(list2 == null)
                {
                    Log.d(TAG, "没有信息在云端, list2" );
                }else {
                    temp_number = (Integer[]) list2.toArray(new Integer[list2.size()]);

                    for (int i = 0; i < temp_number.length; i++) {
                        guest_number.add(temp_number[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list2.size() + "  " + " 条");
                }

                //guest_name = userme.getGuestname();
                //guest_number = userme.getGuestnumber();

                if ((list1 == null && list2 == null)) {
                    downloadFlag = 2;
                    Log.d(TAG, "无原始数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                } else if (list1.size() == list2.size() && list1.size() > 0) {
                    downloadFlag = 1;
                    Log.d(TAG, "有数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }

                initDate();
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
        if(downloadFlag < 0 || downloadFlag == 2)//如果读取数据库不成功，就不要上传空数据到云端，以免冲掉数据
            return;

        //if(downloadFlag == 1) {     //只有downloadFlag等于1时，这两个才需要清空，因为从云上下载了原始数据，而如果downloadFlag等于3时，是不需要清空的，因为这两个没有初始数据，而空list执行clear()会出错
            guest_name.clear();
            guest_number.clear();
        //}

        for(int j=0;j<guest_group.size();j++)
        {
            for(int z=0;z<itemProcess.get(j).size();z++)
            {
                if(itemProcess.get(j).size() > 0) {
                    Log.d(TAG, "j ="+j+";z="+z+";"+itemProcess.get(j).get(z));
                    guest_name.add(j + "##" + itemProcess.get(j).get(z).split("--")[0] + "##" + itemProcess.get(j).get(z).split("--")[2]);
                    guest_number.add(Integer.parseInt(itemProcess.get(j).get(z).split("--")[1]));
                }
            }
        }
        downloadFlag = 1;

        UserMe query = new UserMe();
        query.setGuestname(guest_name);
        query.setGuestnumber(guest_number);
        query.update(this, curUser.getObjectId(), new UpdateListener() {    //query.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
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


    class ExListGuestAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mInflater = null;
        //private ArrayList<ArrayList<String>> generals;
        private Integer[] group_number = {0,0,0,0,0,0,0,0,0,0};

        public ExListGuestAdapter(Context context,ArrayList<ArrayList<String>> alist) {
            mInflater = LayoutInflater.from(context);
            generals = alist;
        }


        @Override
        public int getGroupCount() {
            return guest_group.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return generals.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return guest_group.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return generals.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            ListGuestGroupHolder holder = null;
            if (holder == null) {
                holder = new ListGuestGroupHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_binke_list_tags, parent, false);
                }

                holder.ItemGuestGroup = (TextView) convertView.findViewById(R.id.guest_group_title);
                holder.ItemCxAdd = (CheckBox) convertView.findViewById(R.id.guest_cx_add);
                convertView.setTag(holder);
            } else {
                holder = (ListGuestGroupHolder) convertView.getTag();

            }

            if(itemProcess.get(groupPosition).size()>0)
            {
                group_number[groupPosition]=0;
                for(int i=0;i<itemProcess.get(groupPosition).size();i++)
                {
                    group_number[groupPosition] += Integer.parseInt(itemProcess.get(groupPosition).get(i).split("--")[1]);
                }
            }

            String temp = (String)getGroup(groupPosition);
            holder.ItemGuestGroup.setText(temp + "(" + group_number[groupPosition] + "位)");

            int temp_number = 0;
            for(int j=0;j<group_number.length;j++)
            {
                temp_number += group_number[j];
            }
            guest_total_number.setText(String.valueOf(temp_number));

            final int GP = groupPosition;
            holder.ItemCxAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.add_guest_dialog,
                            (ViewGroup) findViewById(R.id.add_guest_dialog_view));

                    final EditText number = (EditText) layout.findViewById(R.id.add_guest_number_new);
                    number.setText("1");//设置新增宾客时，默认人数为1；

                    final ImageButton ibt_a_add = (ImageButton) layout.findViewById(R.id.ibtn_a_add);
                    final ImageButton ibt_a_remove = (ImageButton) layout.findViewById(R.id.ibtn_a_remove);

                    ibt_a_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int temp_number = Integer.parseInt(number.getText().toString());

                            if(temp_number < 200)
                                temp_number++;

                            number.setText(String.valueOf(temp_number));
                        }
                    });

                    ibt_a_remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int temp_number = Integer.parseInt(number.getText().toString());

                            if(temp_number > 0)
                                temp_number--;

                            number.setText(String.valueOf(temp_number));
                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("新增宾客").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            EditText name = (EditText) layout.findViewById(R.id.add_guest_name_new);

                            if (name.getText().toString().equals("") || number.getText().toString().equals("")) {
                                Toast.makeText(WDGuestListActivity.this, "新增宾客失败，姓名和人数需全部填写！", Toast.LENGTH_LONG).show();
                            } else {
                                itemProcess.get(GP).add(name.getText().toString() + "--" + number.getText().toString() + "--" + "*"); // “*”的意思是没分席位之前,都是*这个符号

                                downloadFlag = 3;
                            }

                            mlAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });


            return convertView;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ListGuestChildHolder holder = null;
            if (holder == null) {
                holder = new ListGuestChildHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_binke_list, parent, false);
                }

                holder.ItemGuestName = (TextView) convertView.findViewById(R.id.guest_name_child);
                holder.ItemGuestNumber = (TextView) convertView.findViewById(R.id.guest_number_child);
                holder.btn_del_guest = (ImageButton) convertView.findViewById(R.id.img_btn_del_guest);
                convertView.setTag(holder);
            } else {
                holder = (ListGuestChildHolder) convertView.getTag();
            }

            String temp = (String)getChild(groupPosition, childPosition);
            holder.ItemGuestName.setText(temp.split("--")[0]);
            holder.ItemGuestNumber.setText(temp.split("--")[1]);

            final int GP = groupPosition;
            final int CP = childPosition;

            final ListGuestChildHolder myholder = holder;
            holder.btn_del_guest.setTag(childPosition);
            holder.btn_del_guest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("确定要删除 " + myholder.ItemGuestName.getText() + "?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            ToastUtils.showToast("删除 " + " " + myholder.ItemGuestName.getText() + " 项！");

                            generals.get(GP).remove(CP);

                            mlAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });

            if(itemProcess.get(groupPosition).size()>0)
            {
                group_number[groupPosition]=0;
                for(int i=0;i<itemProcess.get(groupPosition).size();i++)
                {
                    group_number[groupPosition] += Integer.parseInt(itemProcess.get(groupPosition).get(i).split("--")[1]);
                }
            }

            int temp_number = 0;
            for(int j=0;j<group_number.length;j++)
            {
                temp_number += group_number[j];
            }
            guest_total_number.setText(String.valueOf(temp_number));


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public static class ListGuestGroupHolder {
        public TextView ItemGuestGroup;
        public CheckBox ItemCxAdd;

    }

    public static class ListGuestChildHolder {
        public TextView ItemGuestName;
        public TextView ItemGuestNumber;
        public ImageButton btn_del_guest;
    }
}