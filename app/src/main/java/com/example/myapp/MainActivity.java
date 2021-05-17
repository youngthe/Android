package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

        private static final int ImageCrop = 1;
        int ComeYear, ComeMonth, ComeDay;
        int OutYear, OutMonth, OutDay;
        long totalWork;//총 복무일
        int earlyOut;
        TextView Current_class; //현재 계급
        TextView Next_class; //다음 계급
        TextView textView_endDday;
        TextView textView_percent1;
        TextView textView_percent2;
        TextView textView_nextClassD_day;
        ProgressBar progressbar1;
        ProgressBar progressbar2;
        ImageView Main_background;
        String imgName = "myapp.png";    // 이미지 이름
        Uri ImagefileUri;
        SQLiteDatabase SQLitedb;
    public void onClickModify(View v){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getData(); // 데이터베이스에서 입대 정보를 가져오는 함수
        int howmanyWork = countdday(ComeYear,ComeMonth,ComeDay); //얼마나 복무했는가
        long remainingWork = totalWork - howmanyWork; //얼마 남았는지
        textView_endDday = findViewById(R.id.endD_day);
        textView_endDday.setText("D-"+(remainingWork-1)); //얼마 남았는지 D-DAY 출력
        //군 복무 percent 구하는 함수
        textView_percent1 = findViewById(R.id.percent1);
        float howmanypercent = ((float)howmanyWork/(float)totalWork)*(float)100;
        textView_percent1.setText(String.format("%.1f", howmanypercent)+"%");
        progressbar1 = findViewById(R.id.determinateBar1);
        progressbar1.setProgress((int)howmanypercent);
        WhatyourClass(ComeYear, ComeMonth, ComeDay);//현재 계급, 다음 계급, 다음계급까지 얼마나 남았는지 출력
        String imgpath = getCacheDir() +"/"+ imgName; // 내부 저장소에 저장되어 있는 이미지 경로 저장
        Bitmap bm = BitmapFactory.decodeFile(imgpath); //imgpath에 존재하는 이미지 가져옴.
        Main_background = findViewById(R.id.Main_background);
        Main_background.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋


       //배경화면 클릭을 하면 갤러리를 키는 함수
        Main_background.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, ImageCrop);
            }
        });
    }
    private void getData(){
        init_database(); //데이터베이스 생성
        init_table(); //테이블 생성
        load_values(); //값 가져오기

        //총 복무일 계산
        Calendar Comedate = Calendar.getInstance();
        Calendar Outdate = Calendar.getInstance();
        Comedate.set(ComeYear,ComeMonth,ComeDay);
        Outdate.set(OutYear,OutMonth,OutDay);
        totalWork = ((Outdate.getTimeInMillis()/86400000)-(Comedate.getTimeInMillis()/86400000))+1;
        totalWork -= earlyOut; //조기전역하는 만큼 총 복무일에서 빼기
    }
    private void init_database(){

        SQLitedb = null;
        File file = new File(getFilesDir(), "SQLitedb.db");
        try {
            SQLitedb = SQLiteDatabase.openOrCreateDatabase(file, null); //이는 데이터베이스 직접 호출 방식, SQLiteOpenHelper를 통한 호출 방식이 좋다고 함
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
                        "day integer, " +
                        "earlyOut integer)";
                SQLitedb.execSQL(tableSQL);
            }catch (SQLiteException e){
                Toast.makeText(getApplicationContext(),"error, can't not create table",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void load_values(){

        //입대날짜 데이터베이스에서 불러오기
        try {
            String SelectComeSQL = "select * from DDay where num=0";
            Cursor cursor1 = SQLitedb.rawQuery(SelectComeSQL, null);
            if(cursor1.moveToFirst()) {
                ComeYear = cursor1.getInt(1);
                ComeMonth = cursor1.getInt(2);
                ComeDay = cursor1.getInt(3);
                earlyOut = cursor1.getInt(4);
                cursor1.close();
            }

            String SelectOutSQL = "select * from DDay where num=1";
            Cursor cursor2 = SQLitedb.rawQuery(SelectOutSQL, null);
            if(cursor2.moveToFirst()) {
                OutYear = cursor2.getInt(1);
                OutMonth = cursor2.getInt(2);
                OutDay = cursor2.getInt(3);
                cursor2.close();
            }
        } catch (SQLiteException e){
            Toast.makeText(getApplicationContext(), "can't get ComeDate", Toast.LENGTH_SHORT).show();
        }
    }

    // 갤러리를 실행시키고 갤러리에서 가져온 이미지를 비트맵을 사용해서 저장시키는 함수
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
                    startActivityForResult(intent, 101);
                }
                case 101:{
                    final Bundle extras = data.getExtras();
                    if (resultCode == RESULT_OK) {

                        ContentResolver resolver = getContentResolver(); //ContentResolver 생성자 생성
                        try {
                            Bitmap imgBitmap = extras.getParcelable("data");
                            Main_background.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap);    // 비트맵을 이미지 형태로 저장
                        } catch (Exception e) {}
                    }
                    break;
            }
        }
    }

    //위 함수에서 사용하는 이미지 저장함수
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
        } catch (Exception e) {}
    }

    //얼마나 복무했는지 알려주는 함수
    public int countdday(int year, int month, int date){
        try{
            Calendar todayCal = Calendar.getInstance(); //오늘 날짜 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘 날짜를 가져와 변경시킴

            month -=1;
            ddayCal.set(year,month,date);

            long today = todayCal.getTimeInMillis()/86400000; //초 변환
            long day = ddayCal.getTimeInMillis()/86400000;
            long count = today - day;
            return (int) count;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    //계급이 무엇인지 나타내주는 함수
    public void WhatyourClass(int year, int month, int date) {
        Calendar todayCal = Calendar.getInstance(); //오늘 날짜 가져오기
        Calendar startDay = Calendar.getInstance();  //입대일
        Calendar level1 = Calendar.getInstance(); //훈련병 //2020.03.09
        Calendar level2 = Calendar.getInstance(); //이병   //2020.05.09
        Calendar level3 = Calendar.getInstance(); //일병   //2020.11.09
        Calendar level4 = Calendar.getInstance(); //상병   //2021.05.09
        Calendar level5 = Calendar.getInstance(); //병장   //2021.12.08
        month=month-1;
        startDay.set(year, month, date);
        level1.set(year, month + 1, date); //훈련병(1개월)
        level2.set(year, month + 2, date); //훈련병(1개월)+이병(2개월)
        level3.set(year, month + 8, date); //이병(2개월)+일병(6개월)
        level4.set(year, month + 14, date); //이병(2개월)+일병(6개월)+상병(6개월)
        level5.set(year, month + 21, date); //이병(2개월)+일병(6개월)+상병(6개월)+병장(7개월)

        long today = todayCal.getTimeInMillis() / 86400000;
        long start = startDay.getTimeInMillis() / 86400000;
        long onelevel = level1.getTimeInMillis() / 86400000;//훈련병(1개월)
        long twolevel = level2.getTimeInMillis() / 86400000;//이병(2개월)
        long treelevel = level3.getTimeInMillis() / 86400000;//이병(2개월)+일병(6개월)
        long fourlevel = level4.getTimeInMillis() / 86400000;//이병(2개월)+일병(6개월)+상병(6개월)
        long fivelevel = level5.getTimeInMillis() / 86400000;//이병(2개월)+일병(6개월)+상병(6개월)+병장(7개월)
        long HowmanyNextClass; //다음 계급까지 얼마나 남았는가
        long total; //각 계급별 총 근무일
        float percent; //다음 계급까지 현재 계급에서 복무한 총 일수
        Current_class = findViewById(R.id.Current_class); //현재 계급
        Next_class = findViewById(R.id.Next_class); //다음 계급
        textView_percent2 = findViewById(R.id.percent2);
        //현재 계급, 다음계급, 다음계급까지 남은 일수 출력
        if(today < start) {
            HowmanyNextClass = start-today;
            Current_class.setText("민간인");
            Next_class.setText("훈련병");
            total = start-today;
            percent = ((float)(today-start)/(float)total)*(float)100;
        }else if (today < onelevel) {
            HowmanyNextClass = onelevel-today;
            Current_class.setText("훈련병");
            Next_class.setText("이병");
            total = onelevel-start;
            percent = ((float)(today-start)/(float)total)*(float)100;
        }else if (today < twolevel) {
            HowmanyNextClass = twolevel-today;
            Current_class.setText("이병");
            Next_class.setText("일병");
            total = twolevel-onelevel;
            percent = ((float)(today-onelevel)/(float)total)*(float)100;
        }else if (today < treelevel) {
            HowmanyNextClass = treelevel-today;
            Current_class.setText("일병");
            Next_class.setText("상병");
            total = treelevel-twolevel;
            percent = ((float)(today-twolevel)/(float)total)*(float)100;
        }else if (today < fourlevel) {
            HowmanyNextClass= fourlevel-today;
            Current_class.setText("상병");
            Next_class.setText("병장");
            total = fourlevel-treelevel;
            percent = ((float)(today-treelevel)/(float)total)*(float)100;
        }else if (today < fivelevel) {
            HowmanyNextClass = fivelevel-today;
            Current_class.setText("병장");
            Next_class.setText("민간인");
            total = fivelevel-fourlevel;
            percent = ((float)(today-fourlevel)/(float)total)*(float)100;
        }else {
            HowmanyNextClass = today-fivelevel;
            Current_class.setText("민간인");
            Next_class.setText("민간인");
            percent = 100;
        }
        textView_percent2.setText(String.format("%.1f", percent)+"%");
        progressbar2 = findViewById(R.id.determinateBar2);
        progressbar2.setProgress((int)percent);
        textView_nextClassD_day = findViewById(R.id.nextClassD_day);//다음 계급까지 남은 날
        textView_nextClassD_day.setText("D-"+(int)(HowmanyNextClass-1));
    }
}