package com.zj.weddingtool.weddingtool.ui.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.ToastUtils;
import com.zj.weddingtool.weddingtool.model.CalendarView;
import com.zj.weddingtool.weddingtool.model.UserMe;
import com.zj.weddingtool.weddingtool.model.borderText.BorderText;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

//import android.widget.CalendarView;

/**
 * 日历显示activity 2016-4-19
 * @author zj
 *
 */
public class WDDateActivity extends BaseActivity implements OnGestureListener {
	public static final String TAG = "JHriqiActivity";
	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private com.zj.weddingtool.weddingtool.model.CalendarView calV = null;
	private GridView gridView = null;
	private BorderText topText = null;
	private Drawable draw = null;
	private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private String smmarrydate = "";
	private TextView myText = null;
	private TextView myText_2 = null;
	private TextView myText_3 = null;
	private TextView myText_4 = null;
	private TextView myText_5 = null;
	private TextView myText_6 = null;
	private TextView myText_7 = null;

	public WDDateActivity() {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date);  //当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);


	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jhriqi);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		gestureDetector = new GestureDetector(this);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();

		findAllr();
	}

	private void initView() {
		calV = new CalendarView(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, smmarrydate);

		addGridView();
		gridView.setAdapter(calV);
		//flipper.addView(gridView);
		flipper.addView(gridView, 0);

		myText = (TextView) findViewById(R.id.texthunshaoriqi);

		myText_2 = (TextView) findViewById(R.id.text21);
		myText_3 = (TextView) findViewById(R.id.text31);
		myText_4 = (TextView) findViewById(R.id.text41);
		myText_5 = (TextView) findViewById(R.id.text51);
		myText_6 = (TextView) findViewById(R.id.text61);
		myText_7 = (TextView) findViewById(R.id.text71);

		topText = (BorderText) findViewById(R.id.toptext);
		addTextToTopTextView(topText);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						   float velocityY) {
		int gvFlag = 0;         //每次添加gridview到viewflipper中时给的标记
		if (e1.getX() - e2.getX() > 120) {
			//像左滑动
			addGridView();   //添加一个gridView
			jumpMonth++;     //下一个月

			calV = new CalendarView(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, smmarrydate);
			gridView.setAdapter(calV);
			//flipper.addView(gridView);
			addTextToTopTextView(topText);
			gvFlag++;
			flipper.addView(gridView, gvFlag);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -120) {
			//向右滑动
			addGridView();   //添加一个gridView
			jumpMonth--;     //上一个月

			calV = new CalendarView(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c, smmarrydate);
			gridView.setAdapter(calV);
			gvFlag++;
			addTextToTopTextView(topText);
			//flipper.addView(gridView);
			flipper.addView(gridView, gvFlag);

			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}

	/**
	 * 创建菜单
	 */
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, menu.FIRST, menu.FIRST, "今天");
		menu.add(0, menu.FIRST+1, menu.FIRST+1, "跳转");
	//	menu.add(0, menu.FIRST+2, menu.FIRST+2, "日程");
	//	menu.add(0, menu.FIRST+3, menu.FIRST+3, "日期转换");
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 选择菜单
	 */
