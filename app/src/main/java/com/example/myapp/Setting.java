package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Setting extends AppCompatActivity {
    Button bt1;
    Button bt2;
    SQLiteDatabase SQLitedb;
    int ComeYear, ComeMonth, ComeDay;
    int OutYear, OutMonth, OutDay;
    int earlyOut=0;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //DatePickerDialog 객체 dialog1, dialog2 생성
        DatePickerDialog dialog1 = new DatePickerDialog(this, listener1, 2021, 3, 18);
        DatePickerDialog dialog2 = new DatePickerDialog(this, listener2, 2021, 3, 18);

        //bt1(입대일) 입력시에 데이트피커 창 오픈
        bt1 = findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog1.show();
            }
        });

        //bt2(전역일) 입력시에 데이트피커 창 오픈
        bt2 = findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog2.show();
            }
        });

        //데이터베이스 생성.
        init_database();

        //테이블 생성
        init_table();

        //값 입력
        load_values();

        //저장하기 버튼을 누르면 실행
        Button save_button = findViewById(R.id.set_button2);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_values();
            }
        });
    }

    private void load_values(){

        //입대날짜 데이터베이스에서 불러오기
        try {
            String SelectComeSQL = "select * from DDay where num=0";
            String ComeDate;
            Cursor cursor1 = SQLitedb.rawQuery(SelectComeSQL, null);
            if(cursor1.moveToFirst()) {
                ComeYear = cursor1.getShort(1);
                ComeMonth =cursor1.getShort(2);
                ComeDay =cursor1.getShort(3);
                ComeDate = ComeYear+"."+ComeMonth+"."+ComeDay;
                bt1 = findViewById(R.id.bt1);
                bt1.setText(ComeDate);
                cursor1.close();
            }

            String SelectOutSQL = "select * from DDay where num=1";
            String OutDate;
            Cursor cursor2 = SQLitedb.rawQuery(SelectOutSQL, null);
            if(cursor2.moveToFirst()) {
                OutYear = cursor2.getShort(1);
                OutMonth =cursor2.getShort(2);
                OutDay =cursor2.getShort(3);
                OutDate = OutYear+"."+OutMonth+"."+OutDay;
                bt2 = findViewById(R.id.bt2);
                bt2.setText(OutDate);
                cursor2.close();
            }
        } catch (SQLiteException e){
            Toast.makeText(getApplicationContext(), "can't get ComeDate", Toast.LENGTH_SHORT).show();
        }
    }

    private void init_database(){

        SQLitedb = null;
        File file = new File(getFilesDir(), "SQLitedb.db");
        try {
            SQLitedb = SQLiteDatabase.openOrCreateDatabase(file, null); //이는 데이터베이스 직접 호출 방식, SQLiteOpenHelper를 통한 호출 방식이 좋다고 함
            Toast.makeText(getApplicationContext(), "success create database! ",Toast.LENGTH_SHORT).show();
        }catch (SQLiteException e){
            Toast.makeText(getApplicationContext(), "can't not create database",Toast.LENGTH_SHORT).show();
        }
    }

    private void init_table(){
        if(SQLitedb != null) {
            try {
                String tableSQL = "Create table if not exists DDay " +
                        "(num integer , " +
                        "year integer, " +
                        "month integer, " +
                        "day integer," +
                        "earlyOut integer)";
                SQLitedb.execSQL(tableSQL);
            }catch (SQLiteException e){
                Toast.makeText(getApplicationContext(),"error, can't not create table",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void save_values(){

        EditText earlyOutText = findViewById(R.id.earlyOutText);
        earlyOut=Integer.parseInt( earlyOutText.getText().toString());
        if(SQLitedb != null){
            try {
                //테이블에 존재하는 모든 튜플 삭제
                SQLitedb.execSQL("Delete from DDay");
                //입력한 데이터 저장
                String ComeloadSQL = "insert into DDay (num, year, month, day, earlyOut) values ("+
                        ""+'0'+",'"+ComeYear+"','"+ComeMonth+"','"+ComeDay+"','"+earlyOut+")";
                SQLitedb.execSQL(ComeloadSQL);

                String OutloadSQL = "insert into DDay (num, year, month, day) values ("+
                        ""+'1'+",'"+OutYear+"','"+OutMonth+"','"+OutDay+"')";
                SQLitedb.execSQL(OutloadSQL);

                Toast.makeText(getApplicationContext(), "success input values", Toast.LENGTH_SHORT).show();
            }catch (SQLiteException e){
                Toast.makeText(getApplicationContext(),"can't not insert values", Toast.LENGTH_SHORT).show();
            }
        }
        Intent DataPutIntent = new Intent();
        DataPutIntent.putExtra("ComeYear", ComeYear);
        DataPutIntent.putExtra("ComeMonth", ComeMonth);
        DataPutIntent.putExtra("ComeDay", ComeDay);
        DataPutIntent.putExtra("OutYear", OutYear);
        DataPutIntent.putExtra("OutMonth", OutMonth);
        DataPutIntent.putExtra("OutDay", OutDay);
        setResult(RESULT_OK, DataPutIntent);
    }


    //입대 데이트피커 입력
    private DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ComeYear = year;
            ComeMonth = (monthOfYear+1);
            ComeDay = dayOfMonth;
            bt1.setText(year+"."+(monthOfYear+1)+"."+ dayOfMonth);
        }
    };

    //전역 데이트피커 입력
    private DatePickerDialog.OnDateSetListener listener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            OutYear = year;
            OutMonth = (monthOfYear+1);
            OutDay = dayOfMonth;
            bt2.setText(year+"."+(monthOfYear+1)+"."+ dayOfMonth);
        }
    };
}