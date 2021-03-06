package org.jiangtao.lifetime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jiangtao.application.LifeApplication;
import org.jiangtao.bean.User;
import org.jiangtao.networkutils.LoadHeadImage;
import org.jiangtao.sql.UserBusinessImpl;
import org.jiangtao.utils.BitmapUtils;
import org.jiangtao.utils.Code;
import org.jiangtao.utils.ConstantValues;
import org.jiangtao.utils.JSONUtil;
import org.jiangtao.utils.LogUtils;
import org.jiangtao.utils.TurnActivity;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 登陆界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LinearLayout mLinearLayout;
    private EditText mEditTextUserEmal;
    private EditText mEditTextPassWord;
    private String userName;
    private String passWord;
    private UserBusinessImpl userBusiness;
    public User user;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x111) {
                mDialog.dismiss();
//                new Intent(LoginActivity.this, IndexActivity.class);
                LogUtils.d(TAG, "什么情况");
                Intent intent = getIntent();
                intent.putExtra("flag", true);
                LoginActivity.this.setResult(Code.RESULLTCODE_LOGINSUCCESS_NOPICTURE, intent);
                finish();
            }
        }
    };
    private ProgressDialog mDialog;

    public LoginActivity() {
        userBusiness = new UserBusinessImpl(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControl();
        openDialog();
        getEditTextValue();
    }

    public void openDialog() {
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setTitle(R.string.login);
        mDialog.setMessage(getResources().getString(R.string.logining));
        mDialog.setIcon(R.drawable.login);
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(true);
    }

    /**
     * 获得EditText的值
     */
    private void getEditTextValue() {
        userName = mEditTextUserEmal.getText().toString().trim();
        passWord = mEditTextPassWord.getText().toString().trim();
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        mLinearLayout = (LinearLayout) findViewById(R.id.activity_login_container);
        mEditTextUserEmal = (EditText) findViewById(R.id.personal_login_username);
        mEditTextPassWord = (EditText) findViewById(R.id.personal_login_password);
        mDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //新用户注册
            case R.id.personal_login_newuser: {
                TurnActivity.turnRegisterActivity(LoginActivity.this);
                break;
            }
            //跳转到找回密码界面
            case R.id.activity_login_forget_password: {

                Intent intent = new Intent(LoginActivity.this, RetrievePasswordActivity.class);
                startActivityForResult(intent, Code.FORGOT_PASSWORD_REQUESTCODE);
                break;
            }

            /**
             * 获得结果后验证
             * 验证后提交服务器请求。
             * 返回的结果为空，说明不存在
             * 返回结果不为空，获得解析其结果
             * 回调到personalFragment
             */
            case R.id.personal_login_FrameLayout_buttom_btn_login: {
                LogUtils.d(TAG, "提示。。。。。");
                LogUtils.d(TAG, userName);
                LogUtils.d(TAG, passWord);
                getEditTextValue();
                if (userName.equals("") || passWord.equals("") || userName.equals(null) || passWord.equals(null)) {
                    Snackbar.make(mLinearLayout, R.string.input_not_null,
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    mDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            responseLoginInformation();
                        }
                    }).start();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void responseLoginInformation() {
        getEditTextValue();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("userEmail", userName);
        builder.add("passWord", passWord);
        Request request = new Request.Builder().url(
                ConstantValues.loginUrl
        ).post(builder.build()).build();
        LifeApplication.getCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Snackbar.make(mLinearLayout, R.string.runtime_error,
                        Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String userInformation = response.body().string();
                try {
                    JSONObject object = new JSONObject(userInformation);
                    user = (User) JSONUtil.JSONToObj(userInformation, User.class);
                    LogUtils.d(TAG, "*******" + user.getUser_headpicture());
                    /**
                     * 开启网络请求
                     * 加载图片
                     */
                    if (user != null) {
                        LifeApplication.isLogin = true;
                        LifeApplication.user_id = user.getUser_id();
                        LifeApplication.user_email = user.getUser_email();
                        LifeApplication.user_name = user.getUser_name();
                        LogUtils.d(TAG, Environment.getExternalStorageDirectory() + "/lifetime/headImage/user.jpg");
                        LoadHeadImage.getInstance().new BitmapAsyncTask().execute(
                                user.getUser_headpicture(), ConstantValues.userImageUrl
                        );
                        LoadHeadImage.getInstance().BitmapListener(new LoadHeadImage.BitmapCallBack() {
                            @Override
                            public void sendBitmap(Bitmap bitmap) {
                                if (bitmap != null) {
                                    BitmapUtils.savePhotoToSDCard(ConstantValues.saveImageUri,
                                            user.getUser_name() + ".png", bitmap);
                                    try {
                                        userBusiness.insertUser(user);
                                        LogUtils.d(TAG, ">>><<<<" + user.toString());
                                        Message msg = new Message();
                                        msg.what = 0x111;
                                        msg.obj = "true";
                                        handler.sendMessage(msg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        Snackbar.make(mLinearLayout, R.string.please_register,
                                Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
