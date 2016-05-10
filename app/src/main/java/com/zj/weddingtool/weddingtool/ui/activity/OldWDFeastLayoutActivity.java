package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.weddingtool.model.UserMe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;


public class OldWDFeastLayoutActivity extends BaseActivity{
    public static final String TAG = "OldJHJiuXiActivity";

    public ExListGuestAdapter mlAdapter;

    private ExpandableListView elv;//可伸缩列表

    private TextView feast_total_number;
    private ImageButton imbtn_share;

    private Integer feast_total;

    private ArrayList<ArrayList<String>> itemProcess = new ArrayList<ArrayList<String>>();

    private ArrayList<String>  feast_group=new ArrayList<String>();
    private ArrayList<String> guest_name=new ArrayList<String>();
    private ArrayList<Integer> guest_number=new ArrayList<Integer>();

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
        mlAdapter = new ExListGuestAdapter(this,itemProcess,feast_total_number);

        // 绑定Adapter
        elv.setAdapter(mlAdapter);

        //elv.expandGroup(0);// 0 代表第一个Group，只用展开第一个分组就行
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, final int groupPosition, final int childPosition, long l) {
                Log.d(TAG, "groupPosition = " + groupPosition + ";childPosition = " + childPosition);
                LayoutInflater inflater = getLayoutInflater();

                //final int Gp = groupPosition;
                //final int Cp = childPosition;

                //修改人数和席位
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("确定删除此桌?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        int people_length = generals.get(groupPosition).get(childPosition).split(";").length;
                        Log.d(TAG,"删除的长度是："+people_length);

                        for(int i=0;i<itemProcess.get(groupPosition).size();i++)
                        {
                            for(int j=0;j<people_length;j++) {
                                if (itemProcess.get(groupPosition).get(i).split("--")[0].equals(generals.get(groupPosition).get(childPosition).split(";")[j].split("\\(")[0]))
                                {
                                    itemProcess.get(groupPosition).set(i,itemProcess.get(groupPosition).get(i).split("--")[0] + "--" + itemProcess.get(groupPosition).get(i).split("--")[1] + "--" + "*");
                                    Log.d(TAG, "i =" + i + ";j=" + j + ";" + itemProcess.get(groupPosition).get(i));
                                }
                            }
                        }

                        generals.get(groupPosition).remove(childPosition);

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
                Intent intent = new Intent(OldWDFeastLayoutActivity.this, ShareListToPictureActivity.class);
                intent.putExtra("feast_froup", feast_group);
                for(int i=0;i<feast_group.size();i++) {
                    intent.putExtra("feast"+i, generals.get(i));
                }
                startActivity(intent);
            }
        });

        //////////
    }

    //填充数据
    private void initDate() {

        for(int i=0;i<feast_group.size();i++)
        {
            ArrayList<String> tempStr = new ArrayList<String>();
            if(downloadFlag == 1)//有数据
            {
                for (int j = 0; j < guest_name.size(); j++) {

                    if (String.valueOf(i).equals(guest_name.get(j).split("##")[0])) {
                            //Log.d(TAG,"i = "+i+";j = "+j+";guest_name[0] ="+guest_name.get(j).toString().split("##")[0]);
                        tempStr.add(guest_name.get(j).split("##")[1] + "--" + guest_number.get(j).toString() + "--" +guest_name.get(j).split("##")[2]);
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

        for(int j=0;j<feast_group.size();j++)
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
     //   private ArrayList<ArrayList<String>> generals = new ArrayList<ArrayList<String>>();
        private ArrayList<ArrayList<String>> itemGuest;
        private Integer[] group_number = {0,0,0,0,0,0,0,0,0,0};
        private TextView mfeast_number_tx = null;
        private Integer final_number = 0;

        public ExListGuestAdapter(Context context,ArrayList<ArrayList<String>> alist,TextView feast_number_tx) {

            mInflater = LayoutInflater.from(context);

            this.mfeast_number_tx = feast_number_tx;

            this.itemGuest = alist;

            for(int i=0;i<feast_group.size();i++) {
                for (int j = 0; j < itemGuest.get(i).size(); j++) {
                    //判断一下，最大尾数是多少，方便在新加酒席时，对来宾添加不重复的尾数
                    if(!itemGuest.get(i).get(j).split("--")[2].equals("*") && Integer.parseInt(itemGuest.get(i).get(j).split("--")[2]) > final_number)
                    {
                        final_number = Integer.parseInt(itemGuest.get(i).get(j).split("--")[2]);
                    }
                }
            }

            for(int i=0;i<feast_group.size();i++)
            {
                ArrayList<String> feast_people = new ArrayList<String>();

                for(int j=0;j<=final_number;j++) {//根据最大尾数来进行分组，保证所有的数据全部都能查到
                    String temp = new String();
                    for (int z = 0; z < itemGuest.get(i).size(); z++) {
                        if (itemGuest.get(i).get(z).split("--")[2].equals("*")) {
                            ;
                        } else {

                            if (itemGuest.get(i).get(z).split("--")[2].equals(String.valueOf(j))) {
                                temp += itemGuest.get(i).get(z).split("--")[0] + "(" + itemGuest.get(i).get(z).split("--")[1] + "人);";
                                Log.d(TAG, "i = "+i+";j = "+j+";z = "+z+";"+itemGuest.get(i).get(z));
                            }

                        }
                    }

                    if(!temp.equals(""))
                        feast_people.add(temp);
                }

                generals.add(feast_people);
            }
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

            String temp = (String)getGroup(groupPosition);
            holder.ItemFeastGroup.setText(temp+"("+group_number[groupPosition]+"桌)");

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
                    final ArrayList<String> people_temp = new ArrayList<String>();

                    final ArrayAdapter<String> mbb;
                    LayoutInflater inflater = getLayoutInflater();
                    final List<String> list = new ArrayList<String>();
                    final View layout = inflater.inflate(R.layout.add_feast_dialog,
                            (ViewGroup) findViewById(R.id.add_feast_dialog_view));
                    final TextView people = (TextView) layout.findViewById(R.id.feast_guest_name);
                    final Spinner set_group = (Spinner) layout.findViewById(R.id.set_guest_group);

                    // mbb = ArrayAdapter.createFromResource(view.getContext(),R.array.guest_group,android.R.layout.simple_spinner_item);
                    for (int i = 0; i < itemGuest.get(GP).size(); i++) {
                        if (itemGuest.get(GP).get(i).split("--")[2].equals("*")) {    //只有没有被分桌的，才会显示在下拉列表里，否则一定是显示在每桌的列表里
                            list.add(itemGuest.get(GP).get(i).split("--")[0] + "(" + Integer.parseInt(itemGuest.get(GP).get(i).split("--")[1]) + "人)");
                        }
                    }
                    //mbb = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,generals.get(GP));
                    mbb = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, list);
                    mbb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    set_group.setAdapter(mbb);
                    set_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d(TAG, "select i=" + i + " " + list.get(i));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    final Button btn_add = (Button) layout.findViewById(R.id.btn_add_to_feast);
                    btn_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int aa = (int) set_group.getSelectedItemId();
                            String temps = new String();
                            if(list.size() <= 0)
                            {
                                Toast.makeText(OldWDFeastLayoutActivity.this, "宾客列表为空，无法添加到酒席！", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                people_temp.add(list.get(aa));
                                mbb.remove(set_group.getSelectedItem().toString());
//                            list.remove(aa);
                                for (int i = 0; i < people_temp.size(); i++)
                                    temps += (people_temp.get(i) + ";");

                                people.setText(temps);
                            }
                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("新增酒席").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            if (people.getText().toString().equals("")) {
                                Toast.makeText(OldWDFeastLayoutActivity.this, "并未添加宾客到酒席！", Toast.LENGTH_LONG).show();
                            } else {
                                generals.get(GP).add(people.getText().toString());

                                final_number += 1;

                                for (int j = 0; j < itemGuest.get(GP).size(); j++) {
                                    for (int z = 0; z < people_temp.size(); z++) {
                                        if (itemGuest.get(GP).get(j).split("--")[0].equals(people_temp.get(z).split("\\(")[0])) {

                                            itemGuest.get(GP).set(j, itemGuest.get(GP).get(j).split("--")[0] + "--" + itemGuest.get(GP).get(j).split("--")[1] + "--" + String.valueOf(final_number));

                                            Log.d(TAG, "j =" + j + ";z=" + z + ";" + "final_number=" + final_number + ";" + itemGuest.get(GP).get(j));
                                        }
                                    }
                                }

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
                    convertView = mInflater.inflate(R.layout.item_jiuxi_list, parent, false);
                }

                holder.ItemFeastListName = (TextView) convertView.findViewById(R.id.feast_people_list);
                convertView.setTag(holder);
            } else {
                holder = (ListFeastChildHolder) convertView.getTag();
            }

            String temp = (String)getChild(groupPosition, childPosition);
            holder.ItemFeastListName.setText(temp);

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
        public TextView ItemFeastListName;
    }
}