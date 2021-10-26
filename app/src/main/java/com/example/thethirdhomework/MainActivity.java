package com.example.thethirdhomework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private TextView tvSumPrice;
    private TextView tvLoanRate;
    private Button calButton;
    private TextView tvResultText;
    private Button btRepayDetail;
    private Boolean isInterest;
    private Boolean hasBusiness;
    private Boolean hasAccumulation;
    private TextView tv_payment;
    private TextInputEditText tvShopLoan;
    private TextInputEditText tvHouseFund;
    private Double mBusinessRatio=4.90;
    private Double mAccumulationRatio=3.25;
    private Spinner spHouseYear;
    private int mYear=10;
    private int[] yearsArray= new int[]{5, 10, 15, 20, 25, 30};
    private RadioGroup radioGroup;
    private Button btBenXi;
    private Button btBenJin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initView() {
        tvSumPrice = findViewById(R.id.tv_sum_price);
        tvLoanRate = findViewById(R.id.tv_loan_rate);
        calButton = findViewById(R.id.calSumButton);
        tvResultText = findViewById(R.id.resultText);
        btRepayDetail = findViewById(R.id.calRepayDetail);
        tv_payment = findViewById(R.id.tv_repay_detail);
        tvShopLoan = findViewById(R.id.tv_shop_loan);
        tvHouseFund = findViewById(R.id.tv_housing_fund);
        spHouseYear = findViewById(R.id.rate_choose);
        radioGroup = findViewById(R.id.interest_group);
        btBenXi = findViewById(R.id.bt_benxi);
        btBenJin = findViewById(R.id.bt_benjin);
    }

    private void initEvent() {
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSumPrice.getEditableText() != null &&tvLoanRate.getEditableText() != null) {
                    double loan_rate=Double.parseDouble(tvLoanRate.getEditableText().toString());
                    double price_sum = Double.parseDouble(tvSumPrice.getEditableText().toString());
                    double result=price_sum*loan_rate*0.01;
                    String resultString="您的贷款总金额为"+result+"万元";
                    tvResultText.setText(resultString);
                }else{
                    Toast.makeText(getApplicationContext(),"您的贷款金额和按揭部分输入有误！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        spHouseYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mYear=yearsArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id =group.getCheckedRadioButtonId();
                switch (id){
                    case R.id.bt_benxi:
                        isInterest = true;
                        break;
                    case R.id.bt_benjin:
                        isInterest = false;
                        break;
                }
            }
        });
        btRepayDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepayment();
            }
        });
    }
    // 根据贷款的相关条件，计算还款总额、利息总额，以及月供
    private void showRepayment() {
        Repayment businessResult = new Repayment();
        Repayment accumulationResult = new Repayment();
        if (!tvShopLoan.getEditableText().toString().equals("")) { // 申请了商业贷款
            double businessLoad = Double.parseDouble(tvShopLoan.getText().toString()) * 10000;
            double businessTime = mYear * 12;
            double businessRate = mBusinessRatio / 100;
            // 计算商业贷款部分的还款明细
            businessResult = calMortgage(businessLoad, businessTime, businessRate, isInterest);
        }
        if (!tvHouseFund.getEditableText().toString().equals("")) { // 申请了公积金贷款
            double accumulationLoad = Double.parseDouble(tvHouseFund.getText().toString()) * 10000;
            double accumulationTime = mYear * 12;
            double accumulationRate = mAccumulationRatio / 100;
            // 计算公积金贷款部分的还款明细
            accumulationResult = calMortgage(accumulationLoad, accumulationTime, accumulationRate, isInterest);
        }
        String desc = String.format("您的贷款总额为%s万元", formatDecimal(
                (businessResult.mTotal + accumulationResult.mTotal) / 10000, 2));
        desc = String.format("%s\n　　还款总额为%s万元", desc, formatDecimal(
                (businessResult.mTotal + businessResult.mTotalInterest +
                        accumulationResult.mTotal + accumulationResult.mTotalInterest) / 10000, 2));
        desc = String.format("%s\n其中利息总额为%s万元", desc, formatDecimal(
                (businessResult.mTotalInterest + accumulationResult.mTotalInterest) / 10000, 2));
        desc = String.format("%s\n　　还款总时间为%d月", desc, mYear * 12);
        if (isInterest) { // 如果是等额本息方式
            desc = String.format("%s\n每月还款金额为%s元", desc, formatDecimal(
                    businessResult.mMonthRepayment + accumulationResult.mMonthRepayment, 2));
        } else { // 如果是等额本金方式
            desc = String.format("%s\n首月还款金额为%s元，其后每月递减%s元", desc, formatDecimal(
                    businessResult.mMonthRepayment + accumulationResult.mMonthRepayment, 2),
                    formatDecimal(businessResult.mMonthMinus + accumulationResult.mMonthMinus, 2));
        }
        tv_payment.setText(desc);
    }



    // 精确到小数点后第几位
    private String formatDecimal(double value, int digit) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(digit, RoundingMode.HALF_UP);
        return bd.toString();
    }

    // 根据贷款金额、还款年限、基准利率，计算还款信息
    private Repayment calMortgage(double ze, double nx, double rate, boolean bInterest) {
        double zem = (ze * rate / 12 * Math.pow((1 + rate / 12), nx))
                / (Math.pow((1 + rate / 12), nx) - 1);
        double amount = zem * nx;
        double rateAmount = amount - ze;

        double benjinm = ze / nx;
        double lixim = ze * (rate / 12);
        double diff = benjinm * (rate / 12);
        double huankuanm = benjinm + lixim;
        double zuihoukuan = diff + benjinm;
        double av = (huankuanm + zuihoukuan) / 2;
        double zong = av * nx;
        double zongli = zong - ze;

        Repayment result = new Repayment();
        result.mTotal = ze;
        if (bInterest) {
            result.mMonthRepayment = zem;
            result.mTotalInterest = rateAmount;
        } else {
            result.mMonthRepayment = huankuanm;
            result.mMonthMinus = diff;
            result.mTotalInterest = zongli;
        }
        return result;
    }
}