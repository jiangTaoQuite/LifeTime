package org.jiangtao.lifetime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jiangtao.application.LifeApplication;
import org.jiangtao.utils.ConstantValues;
import org.jiangtao.utils.LogUtils;
import org.jiangtao.utils.ValidateEmailAndNumber;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 根据邮箱获得注册信息
 */
public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = RegisterActivity.class.getSimpleName();
    private EditText mUserNameEditText;
    private EditText mPassWordEditText;
    private EditText mRepeatPassWordEditText;
    private EditText mEmailEditText;
    private EditText mEmailValidateEditText;
    private Button mSendValidateButton;
    private Button mResetButton;
    private Button mRegisterButton;
    private int flag;
    private String mUserName;
    private String mPassWord;
    private String mRepeatPassWord;
    private String mEmail;
    private String mValidatValue;
    //返回的验证码校验
    private String netWorkValidatevalue;
    private ScrollView container;
    private CountDownTimer mCountDownTimer;
    public long timeRemain;
    public ProgressDialog mDialogs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        flag = 1;
        initEditText();
        getEditTextValue();
        this.getSupportActionBar().hide();
    }

    /**
     * 初始化EditText控件
     */
    private void initEditText() {
        mUserNameEditText = (EditText) findViewById(R.id.activity_register_et_username);
        mPassWordEditText = (EditText) findViewById(R.id.activty_register_tv_password);
        mRepeatPassWordEditText = (EditText) findViewById(R.id.activity_register_tv_repassword);
        mEmailEditText = (EditText) findViewById(R.id.activity_register_tv_email);
        mEmailValidateEditText = (EditText) findViewById(R.id.activity_register_email_check);
        container = (ScrollView) findViewById(R.id.register_container);
        mSendValidateButton = (Button) findViewById(R.id.activity_register_btn_sentcheckemil);
        mRegisterButton = (Button) findViewById(R.id.btn_activity_register);
        mResetButton = (Button) findViewById(R.id.btn_activity_reset);
        mDialogs = new ProgressDialog(this);
    }

    public void openDialog() {

        mDialogs.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialogs.setTitle(R.string.register);
        mDialogs.setMessage(getResources().getString(R.string.registering));
        mDialogs.setIcon(R.drawable.register);
        mDialogs.setIndeterminate(false);
        mDialogs.setCancelable(true);
        mDialogs.show();
    }

    /**
     * 两个按钮的单击事件
     * 联网获取图片和json
     *
     * @param view
     */
    public void registerOnClick(View view) throws JSONException {
        switch (view.getId()) {
            /**
             * 点击注册
             */
            case R.id.btn_activity_register: {
                getEditTextValue();
                if (ValidateEmailAndNumber.isNumeric(mValidatValue)) {
                    if (netWorkValidatevalue.equals(mValidatValue)) {
                        /**
                         * 保存用户信息到数据库
                         */
                        openDialog();
                        new AsyncTask<Void, Void, Void>() {
                            int id;

                            @Override
                            protected Void doInBackground(Void... params) {
                                FormEncodingBuilder builder = new FormEncodingBuilder();
                                builder.add("userName", mUserName);
                                builder.add("passWord", mPassWord);
                                builder.add("email", mEmail);
                                Request request = new Request.Builder().url(
                                        ConstantValues.registerInformationUrl
                                ).post(builder.build()).build();
                                LifeApplication.getCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {
                                        LogUtils.d(TAG, request.toString());
                                    }

                                    @Override
                                    public void onResponse(Response response) throws IOException {
                                        String emailJson = response.body().string();
                                        LogUtils.d(TAG, emailJson);
                                        try {
                                            JSONObject object = new JSONObject(emailJson);
                                            boolean flag = object.getBoolean("flag");
                                            id = object.getInt("id");
                                            if (flag) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mDialogs.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(RegisterActivity.this,
                                                                IndexActivity.class);
                                                        intent.putExtra("id", id);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                return null;
                            }
                        }.execute();
                    } else {
                        Snackbar.make(container, R.string.password_error, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Snackbar.make(container, R.string.password_error, Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            /**
             * 点击发送验证码
             */
            case R.id.activity_register_btn_sentcheckemil: {
                flag = 1;
                if (validateValue()) {
                    mSendValidateButton.setEnabled(false);
                    timeRemain(60l);
                    //网络请求s
                    FormEncodingBuilder builder = new FormEncodingBuilder();
                    builder.add("email", mEmail);
                    Request request = new Request.Builder().url(
                            ConstantValues.verificationCodeUrl
                    ).post(builder.build()).build();
                    LifeApplication.getCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            LogUtils.d(TAG, request.toString());
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String emailJson = response.body().string();
                            LogUtils.d(TAG, emailJson);
                            try {
                                JSONObject object = new JSONObject(emailJson);
                                netWorkValidatevalue = object.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
                break;

            }
            case R.id.btn_activity_reset: {
                mResetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUserNameEditText.setText("");
                        mPassWordEditText.setText("");
                        mRepeatPassWordEditText.setText("");
                        mEmailEditText.setText("");
                        mEmailValidateEditText.setText("");
                    }
                });
            }
            break;
        }

    }

    /**
     * 获取各个控件的值
     */
    public void getEditTextValue() {
        mUserName = mUserNameEditText.getText().toString().trim();
        mPassWord = mPassWordEditText.getText().toString().trim();
        mRepeatPassWord = mRepeatPassWordEditText.getText().toString().trim();
        mEmail = mEmailEditText.getText().toString().trim();
        mValidatValue = mEmailValidateEditText.getText().toString().trim();
    }

    /**
     * 发送到服务器之前的检查
     */
    private boolean validateValue() {
        if (flag == 1) {
            getEditTextValue();
            if (mUserName != null && mPassWord != null && mRepeatPassWord != null
                    && mEmail != null && mUserName != "" && mPassWord != "" && mRepeatPassWord != ""
                    && mEmail != "") {
                if (ValidateEmailAndNumber.isEmail(mEmail)) {

                    if (ValidateEmailAndNumber.isCommonValue(mPassWord, mRepeatPassWord)) {
                        return true;
                    } else {
                        mPassWordEditText.setText("");
                        mRepeatPassWordEditText.setText("");
                        flag = 1;
                        Snackbar.make(container, R.string.password_error, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Snackbar.make(container, R.string.email_error, Snackbar.LENGTH_SHORT)
                            .show();
                    flag = 1;
                }
            } else {
                Snackbar.make(container, R.string.input_not_null, Snackbar.LENGTH_SHORT)
                        .show();
                flag = 1;
            }
        }
        return false;
    }

    /**
     * 按钮防止长点击
     *
     * @param time
     */
    public void timeRemain(long time) {
        mCountDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long time) {
                mSendValidateButton.setText(time / 1000 + "秒");
                timeRemain = time / 1000;
                if (time <= 0) {
                    timeRemain = 60l;
                }
            }

            @Override
            public void onFinish() {
                mSendValidateButton.setText(R.string.regest_btn_sent_checkemail);
                mSendValidateButton.setEnabled(true);
            }
        }.start();
    }


}