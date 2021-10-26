package com.example.thethirdhomework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jyn.vcview.VerificationCodeView;

public class VerificationCodeActivity extends AppCompatActivity {


    private VerificationCodeView vcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        initView();
        initEvent();
    }

    private void initView() {
        vcv = findViewById(R.id.verification_view);
    }

    private void initEvent() {
        vcv.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
            @Override
            public void onTextChange(View view, String content) {

            }

            @Override
            public void onComplete(View view, String content) {
                if (content.equals("0786")) {
                    Intent intent=new Intent(VerificationCodeActivity.this,MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"您的验证码输入有误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}