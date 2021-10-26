package com.example.thethirdhomework;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText tvPhoneNumber;
    private TextInputEditText tvPasswordNumber;
    private Button btLogin;
    private TextView tvVerificationCode;
    private TextView tvUserIdentity;

    private int flag=1; //1表示个人用户，-1表示企业用户
    private ImageView ivLoginUser;
    private TextView tv_presentation;
    private static final int NOTIFICATION_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        initView();
        initEvent();
    }

    private void initView() {
        tvPhoneNumber = findViewById(R.id.tv_phoneNumberInput);
        tvPasswordNumber = findViewById(R.id.tv_passwordInput);
        btLogin = findViewById(R.id.login_button);
        tvVerificationCode = findViewById(R.id.tv_verification_code);
        tvUserIdentity = findViewById(R.id.tv_userIdentity);
        ivLoginUser = findViewById(R.id.changeLoginUser);
        tv_presentation = findViewById(R.id.tv_account_presentation);
    }

    private void initEvent() {
        tvPasswordNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !String.valueOf(s).equals("") ){
                    Log.d(TAG, String.valueOf(s));
                        btLogin.setBackgroundColor(Color.parseColor("#0055FE"));
                    }
                else {
                    btLogin.setBackgroundColor(Color.parseColor("#9AD0FF"));
                }
                }
        });
        tvVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginAccount = tvPhoneNumber.getEditableText().toString();
                if (!loginAccount.equals("")) {
                    setNotification("0786");
                    Intent intent=new Intent(LoginActivity.this,VerificationCodeActivity.class);
                    startActivity(intent);
                }else{
                    tv_presentation.setVisibility(View.VISIBLE);
                }
            }
        });
        ivLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag==1) {
                    ivLoginUser.setImageResource(R.mipmap.business);
                    tvUserIdentity.setText("企业用户");
                    flag=-flag;
                }else {
                    ivLoginUser.setImageResource(R.mipmap.individual);
                    tvUserIdentity.setText("个人用户");
                    flag=-flag;
                }
            }
        });
    }


    public void loginEvent(View view) {
        String loginPassword = tvPasswordNumber.getEditableText().toString();
        String loginAccount = tvPhoneNumber.getEditableText().toString();
        if (loginAccount != null && !loginAccount.equals("")) {
            if (loginPassword !=null && !loginPassword.equals("")) {
                if (loginAccount.equals("17366638195") && loginPassword.equals("123456")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }else{
                Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"手机号不可为空",Toast.LENGTH_SHORT).show();
        }
    }


    //传送验证码的通知
    public void setNotification(String notificationText){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0,
                new Intent(this, VerificationCodeActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        Notification notify3 = new Notification.Builder(this)
                .setSmallIcon(R.drawable.business)
                .setTicker("TickerText:"  )
                .setContentTitle("登录验证")
                .setContentText("您的验证码是："+notificationText)
                .setContentIntent(pendingIntent3).setNumber(1).build(); // 需要注意build()是在API
        // level16及之后增加的，API11可以使用getNotificatin()来替代
        notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        manager.notify(NOTIFICATION_FLAG, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
    }
}