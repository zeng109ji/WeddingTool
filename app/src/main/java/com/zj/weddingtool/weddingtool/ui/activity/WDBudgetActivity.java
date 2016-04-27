package com.zj.weddingtool.weddingtool.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.weddingtool.model.Line;
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
public class WDBudgetActivity extends BaseActivity {

    public static final String TAG = "JHyusuanActivity";
    public static final String EXTRA_KEY_USER_ID = "";

    public ListEditTextAdapter mlAdapter;
    private UserMe showUser;
    private UserMe curUser;

    private ListView lv;
    private TextView tx;
    private Button btn_add;

    private List<HashMap<String, Object>> mlist = null;
    private ArrayList<Line> mLines;
    private ArrayList<Integer> amymoney=new ArrayList<Integer>();
    private Integer mymoney_totel;
    private ArrayList<String> mallarray=new ArrayList<String>();

    private Integer downloadFlag = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jhyusuan);

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
        lv = (ListView) findViewById(R.id.listView_yusuan);

        tx = (TextView) findViewById(R.id.jhyusuan_heji_edit);

        btn_add = (Button) findViewById(R.id.itemadd);

        // 为Adapter准备数据
        mlist = new ArrayList<HashMap<String, Object>>();

        mymoney_totel = 0;

        for (int i = 0; i < mallarray.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ysxiangmu", mallarray.get(i));
            map.put("xiangmuedit", amymoney.get(i));
            mlist.add(map);

            mymoney_totel += amymoney.get(i);//amymoney.toArray(new Integer[amymoney.size()])[i];
        }

        tx.setText(String.valueOf(mymoney_totel));

        // 实例化自定义的MyAdapter
        mlAdapter = new ListEditTextAdapter(this, mlist, new String[] { "ysxiangmu", "xiangmuedit" },
                            new int[] {R.id.ysxiangmu, R.id.xiangmuedit },amymoney,mLines,tx);

        // 绑定Adapter
        lv.setAdapter(mlAdapter);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText inputServer = new EditText(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("增加新项目").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mallarray.add(inputServer.getText().toString());
                        amymoney.add(0);

                        Line linetemp = new Line();
                        linetemp.setNum(mallarray.size()-1);
                        mLines.add(linetemp);
                        //addItem(p);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("ysxiangmu", inputServer.getText().toString());
                        map.put("xiangmuedit", 0);
                        mlist.add(map);
                        mlAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();
            }
        });


    }

    //填充数据
    private void initDate(Integer[] ints) {

    //    amymoney = new Integer[mallarray.size()];

        mLines = createLines();

        for(int i=0;i<mallarray.size();i++) {
            Log.d(TAG, "initDate从云端同步下的数据,任务信息第 " + i + "任务为 " + ints[i]);
            amymoney.add(ints[i]);

            if( amymoney.get(i) > 0) {
                mLines.get(i).setText(String.valueOf(amymoney.get(i)));
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
                ArrayList<Integer> list1 = new ArrayList<Integer>();
                ArrayList<String> list2 = new ArrayList<String>();
                list1 = userme.getMymoney();
                list2 = userme.getMymoneyItem();

                if(list2 == null)
                {
                    for (int i = 0; i < getResources().getStringArray(R.array.jhyusuan).length; i++) {
                        mallarray.add(getResources().getStringArray(R.array.jhyusuan)[i]);
                    }
                    Log.d(TAG, "没有信息在云端， mallarray " + mallarray.size() + "  " + " 条");
                }else {
                    temps = (String[]) list2.toArray(new String[list2.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list2.size() + "  " + " 条");

                    for (int i = 0; i < temps.length; i++) {
                        mallarray.add(temps[i]);
                    }
                }


                if(list1 == null)
                {
                    Log.d(TAG, "没有信息在云端1 "+ mallarray.size());
                    temp = new Integer[9];
                    for (int i = 0; i < mallarray.size(); i++) {
                        temp[i] = 0;
                    }
                    Log.d(TAG, "没有信息在云端");
                }else {
                    temp = (Integer[]) list1.toArray(new Integer[list1.size()]);
                    Log.d(TAG, "查询到服务器端的任务为 " + list1.size() + "  " + " 条" + userme.getObjectId());
                }

                if((list1 == null && list2 == null)) {
                    downloadFlag = 1;
                    Log.d(TAG, "无原始数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }else if(list1.size() == list2.size() && list1.size() > 0){
                    downloadFlag = 1;
                    Log.d(TAG, "有数据 变化查询数据标志位 downloadFlag=" + downloadFlag);
                }

                initDate(temp);
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

        Integer[] moneytemp = amymoney.toArray(new Integer[amymoney.size()]);

        ArrayList list1 = new ArrayList();
        for(int i=0;i<mallarray.size();i++) {
           // amymoney[i] = Integer.parseInt(mlAdapter.getHowMoney().get(i));
            if(mLines.get(i).getText() == null)
                moneytemp[i] = 0;
            else
                moneytemp[i] = Integer.parseInt(mLines.get(i).getText());
            Log.d(TAG, "上传数据到云,任务信息第 " + i + "任务为 " + moneytemp[i]);
            list1.add(moneytemp[i]);
        }

        ArrayList list2 = new ArrayList();
        for(int i=0;i<mallarray.size();i++) {
            list2.add(mallarray.get(i));//toArray(new String[mallarray.size()])[i]);//mallarray是ArrayList，而add的参数必须是数组，所以此处要转换一下
        }

        UserMe query = new UserMe();
        query.setMymoney(list1);
        query.setMymoneyItem(list2);
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
        private ArrayList<Integer> amyMoney = null;
        private List<Line> lines;
        private Integer mtotal = null;
        private TextView mtx = null;

        public ListEditTextAdapter(Context context, List<HashMap<String, Object>> list, String[] from, int[] to,ArrayList<Integer> amyMoney,List<Line> mLines,TextView tx) {
            this.mcontext = context;
            this.mmlist = list;
            this.lines = mLines;
            this.mtx = tx;
            mInflater = LayoutInflater.from(context);
            howMoney = new HashMap<Integer, String>();
            this.amyMoney = amyMoney;
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);

            initDate();
        }

        public void initDate() {
            for (int i = 0; i < mmlist.size(); i++) {
                getHowMoney().put(i, amyMoney.get(i).toString());
            }
            //Log.d(TAG, "从服务器获取用户信息mmlist是："+mmlist.size()+"同时myCompleted是"+myCompleted.length);
        }

        public void setTotalMoney() {
            Integer mtotal = 0;
            Integer[] tempint = amyMoney.toArray(new Integer[amyMoney.size()]);
            for(int i=0;i<mmlist.size();i++) {
                if(lines.get(i).getText() == null)
                    tempint[i] = 0;
                else
                    tempint[i] = Integer.parseInt(lines.get(i).getText());
                mtotal += tempint[i];
            }

            mtx.setText(String.valueOf(mtotal));
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
                    convertView = mInflater.inflate(R.layout.item_yusuan_et_list, null);
                }

                holder.ItemYuSuanxiangmu = (TextView) convertView.findViewById(R.id.ysxiangmu);
                holder.et = (EditText) convertView.findViewById(R.id.xiangmuedit);
                holder.btn_del = (Button) convertView.findViewById(R.id.btn_del);
                convertView.setTag(holder);
            } else {
                holder = (ListEditTextHolder) convertView.getTag();
            }

            final Line line = lines.get(position);

            if (holder.et.getTag() instanceof TextWatcher) {
                holder.et.removeTextChangedListener((TextWatcher) (holder.et.getTag()));
            }

            if (TextUtils.isEmpty(line.getText())) {
                holder.et.setText("");
            } else {
                holder.et.setText(line.getText());
            }

            if (line.isFocus()) {
                if (!holder.et.isFocused()) {
                    holder.et.requestFocus();
                }
                CharSequence text = line.getText();
                holder.et.setSelection(TextUtils.isEmpty(text) ? 0 : text.length());
            } else {
                if (holder.et.isFocused()) {
                    holder.et.clearFocus();
                }
            }

            final int p = position;
            final ListEditTextHolder myholder = holder;

            holder.et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        final boolean focus = line.isFocus();
                        check(p);
                        if (!focus && !myholder.et.isFocused()) {
                            myholder.et.requestFocus();
                            myholder.et.onWindowFocusChanged(true);
                        }
                    }
                    return false;
                }
            });

            final TextWatcher watcher = new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(s)) {
                        line.setText(null);
                    } else {
                        line.setText(String.valueOf(s));
                        setTotalMoney();
                    }
                }
            };
            holder.et.addTextChangedListener(watcher);
            holder.et.setTag(watcher);

            HashMap<String, Object> map = mmlist.get(position);
            if (map != null) {
                itemString = (String) map.get(keyString[0]);
                holder.ItemYuSuanxiangmu.setText(itemString);
            }


            holder.btn_del.setTag(position);
            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtils.showToast("删除 " + p + " " + myholder.ItemYuSuanxiangmu.getText() + " 项！");

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("确定要删除 "+ myholder.ItemYuSuanxiangmu.getText() + "?")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            mallarray.remove(p);
                            amyMoney.remove(p);

                            mmlist.remove(p);
                            mLines.remove(p);

                            setTotalMoney();

                            mlAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
            });


            return convertView;
        }

        private void check(int position) {
            for (Line l : lines) {
                l.setFocus(false);
            }
            lines.get(position).setFocus(true);
        }

        public HashMap<Integer, String> getHowMoney() {
            return howMoney;
        }

        public void setHowMoney(HashMap<Integer, String> howMoney) {
            mlAdapter.howMoney = howMoney;
        }
    }

    public static class ListEditTextHolder {

        public TextView ItemYuSuanxiangmu;
        public EditText et;
        public Button btn_del;
    }

    private ArrayList<Line> createLines() {
        ArrayList<Line> lines = new ArrayList<>();
        for (int i = 0; i < mallarray.size(); i++) {
            Line line = new Line();
            line.setNum(i);
            lines.add(line);
        }
        return lines;
    }
}
