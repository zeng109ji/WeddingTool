package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
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


public class WDFeastLayoutActivity extends BaseActivity{
    public static final String TAG = "JHJiuXiActivity";

    public ExListGuestAdapter mlAdapter;

    private ExpandableListView elv;//可伸缩列表

    private TextView feast_total_number;
    private ImageButton imbtn_share;

    private ImageView ivDeleteText;
    private EditText etSearch;

    private Integer feast_total;

    //private ArrayList<ArrayList<String>> itemProcess = new ArrayList<ArrayList<String>>();

    private ArrayList<String>  feast_group=new ArrayList<String>();
    private ArrayList<String> feast_info=new ArrayList<String>();

    private Integer downloadFlag = -1;

    private ArrayList<ArrayList<String>> generals = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jhjiuxi);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < getResources().getStringArray(R.array.guest_group).length; i++) {
            feast_group.add(getResources().getStringArray(R.array.guest_group)[i]);
        }

        findAll();//从服务器取数据需要时间，保证能完成数据下载的话最好延时一段时间，然后再调用initView()

        mHandler.sendEmptyMessageDelayed(0, 1000);

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
        elv = (ExpandableListView) findViewById(R.id.listView_jiuxi);

        feast_total_number = (TextView) findViewById(R.id.jhjiuxi_heji_numb);
        feast_total = 0;
        feast_total_number.setText(String.valueOf(feast_total));

        imbtn_share = (ImageButton) findViewById(R.id.itemshare_jiuxi);

        // 实例化自定义的MyAdapter
        mlAdapter = new ExListGuestAdapter(this,feast_total_number);

        // 绑定Adapter
        elv.setAdapter(mlAdapter);

        //elv.expandGroup(0);// 0 代表第一个Group，只用展开第一个分组就行
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, final int groupPosition, final int childPosition, long l) {
                Log.d(TAG, "groupPosition = " + groupPosition + ";childPosition = " + childPosition);
                LayoutInflater inflater = getLayoutInflater();

                final ListFeastChildHolder holder = (ListFeastChildHolder) view.getTag();

                final View layout = inflater.inflate(R.layout.edit_feast_dialog,
                        (ViewGroup) findViewById(R.id.edit_feast_dialog_view));

                final EditText change_feast_name = (EditText) layout.findViewById(R.id.change_feast_guest_tx);

                change_feast_name.setText(holder.ItemFeastListName.getText());

                final int Gp = groupPosition;
                final int Cp = childPosition;

                //修改人数和席位
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("修改"+holder.ItemFeastTitle.getText())
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String feast_names_new = new String();

                        if (change_feast_name.getText().toString().equals("")) {
                            Toast.makeText(WDFeastLayoutActivity.this, "并未添加宾客到酒席！", Toast.LENGTH_LONG).show();
                        } else {
                            //Boolean isend = false;
                            char temps;

                            String names = new String();
                            String temp_str = change_feast_name.getText().toString();
                            Log.d(TAG,""+temp_str);
                            for(int i=0;i<temp_str.length();i++)
                            {

                                if(" ".equals(String.valueOf(temp_str.charAt(i))))
                                {
                                    if(!names.equals("")) {
                                        feast_names_new += (names+"--");
                                        names = "";
                                    }
                                }else{
                                    temps = temp_str.charAt(i);
                                    names += String.valueOf(temps);
                                    //    Log.d(TAG, "i:" +i+";s:"+ temp_str.charAt(i)+";"+names);
                                }
                            }

                            if(!names.equals("")) {
                                feast_names_new += (names);
                                names = "";
                            }
                            Log.d(TAG, "" + feast_names_new);
                        }

                        generals.get(groupPosition).set(childPosition,feast_names_new);

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

        ////////// 分享

        imbtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WDFeastLayoutActivity.this, ShareListToPictureActivity.class);
                intent.putExtra("share_number", 0);
                intent.putExtra("feast_froup", feast_group);
                for(int i=0;i<feast_group.size();i++) {
                    intent.putExtra("feast"+i, generals.get(i));
                }
                startActivity(intent);
            }
        });

        //////////

        /////////////////////////////////////////////////////////////////////搜索
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
        etSearch = (EditText) findViewById(R.id.etSearch);

        ivDeleteText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.GONE);
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
    }

    //填充数据
    private void initDate() {

        for(int i=0;i<feast_group.size();i++)
        {
            ArrayList<String> tempStr = new ArrayList<String>();
            if(downloadFlag == 1)//有数据
            {
                for (int j = 0; j < feast_info.size(); j++) {

                    if (String.valueOf(i).equals(feast_info.get(j).split("##")[0])) {
                            //Log.d(TAG,"i = "+i+";j = "+j+";guest_name[0] ="+guest_name.get(j).toString().split("##")[0]);
                        tempStr.add(feast_info.get(j).split("##")[1]);
                        //    Log.d(TAG, "tempStr.size()=" + tempStr.size() );
                    }
                }
            }
            generals.add(tempStr);
        }
    }

    private void findAll() {
        Log.d(TAG, "从服务器获取用户信息");

        BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
        BmobQuery<UserMe> query = new BmobQuery<>();
        query.getObject(this, curUser.getObjectId(), new GetListener<UserMe>() {
            @Override
            public void onSuccess(UserMe userme) {

                String[] temp_feastinfo = {};

                ArrayList<String> list1 = new ArrayList<String>();

                list1 = userme.getFeastinfo();

                if(list1 == null)
                {
                    Log.d(TAG, "没有信息在云端, list1" );
                }else {
                    temp_feastinfo = (String[]) list1.toArray(new String[list1.size()]);

                    for (int i = 0; i < temp_feastinfo.length; i++) {
                        feast_info.add(temp_feastinfo[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条");
                }


                if (list1 == null) {
                    downloadFlag = 2;
                    Log.d(TAG, "无原始数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                } else if (list1.size() > 0) {
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
        feast_info.clear();
        //}

        for(int j=0;j<feast_group.size();j++)
        {
            for(int z=0;z<generals.get(j).size();z++)
            {
                if(generals.get(j).size() > 0) {
                    Log.d(TAG, "j =" + j + ";z=" + z + ";" + generals.get(j).get(z));
                    feast_info.add(j + "##" + generals.get(j).get(z));
                }
            }
        }
        downloadFlag = 1;

        UserMe query = new UserMe();
        query.setFeastinfo(feast_info);
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
     //   private ArrayList<ArrayList<String>> generals = new ArrayList<ArrayList<String>>();
    //    private ArrayList<ArrayList<String>> itemGuest;
        private Integer[] group_number = {0,0,0,0,0,0,0,0,0,0};
        private TextView mfeast_number_tx = null;
        private Integer final_number = 0;

        public ExListGuestAdapter(Context context,TextView feast_number_tx) {
            mInflater = LayoutInflater.from(context);
            this.mfeast_number_tx = feast_number_tx;
        }


        @Override
        public int getGroupCount() {
            return feast_group.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return generals.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return feast_group.get(groupPosition);
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

            ListFeastGroupHolder holder = null;
            if (holder == null) {
                holder = new ListFeastGroupHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_jiuxi_list_tags, parent, false);
                }

                holder.ItemFeastGroup = (TextView) convertView.findViewById(R.id.feast_group_title);
                holder.ItemCxAdd = (CheckBox) convertView.findViewById(R.id.feast_cx_add);
                convertView.setTag(holder);
            } else {
                holder = (ListFeastGroupHolder) convertView.getTag();

            }

            if(generals.get(groupPosition).size()>0)
            {
                group_number[groupPosition]=generals.get(groupPosition).size();
            }else
                group_number[groupPosition]=0;

            int group_total_people=0;
            for(int i=0;i<group_number[groupPosition];i++)
            {
                group_total_people += generals.get(groupPosition).get(i).split("--").length;
            }
            String temp = (String)getGroup(groupPosition);
            holder.ItemFeastGroup.setText(temp+"("+group_number[groupPosition]+"桌/"+group_total_people+"人)");

            int temp_total = 0;
            for(int i=0;i<generals.size();i++)
            {
               temp_total += generals.get(i).size();
            }
            mfeast_number_tx.setText(String.valueOf(temp_total));

            final int GP = groupPosition;

            holder.ItemCxAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = getLayoutInflater();

                    final View layout = inflater.inflate(R.layout.add_feast_dialog_new,
                            (ViewGroup) findViewById(R.id.add_feast_dialog_new_view));

                    final EditText feast_guest = (EditText) layout.findViewById(R.id.feast_guest_tx);


                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(feast_group.get(GP)+" 新增酒席").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        String feast_names = new String();

                        public void onClick(DialogInterface dialog, int which) {

                            if (feast_guest.getText().toString().equals("")) {
                                Toast.makeText(WDFeastLayoutActivity.this, "并未添加宾客到酒席！", Toast.LENGTH_LONG).show();
                            } else {
                                //Boolean isend = false;
                                char temps;

                                String names = new String();
                                String temp_str = feast_guest.getText().toString();
                                Log.d(TAG,""+temp_str);
                                for(int i=0;i<temp_str.length();i++)
                                {

                                    if(" ".equals(String.valueOf(temp_str.charAt(i))))
                                    {
                                        if(!names.equals("")) {
                                            feast_names += (names+"--");
                                            names = "";
                                        }
                                    }else{
                                        temps = temp_str.charAt(i);
                                        names += String.valueOf(temps);
                                    //    Log.d(TAG, "i:" +i+";s:"+ temp_str.charAt(i)+";"+names);
                                    }
                                }
                                if(!names.equals("")) {
                                    feast_names += (names);
                                    names = "";
                                }

                                //Log.d(TAG, "" + feast_names);

                                generals.get(GP).add(feast_names);

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
            ListFeastChildHolder holder = null;
            if (holder == null) {
                holder = new ListFeastChildHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_jiuxi_list_new, parent, false);
                }

                holder.ItemFeastTitle = (TextView) convertView.findViewById(R.id.feast_title);
                holder.ItemFeastListName = (TextView) convertView.findViewById(R.id.feast_people_list_new);
                holder.ItemFeastRemove = (ImageButton) convertView.findViewById(R.id.ibtn_remove_feast);

                convertView.setTag(holder);
            } else {
                holder = (ListFeastChildHolder) convertView.getTag();
            }

            String temp = (String)getChild(groupPosition, childPosition);
            holder.ItemFeastTitle.setText("第"+(childPosition+1)+"桌:共"+temp.split("--").length+"人");
            holder.ItemFeastListName.setText(temp.replace("--"," "));

            final int GP = groupPosition;
            final int CP = childPosition;

            final ListFeastChildHolder myholder = holder;
            holder.ItemFeastRemove.setTag(childPosition);
            holder.ItemFeastRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("确定要删除 " + myholder.ItemFeastTitle.getText() + "?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            ToastUtils.showToast("删除 " + " " + myholder.ItemFeastTitle.getText() + " ！");

                            generals.get(GP).remove(CP);

                            mlAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public static class ListFeastGroupHolder {
        public TextView ItemFeastGroup;
        public CheckBox ItemCxAdd;
    }

    public static class ListFeastChildHolder {
        public TextView ItemFeastTitle;
        public TextView ItemFeastListName;
        public ImageButton ItemFeastRemove;
    }
}