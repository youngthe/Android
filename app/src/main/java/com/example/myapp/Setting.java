package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class Setting extends AppCompatActivity {
    private static final int ImageCrop = 1;
    private static final int ImageSet = 2;
    Button bt1;
    Button bt2;
    SQLiteDatabase SQLitedb;
    int ComeYear, ComeMonth, ComeDay;
    int OutYear, OutMonth, OutDay;
    String imgName = "myapp.png";
    Uri ImagefileUri;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init_database();//데이터베이스 생성.
        init_table();//테이블 생성
        load_values();//값 입력
        setDatePicker();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case ImageCrop:{
                ImagefileUri = data.getData(); //갤러리에서 이미지 가져오기
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(ImagefileUri, "image/*");
                intent.putExtra("outputX", 340);// 크롭한 이미지의 x축 크기
                intent.putExtra("outputY", 340);// 크롭한 이미지의 y축 크기
                intent.putExtra("aspectX", 1);// crop 박스의 x축 크기
                intent.putExtra("aspectY", 1);// crop 박스의 y축 크기
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, ImageSet);
                break;
            }
            case ImageSet:{
                final Bundle extras = data.getExtras();
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap imgBitmap = extras.getParcelable("data");
                        saveBitmapToJpeg(imgBitmap);    // 비트맵을 이미지 형태로 저장
                        Intent imgIntent = new Intent();
                        imgIntent.putExtra("Image", imgBitmap);
                        startActivity(imgIntent);
                    } catch (Exception e) {}
                }
                break;
            }
        }

    }
    public void onClick(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ImageCrop);
    }
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
        } catch (Exception e) {}
    }
    public void setDatePicker(){
        DatePickerDialog dialog1 = new DatePickerDialog(this, listener1, 2020, 2, 9);
        DatePickerDialog dialog2 = new DatePickerDialog(this, listener2, 2021, 11, 8);
        bt1 = findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                dialog1.show();
            }
        }
        );

        bt2 = findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //입대일 버튼 누를 시 데이트피커 창 오픈
                dialog2.show();
            }
        }
        );
    }

    //돌아가기 버튼 누르면 실행
    public void onClickBackHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
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

    //저장하기 버튼 누르면 save_values 함수 실행
    public void onClickSave(View view){
        save_values();
    }

    //데이터베이스 생성
    private void init_database(){

        SQLitedb = null;
        File file = new File(getFilesDir(), "SQLitedb.db");
        try {
            SQLitedb = SQLiteDatabase.openOrCreateDatabase(file, null); //이는 데이터베이스 직접 호출 방식, SQLiteOpenHelper를 통한 호출 방식이 좋다고 함
        }catch (SQLiteException e){
            Toast.makeText(getApplicationContext(), "can't not create database",Toast.LENGTH_SHORT).show();
        }
    }

    //입대날짜 데이터베이스에서 불러오기
    private void load_values(){
        if(SQLitedb != null) {
            try {
                String SelectComeSQL = "select * from DDay where num=0";
                String ComeDate;
                Cursor cursor1 = SQLitedb.rawQuery(SelectComeSQL, null);
                if (cursor1.moveToFirst()) {
                    ComeYear = cursor1.getInt(1);
                    ComeMonth = cursor1.getInt(2);
                    ComeDay = cursor1.getInt(3);
                    ComeDate = ComeYear + "." + ComeMonth + "." + ComeDay;
                    bt1 = findViewById(R.id.bt1);
                    bt1.setText(ComeDate);
                    cursor1.close();
                }

                String SelectOutSQL = "select * from DDay where num=1";
                String OutDate;
                Cursor cursor2 = SQLitedb.rawQuery(SelectOutSQL, null);
                if (cursor2.moveToFirst()) {
                    OutYear = cursor2.getInt(1);
                    OutMonth = cursor2.getInt(2);
                    OutDay = cursor2.getInt(3);
                    OutDate = OutYear + "." + OutMonth + "." + OutDay;
                    bt2 = findViewById(R.id.bt2);
                    bt2.setText(OutDate);
                    cursor2.close();
                }
            } catch (SQLiteException e) {
                Toast.makeText(getApplicationContext(), "can't get ComeDate", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init_table(){
        if(SQLitedb != null) {
            try {
                String tableSQL = "Create table if not exists DDay " +
                        "(num integer , " +
                        "year integer, " +
                        "month integer, " +
                        "day integer)";
                SQLitedb.execSQL(tableSQL);
            }catch (SQLiteException e){
                Toast.makeText(getApplicationContext(),"error, can't not create table",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void save_values(){


        if(SQLitedb != null){
            try {
                //테이블에 존재하는 모든 튜플 삭제
                SQLitedb.execSQL("Delete from DDay");
                //입력한 데이터 저장
                String ComeloadSQL = "insert into DDay (num, year, month, day) values ("+
                        ""+'0'+",'"+ComeYear+"','"+ComeMonth+"','"+ComeDay+"')";
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

}