/*	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
        case Menu.FIRST:
        	//跳转到今天
        	int xMonth = jumpMonth;
        	int xYear = jumpYear;
        	int gvFlag =0;
        	jumpMonth = 0;
        	jumpYear = 0;
        	addGridView();   //添加一个gridView
        	year_c = Integer.parseInt(currentDate.split("-")[0]);
        	month_c = Integer.parseInt(currentDate.split("-")[1]);
        	day_c = Integer.parseInt(currentDate.split("-")[2]);
        	calV = new CalendarView(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView,gvFlag);
	        if(xMonth == 0 && xYear == 0){
	        	//nothing to do
	        }else if((xYear == 0 && xMonth >0) || xYear >0){
	        	this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
				this.flipper.showNext();
	        }else{
	        	this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
				this.flipper.showPrevious();
	        }
			flipper.removeViewAt(0);
        	break;
        case Menu.FIRST+1:
        	
        	new DatePickerDialog(this, new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					//1901-1-1 ----> 2049-12-31
					if(year < 1901 || year > 2049){
						//不在查询范围内
						new AlertDialog.Builder(JHriqiActivity.this).setTitle("错误日期").setMessage("跳转日期范围(1901/1/1-2049/12/31)").setPositiveButton("确认", null).show();
					}else{
						int gvFlag = 0;
						addGridView();   //添加一个gridView
			        	calV = new CalendarView(JHriqiActivity.this, JHriqiActivity.this.getResources(),year,monthOfYear+1,dayOfMonth);
				        gridView.setAdapter(calV);
				        addTextToTopTextView(topText);
				        gvFlag++;
				        flipper.addView(gridView,gvFlag);
				        if(year == year_c && monthOfYear+1 == month_c){
				        	//nothing to do
				        }
				        if((year == year_c && monthOfYear+1 > month_c) || year > year_c ){
							JHriqiActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(JHriqiActivity.this,R.anim.push_left_in));
							JHriqiActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(JHriqiActivity.this,R.anim.push_left_out));
							JHriqiActivity.this.flipper.showNext();
				        }else{
							JHriqiActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(JHriqiActivity.this,R.anim.push_right_in));
							JHriqiActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(JHriqiActivity.this,R.anim.push_right_out));
							JHriqiActivity.this.flipper.showPrevious();
				        }
				        flipper.removeViewAt(0);
				        //跳转之后将跳转之后的日期设置为当期日期
				        year_c = year;
						month_c = monthOfYear+1;
						day_c = dayOfMonth;
						jumpMonth = 0;
						jumpYear = 0;
					}
				}
			},year_c, month_c-1, day_c).show();
        	break;
        case Menu.FIRST+2:
        	Intent intent = new Intent();
			intent.setClass(JHriqiActivity.this, ScheduleAll.class);
			startActivity(intent);
        	break;
        case Menu.FIRST+3:
        	Intent intent1 = new Intent();
        	intent1.setClass(JHriqiActivity.this, CalendarConvert.class);
        	intent1.putExtra("date", new int[]{year_c,month_c,day_c});
        	startActivity(intent1);
        	break;

		}
		return super.onMenuItemSelected(featureId, item);
	}
*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return this.gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	//填充数据
	private void initDater(String[] date) {
		smmarrydate = date[0];
		if (smmarrydate == null) {
			smmarrydate = currentDate;
		}

		initView();

		myText.setText(smmarrydate);
		Log.d(TAG, "initDate从云端同步下的数据,日期信息为 " + smmarrydate);

		if(smmarrydate == currentDate) {
			myText_2.setText(currentDate);
			myText_3.setText(currentDate);
			myText_4.setText(currentDate);
			myText_5.setText(currentDate);
			myText_6.setText(currentDate);
			myText_7.setText(currentDate);
		}
		else{
			myText_2.setText(date[1]);
			myText_3.setText(date[2]);
			myText_4.setText(date[3]);
			myText_5.setText(date[4]);
			myText_6.setText(date[5]);
			myText_7.setText(date[6]);
		}
	}


	private void findAllr() {
		Log.d(TAG, "从服务器获取用户信息");

		BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
		BmobQuery<UserMe> query = new BmobQuery<>();
		query.getObject(this, curUser.getObjectId(), new GetListener<UserMe>() {
			@Override
			public void onSuccess(UserMe userme) {
				String[] temp = new String[7];
				ArrayList<String> mdate = new ArrayList<String>();
				mdate = userme.getMarrydate();
				if (mdate == null) {
					for (int i = 0; i < 7; i++) {
						temp[i] = "";
					}
					Log.d(TAG, "没有信息在云端");
				} else {
					temp = (String[]) mdate.toArray(new String[mdate.size()]);
					Log.d(TAG, "查询到服务器端的任务为 " + mdate.size() + "  " + " 条" + userme.getObjectId());
				}

				initDater(temp);

			}

			@Override
			public void onFailure(int i, String s) {
				ToastUtils.showToast("从云端同步数据获取失败，请稍后再试");
			}
		});
	}

	private void saveAllr() {
		BmobUser curUser = BmobUser.getCurrentUser(this, UserMe.class);
		ArrayList list1 = new ArrayList();

		list1.add(smmarrydate);
		list1.add((String)myText_2.getText());
		list1.add((String)myText_3.getText());
		list1.add((String)myText_4.getText());
		list1.add((String)myText_5.getText());
		list1.add((String)myText_6.getText());
		list1.add((String)myText_7.getText());

		UserMe query = new UserMe();
		query.setMarrydate(list1);
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

	//添加头部的年份 闰哪月等信息
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		view.setBackgroundResource(R.drawable.top_day);
		textDate.append(calV.getShowYear()).append("年").append(
				calV.getShowMonth()).append("月").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("闰").append(calV.getLeapMonth()).append("月")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("年").append("(").append(
				calV.getCyclical()).append("年)");
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}

	//添加gridview
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		//取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(46);
		Log.d(TAG, "Width=" + Width + ";Height=" + Height);
		if (Width == 480 && Height == 800) {
			gridView.setColumnWidth(69);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		//	gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		//	gridView.setSelector(new ColorDrawable(Color.DKGRAY));
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setOnTouchListener(new OnTouchListener() {
			//将gridview中的触摸事件回传给gestureDetector
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return WDDateActivity.this.gestureDetector
						.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			//gridView中的每一个item的点击事件
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {

				//点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position && position <= endPosition) {
					String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
					String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
					String scheduleYear = calV.getShowYear();
					String scheduleMonth = calV.getShowMonth();
					String week = "";

					//smmarrydate = scheduleDay;
					//mmarrydate =(new SimpleDateFormat("yyyy-M-d")).parse(smmarrydate);
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
					//Date date = sdf.parse(scheduleDay);

					smmarrydate = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
					Log.d(TAG, "选中的日期是：" + smmarrydate);

					//直接跳转到需要添加日程的界面

					//得到这一天是星期几
					switch (position % 7) {
						case 0:
							week = "星期日";
							break;
						case 1:
							week = "星期一";
							break;
						case 2:
							week = "星期二";
							break;
						case 3:
							week = "星期三";
							break;
						case 4:
							week = "星期四";
							break;
						case 5:
							week = "星期五";
							break;
						case 6:
							week = "星期六";
							break;
					}

					ArrayList<String> scheduleDate = new ArrayList<String>();
					scheduleDate.add(scheduleYear);
					scheduleDate.add(scheduleMonth);
					scheduleDate.add(scheduleDay);
					scheduleDate.add(week);
					//scheduleDate.add(scheduleLunarDay);

					calV.setSeclection(position);//标记选中的格子，传给getview方法
					calV.notifyDataSetChanged();
					//gridView.invalidateViews();
					//gridView.setAdapter(calV);
				/*
					Intent intent = new Intent();
					intent.putStringArrayListExtra("scheduleDate", scheduleDate);
					intent.setClass(JHriqiActivity.this, ScheduleView.class);
					startActivity(intent);
				*/

					//对比选中日期与当前日期
					/*
					if (year_c == Integer.parseInt(scheduleYear)) {
						if (month_c < Integer.parseInt(scheduleMonth)) {
							;
						} else if (month_c == Integer.parseInt(scheduleMonth)) {
							if (day_c <= Integer.parseInt(scheduleDay)) {
								;
							} else {
								smmarrydate = currentDate;
								ToastUtils.showToast("当前日期是 " + currentDate + ",选择日期不能小于当前日期！");
							}
						} else {
							smmarrydate = currentDate;
							ToastUtils.showToast("当前日期是 " + currentDate + ",选择日期不能小于当前日期！");
						}
					} else if (year_c < Integer.parseInt(scheduleYear)) {
						;
					} else {
						smmarrydate = currentDate;
						ToastUtils.showToast("当前日期是 " + currentDate + ",选择日期不能小于当前日期！");
					}
					*/
					DateFormat df = new SimpleDateFormat("yyyy-M-d");
					ParsePosition ppst0 = new ParsePosition(0);
					Date currdt = df.parse(currentDate, ppst0);

					ParsePosition ppst1 = new ParsePosition(0);
					Date clickdt = df.parse(smmarrydate, ppst1);

					if(getGapCount(currdt, clickdt) <= 0)//对比选中日期与当前日期
						smmarrydate = currentDate;

					ParsePosition ppst2 = new ParsePosition(0);
					Date marrydt = df.parse(smmarrydate, ppst2);

					int days = getGapCount(currdt, marrydt);
					ToastUtils.showToast("距离婚期还有 " + days + " 天！");

					Date zzday2 = addGapCount(days, days / 5 * 4);
					Date zzday3 = addGapCount(days, days / 5 * 3);
					Date zzday4 = addGapCount(days, days / 5 * 3);
					Date zzday5 = addGapCount(days, days / 5 * 2);
					Date zzday6 = marrydt;
					Date zzday7 = addGapCount(days, days / 5);

					myText.setText(smmarrydate);

					myText_2.setText(df.format(zzday2));
					myText_3.setText(df.format(zzday3));
					myText_4.setText(df.format(zzday4));
					myText_5.setText(df.format(zzday5));
					myText_6.setText(df.format(zzday6));
					myText_7.setText(df.format(zzday7));

					saveAllr();
				}
			}
		});
		gridView.setLayoutParams(params);
	}

	/**
	 * 判断两个日期之间的天数
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getGapCount(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}

	/**
	 * 获取指定天数前的日期
	 *
	 * @param MarryDate
	 * @param days
	 * @return
	 */
	public static Date addGapCount(Integer MarryDate, Integer days) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.add(Calendar.DAY_OF_MONTH, MarryDate-days);
		return (Date) fromCalendar.getTime();
	}
}