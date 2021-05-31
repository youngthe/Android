package com.example.myapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    int oneLevelyear; //일병 진급해
    int oneLevelmonth; //일병 진급월
    int oneLevelDay; //일병 진급일
    int twoLevelyear; //상병 진급해
    int twoLevelmonth; //상병 진급월
    int twoLevelDay; //상병 진급일
    int threeLevelyear; //병장 진급해
    int threeLevelmonth; //병장 진급월
    int threeLevelDay; //병장 진급일
    Button bt1, bt2, bt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_level);
        }

        public void onClickBackSetting(View view){
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
            finish();
        }

    public void setDatePicker(){
        Calendar Date = Calendar.getInstance();
        int year = Date.get(Calendar.YEAR);
        int month = Date.get(Calendar.MONTH);
        int day = Date.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog1 = new DatePickerDialog(this, listener1,year,month,day);
        DatePickerDialog dialog2 = new DatePickerDialog(this, listener2,year,month,day);
        DatePickerDialog dialog3 = new DatePickerDialog(this, listener3,year,month,day);
        Button bt1 = findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener(){
                                   public void onClick(View view){
                                       dialog1.show();
                                   }
                               }
        );

        Button bt2 = findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener(){
                                   public void onClick(View view){
                                       //입대일 버튼 누를 시 데이트피커 창 오픈
                                       dialog2.show();
                                   }
                               }
        );
        Button bt3 = findViewById(R.id.bt3);
        bt3.setOnClickListener(new View.OnClickListener(){
                                   public void onClick(View view){
                                       //입대일 버튼 누를 시 데이트피커 창 오픈
                                       dialog3.show();
                                   }
                               }
        );
    }

    private DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            oneLevelyear = year;
            oneLevelmonth = (monthOfYear+1);
            oneLevelDay = dayOfMonth;
            bt1.setText(year+"."+(monthOfYear+1)+"."+ dayOfMonth);
        }
    };

    //전역 데이트피커 입력
    private DatePickerDialog.OnDateSetListener listener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            twoLevelyear = year;
            twoLevelmonth = (monthOfYear+1);
            twoLevelDay = dayOfMonth;
            bt2.setText(year+"."+(monthOfYear+1)+"."+ dayOfMonth);
        }
    };
    private DatePickerDialog.OnDateSetListener listener3 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            threeLevelyear = year;
            threeLevelmonth = (monthOfYear+1);
            threeLevelDay = dayOfMonth;
            bt3.setText(year+"."+(monthOfYear+1)+"."+ dayOfMonth);
        }
    };
}
