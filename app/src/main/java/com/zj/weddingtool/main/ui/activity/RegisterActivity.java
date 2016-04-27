package com.zj.weddingtool.main.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zj.weddingtool.R;
import com.zj.weddingtool.base.ui.BaseActivity;
import com.zj.weddingtool.base.util.Utils;
import com.zj.weddingtool.main.model.User;
import com.umeng.analytics.MobclickAgent;

import cn.bmob.v3.listener.SaveListener;

/**
 * 注册界面
 * 
 * @date 2014-4-24
 * @author Stone
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = "RegisterActivity";

	private Button btnReg;
	private EditText etUsername;
	private EditText etPassword;
	private EditText etComfirmPsd;
	private EditText etPhone;

	private String username = null;
	private String password = null;
	private String comfirmPsd = null;
	private String phone = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);

		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		etComfirmPsd = (EditText) findViewById(R.id.et_comfirm_psd);
		etPhone = (EditText) findViewById(R.id.et_phone);

		btnReg = (Button) findViewById(R.id.btn_reg_now);
		btnReg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_reg_now:
			username = etUsername.getText().toString();
			password = etPassword.getText().toString();
			comfirmPsd = etComfirmPsd.getText().toString();
			phone = etPhone.getText().toString();
			if(!Utils.isNetworkAvailable(this)) {
				toast("亲, 木有网络 ( ⊙ o ⊙ ) ");
			} else if (username.equals("") || password.equals("")
					|| comfirmPsd.equals("") || phone.equals("")) {
				toast("亲, 不填完整, 不能拿到身份证, ~~~~(>_<)~~~~ ");
			} else if (!comfirmPsd.equals(password)) {
				toast("亲, 你手抖了下, 两次密码输入不一致");
			} else if(!Utils.isPhoneNumberValid(phone)) {
				toast("亲, 请输入正确的手机号码");
			}else {
				// 开始提交注册信息
				User bu = new User();
				bu.setUsername(username);
				bu.setPassword(password);
				bu.setPhone(phone);
				bu.signUp(this, new SaveListener() {
					@Override
					public void onSuccess() {
						toast("注册成功");
						Intent backLogin = new Intent(RegisterActivity.this,
								com.zj.weddingtool.main.ui.activity.LoginActivity.class);
						startActivity(backLogin);
						RegisterActivity.this.finish();
					}

					@Override
					public void onFailure(int arg0, String msg) {
						toast("注册失败，请换个用户名稍后重试");
					}

				});
			}
			break;

		default:
			break;
		}
	}

	public void toast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	};
	
	@Override
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("RegisterActivity"); //统计页面
	    MobclickAgent.onResume(this);          //统计时长
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("RegisterActivity"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}

}
