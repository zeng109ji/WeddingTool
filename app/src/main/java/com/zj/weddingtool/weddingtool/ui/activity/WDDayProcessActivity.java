package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

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
 * 实现了ExpandableListView中的childlist的长按响应功能
 */
public class WDDayProcessActivity extends BaseActivity {

    public static final String TAG = "WDDayProcessActivity";

    //public ListEditTextAdapter mlAdapter;
    public MyAdapter maAdapter;

    private ExpandableListView elv;//可伸缩列表
    private ImageButton imbtn1_share;

    private List<HashMap<String, Object>> mlist = null;

    private ArrayList<String> mprocessGroup=new ArrayList<String>();
    private ArrayList<String> mprocesschildTime=new ArrayList<String>();
    private ArrayList<String> mprocesschildThings=new ArrayList<String>();
    private ArrayList<String> mprocesschildPeople=new ArrayList<String>();

    private ArrayList<ArrayList<String>> itemProcess = new ArrayList<ArrayList<String>>();

    private Integer downloadFlag = -1;

    private List<ArrayList<String>> generals;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jhdangtian);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
        elv = (ExpandableListView) findViewById(R.id.listView_dangtian);
        imbtn1_share = (ImageButton) findViewById(R.id.itemshare_liucheng);
        // 为Adapter准备数据
        mlist = new ArrayList<HashMap<String, Object>>();

        Log.d(TAG,"itemProcess = "+itemProcess.size());

        for (int i = 0; i < mprocesschildTime.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("process_time", mprocesschildTime.get(i));
            map.put("process_things", mprocesschildThings.get(i));
            map.put("process_people", mprocesschildPeople.get(i));
            mlist.add(map);
        }

        maAdapter=new MyAdapter(this,itemProcess);
        elv.setAdapter(maAdapter);

        //遍历所有group,将所有项设置成默认展开
        //int groupCount = elv.getCount();
        //for (int i=0; i<groupCount; i++)
        //{
        //    elv.expandGroup(i);
        //}
        //elv.expandGroup(0);// 0 代表第一个Group，只用展开第一个分组就行

        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Log.d(TAG, "groupPosition = " + groupPosition + ";childPosition = " + childPosition);

                LayoutInflater inflater = getLayoutInflater();

                final View layout = inflater.inflate(R.layout.edit_day_process_dialog,
                        (ViewGroup) findViewById(R.id.edit_day_process_dialog_view));

                ListChildTextHolder holder = (ListChildTextHolder) view.getTag();

                final int Gp = groupPosition;
                final int Cp = childPosition;

                final EditText thing_change = (EditText) layout.findViewById(R.id.change_things);
                final EditText people_change = (EditText) layout.findViewById(R.id.change_people);

                thing_change.setText(itemProcess.get(groupPosition).get(childPosition).split("--")[1]);
                people_change.setText(itemProcess.get(groupPosition).get(childPosition).split("--")[2]);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("修改 " + holder.ItemTime.getText().toString()+ " 的事宜和人员").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String thing_temp = new String();
                        if(thing_change.getText().toString().equals("")) {
                            thing_temp = " ";
                        }else {
                            thing_temp = thing_change.getText().toString();
                        }

                        String people_temp = new String();
                        if(people_change.getText().toString().equals("")) {
                            people_temp = " ";
                        }else {
                            people_temp = people_change.getText().toString();
                        }

                        itemProcess.get(Gp).set(Cp, itemProcess.get(Gp).get(Cp).split("--")[0] + "--" + thing_temp + "--" + people_temp);

                        maAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();

                return true;//true 响应点击，反之则不响应点击
            }
        });

        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int groupPos = (Integer)view.getTag(R.id.list_tag_group);
                final int childPos = (Integer)view.getTag(R.id.list_tag_child);

                Log.i(TAG, "groupPos:" + groupPos + ",childPos:" + childPos);

                if(childPos == -1){         //如果得到child位置的值为-1，则是操作group
                    Log.i(TAG, "操作group组件");
                }else{
                    Log.i(TAG,"操作child组件");     //否则，操作child

                    ListChildTextHolder holder = (ListChildTextHolder) view.getTag();

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("是否删除 " + holder.ItemTime.getText().toString() + "??")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            itemProcess.get(groupPos).remove(childPos);

                            maAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });

        for(int i=0;i<generals.size();i++) {
            if (generals.get(i).size() > 0)
                elv.expandGroup(i);         //如果Group有子项，就展开Group
        }

        ////////// 分享

        imbtn1_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WDDayProcessActivity.this, ShareListToPictureActivity.class);
                intent.putExtra("share_number", 1);
                intent.putExtra("process_group", mprocessGroup);
                for(int i=0;i<mprocessGroup.size();i++) {
                    intent.putExtra("process"+i, generals.get(i));
                }
                startActivity(intent);
            }
        });

        //////////

    }

    //填充数据
    private void initDate() {

        for(int i=0;i<mprocessGroup.size();i++)
        {
            ArrayList<String> tempStr = new ArrayList<String>();
            for(int j=0;j<mprocesschildTime.size();j++)
            {

                if(mprocessGroup.get(i).equals(mprocesschildTime.get(j).split("--")[0])){
                    Log.d(TAG,"i = "+i+";j = "+j);
                    //generals[i][j][0] = mprocesschildTime.get(j).toString().split("\\+")[1];
                    //generals[i][j][1] = mprocesschildThings.get(j).toString();
                    //generals[i][j][2] = mprocesschildTime.get(j).toString();
                    tempStr.add(mprocesschildTime.get(j).split("--")[1] + "--" + mprocesschildThings.get(j) + "--" + mprocesschildPeople.get(j));
                    Log.d(TAG, "tempStr.size()=" + tempStr.size() );
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
                String[] temp_group = {};
                String[] temp_childTime = {};
                String[] temp_childThings = {};
                String[] temp_childPeople = {};

                ArrayList<String> list1 = new ArrayList<String>();
                ArrayList<String> list2 = new ArrayList<String>();
                ArrayList<String> list3 = new ArrayList<String>();
                ArrayList<String> list4 = new ArrayList<String>();

                list1 = userme.getProcessgroup();
                list2 = userme.getProcesschildTime();
                list3 = userme.getProcesschildThings();
                list4 = userme.getProcesschildPeople();

                if(list1 == null)
                {
                    for (int i = 0; i < getResources().getStringArray(R.array.jhdangtian_tags).length; i++) {
                        mprocessGroup.add(getResources().getStringArray(R.array.jhdangtian_tags)[i]);
                    }
                    Log.d(TAG, "没有信息在云端,mprocessGroup " + mprocessGroup.size() + " 条");
                }else {
                    temp_group = (String[]) list1.toArray(new String[list1.size()]);

                    for (int i = 0; i < temp_group.length; i++) {
                        mprocessGroup.add(temp_group[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条");
                }


                if(list2 == null)
                {
                    TypedArray typedArray = getResources().obtainTypedArray(R.array.jhdangtian_liucheng);

                    for (int i = 0; i < getResources().getStringArray(R.array.jhdangtian_liucheng).length; i++) {
                        mprocesschildTime.add(getResources().getStringArray(typedArray.getResourceId(i,0))[0]);
                    }
                    Log.d(TAG, "没有信息在云端,mprocesschildTime " + mprocesschildTime.size() + " 条");
                }else {
                    temp_childTime = (String[]) list2.toArray(new String[list2.size()]);

                    for (int i = 0; i < temp_childTime.length; i++) {
                        mprocesschildTime.add(temp_childTime[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list2.size() + "  " + " 条");
                }

                if(list3 == null)
                {
                    TypedArray typedArray = getResources().obtainTypedArray(R.array.jhdangtian_liucheng);

                    for (int i = 0; i < getResources().getStringArray(R.array.jhdangtian_liucheng).length; i++) {
                        mprocesschildThings.add(getResources().getStringArray(typedArray.getResourceId(i,0))[1]);
                    }
                    Log.d(TAG, "没有信息在云端,mprocesschildThings " + mprocesschildThings.size() + " 条");
                }else {
                    temp_childThings = (String[]) list3.toArray(new String[list3.size()]);

                    for (int i = 0; i < temp_childThings.length; i++) {
                        mprocesschildThings.add(temp_childThings[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list3.size() + "  " + " 条");
                }

                if(list4 == null)
                {
                    TypedArray typedArray = getResources().obtainTypedArray(R.array.jhdangtian_liucheng);

                    for (int i = 0; i < getResources().getStringArray(R.array.jhdangtian_liucheng).length; i++) {
                        mprocesschildPeople.add(getResources().getStringArray(typedArray.getResourceId(i,0))[2]);
                    }
                    Log.d(TAG, "没有信息在云端,mprocesschildPeople " + mprocesschildPeople.size() + " 条");
                }else {
                    temp_childPeople = (String[]) list4.toArray(new String[list4.size()]);

                    for (int i = 0; i < temp_childPeople.length; i++) {
                        mprocesschildPeople.add(temp_childPeople[i]);
                    }
                    Log.d(TAG, "查询到服务器端的任务为 " + list4.size() + "  " + " 条");
                }

                if((list1 == null )) {
                    downloadFlag = 1;
                    Log.d(TAG, "无原始数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }else if(list2.size() == list3.size() && list1.size() > 0){
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
        if(downloadFlag < 0)//如果读取数据库不成功，就不要上传空数据到云端，以免冲掉数据
            return;

    //    Boolean[] tuitemp = mtuilijin.toArray(new Boolean[mtuilijin.size()]);
    //    Integer[] moneytemp = mlijin.toArray(new Integer[mlijin.size()]);

        mprocesschildTime.clear();
        mprocesschildThings.clear();
        mprocesschildPeople.clear();

        for(int j=0;j<mprocessGroup.size();j++)
        {
            for(int z=0;z<itemProcess.get(j).size();z++)
            {
                mprocesschildTime.add(mprocessGroup.get(j)+"--"+itemProcess.get(j).get(z).toString().split("--")[0]);
                mprocesschildThings.add(itemProcess.get(j).get(z).toString().split("--")[1]);
                mprocesschildPeople.add(itemProcess.get(j).get(z).toString().split("--")[2]);
            }
        }

        ArrayList list1 = new ArrayList();
        for(int i=0;i<mprocessGroup.size();i++) {
            list1.add(mprocessGroup.get(i));
        }

        ArrayList list2 = new ArrayList();
        for(int i=0;i<mprocesschildTime.size();i++) {
            list2.add(mprocesschildTime.get(i));//toArray(new String[mallarray.size()])[i]);//mallarray是ArrayList，而add的参数必须是数组，所以此处要转换一下
        }

        ArrayList list3 = new ArrayList();
        for(int i=0;i<mprocesschildThings.size();i++){
            list3.add(mprocesschildThings.get(i));
        }

        ArrayList list4 = new ArrayList();
        for(int i=0;i<mprocesschildPeople.size();i++){
            list4.add(mprocesschildPeople.get(i));
        }

        UserMe query = new UserMe();
        query.setProcessgroup(list1);
        query.setProcesschildTime(list2);
        query.setProcesschildThings(list3);
        query.setProcesschildPeople(list4);
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
        saveAll();
        super.onDestroy();
    }

    @Override
	public void onResume() {
	    super.onResume();
	}

    @Override
	public void onPause() {
        super.onPause();
	}



    class MyAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mInflater = null;

        public MyAdapter(Context context,List<ArrayList<String>> general){
            mInflater = LayoutInflater.from(context);
            generals = general;
        }

        @Override
        public int getGroupCount() {
            return mprocessGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return generals.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mprocessGroup.get(groupPosition);
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

            ListGroupTextHolder holder = null;
            if (holder == null) {
                holder = new ListGroupTextHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_dangtian_list_tags, parent, false);
                }

                holder.ItemTitle = (TextView) convertView.findViewById(R.id.group_title);
                holder.ItemCxAdd = (CheckBox) convertView.findViewById(R.id.cx_add);
                convertView.setTag(holder);
            } else {
                holder = (ListGroupTextHolder) convertView.getTag();

            }

            convertView.setTag(R.id.list_tag_group,groupPosition);//添加Tag，以便长按或是点击时，可以通过Tag值判断是group或是child列表项
            convertView.setTag(R.id.list_tag_child,-1);

            String temp = (String)getGroup(groupPosition);
            holder.ItemTitle.setText(temp);

            final int GP = groupPosition;
            holder.ItemCxAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.add_day_process_dialog,
                            (ViewGroup) findViewById(R.id.add_day_process_dialog_view));

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("新增具体流程").setIcon(android.R.drawable.ic_dialog_info).setView(layout)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            TimePicker tp = (TimePicker)layout.findViewById(R.id.timePicker1);
                            tp.setIs24HourView(true);
                            EditText things = (EditText)layout.findViewById(R.id.text_things);
                            EditText people = (EditText)layout.findViewById(R.id.text_people);

                            int curtimelist = -1;

                            int hour = tp.getCurrentHour();
                            int minute   = tp.getCurrentMinute();
                            String ctime = String.valueOf(hour)+":"+String.valueOf(minute);
                            ArrayList<Integer> times = new ArrayList<Integer>();

                            for(int i=0;i<generals.get(GP).size();i++)
                            {
                                String hh = generals.get(GP).get(i).split("--")[0].split(":")[0];
                                String mm = generals.get(GP).get(i).split("--")[0].split(":")[1];

                                times.add(Integer.parseInt(hh) * 60 + Integer.parseInt(mm));
                            }

                            if(minute == 0)
                                ctime = String.valueOf(hour)+":00";
                            else if(minute>0 && minute<10)
                                ctime = String.valueOf(hour)+":0"+String.valueOf(minute);

                            for(int j=0;j<generals.get(GP).size();j++)
                            {
                                int temp = hour*60+minute;
                                Log.d(TAG, "hour*60+minute=" + temp + ";times[" + j + "]=" + times.get(j) + ";times.size()=" + times.size() + ";generals.get(GP).size()" + generals.get(GP).size());
                                if ((hour*60+minute)<=times.get(j) && j<=generals.get(GP).size())
                                {
                                    curtimelist=j;
                                    break;
                                }

                            }

                            String thing_temp = new String();
                            if(things.getText().toString().equals("")) {
                                thing_temp = " ";
                            }else {
                                thing_temp = things.getText().toString();
                            }

                            String people_temp = new String();
                            if(people.getText().toString().equals("")) {
                                people_temp = " ";
                            }else {
                                people_temp = people.getText().toString();
                            }

                            Log.d(TAG,"curtimelist="+curtimelist);
                            if (curtimelist > -1)
                            {
                             //   mprocesschildTime.add(curtimelist,mprocessGroup.get(GP).toString()+"--"+ctime);
                             //   mprocesschildThings.add(curtimelist,things.getText().toString());
                             //   mprocesschildPeople.add(curtimelist,people.getText().toString());

                                generals.get(GP).add(curtimelist,ctime+"--"+thing_temp+"--"+people_temp);

                            }else
                            {
                             //   mprocesschildTime.add(mprocessGroup.get(GP).toString()+"--"+ctime);
                             //   mprocesschildThings.add(things.getText().toString());
                             //   mprocesschildPeople.add(people.getText().toString());

                                generals.get(GP).add(ctime+"--"+thing_temp+"--"+people_temp);
                            }
                            Log.d(TAG,""+generals.get(GP).get(0));
                            Log.d(TAG,""+generals.get(GP).get(0).split("--")[0]);
                            Log.d(TAG,""+generals.get(GP).get(0).split("--")[1]);
                            Log.d(TAG,""+generals.get(GP).get(0).split("--")[2]);

                            MyAdapter.this.notifyDataSetChanged();

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

            ListChildTextHolder holder = null;
            if (holder == null) {
                holder = new ListChildTextHolder();

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_dangtian_list, parent, false);
                }

                holder.ItemTime = (TextView) convertView.findViewById(R.id.process_time);
                holder.ItemThings = (TextView) convertView.findViewById(R.id.process_things);
                holder.ItemPeople = (TextView) convertView.findViewById(R.id.process_people);
                holder.Itemremove = (ImageButton) convertView.findViewById(R.id.ibtn_remove_process);
                convertView.setTag(holder);
            } else {
                holder = (ListChildTextHolder) convertView.getTag();
            }

            convertView.setTag(R.id.list_tag_group,groupPosition);
            convertView.setTag(R.id.list_tag_child,childPosition);

            //TextView textView = getTextView();
            //textView.setText(getChild(groupPosition, childPosition).toString());
            //ll.addView(textView);

            String temp = (String)getChild(groupPosition, childPosition);
            holder.ItemTime.setText(temp.split("--")[0]);
            holder.ItemThings.setText(temp.split("--")[1]);
            holder.ItemPeople.setText(temp.split("--")[2]);

            final int GP = groupPosition;
            final int CP = childPosition;

            final ListChildTextHolder myholder = holder;
            holder.Itemremove.setTag(childPosition);
            holder.Itemremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("确定要删除 " + myholder.ItemTime.getText() + "?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            ToastUtils.showToast("删除 " + " " + myholder.ItemTime.getText() + " 项！");

                            generals.get(GP).remove(CP);

                            MyAdapter.this.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });
            return convertView;//ll;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public static class ListGroupTextHolder {
        public TextView ItemTitle;
        public CheckBox ItemCxAdd;
    }

    public static class ListChildTextHolder {
        public TextView ItemTime;
        public TextView ItemThings;
        public TextView ItemPeople;
        public ImageButton Itemremove;
    }
}

