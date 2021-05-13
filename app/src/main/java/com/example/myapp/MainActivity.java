package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
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

        int year = 2020;
        int month = 3;
        int date = 9;
        int totalWork = 640; //총 복무일
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

    public void onClickModify(View v){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int howmanyWork = countdday(year,month,date); //얼마나 복무했는가
        int remainingWork = totalWork - howmanyWork; //얼마 남았는지
        textView_endDday = findViewById(R.id.endD_day);
        textView_endDday.setText("D-"+(remainingWork-1)); //얼마 남았는지 D-DAY 출력

        //군 복무 percent 구하는 함수
        textView_percent1 = findViewById(R.id.percent1);
        float howmanypercent = ((float)howmanyWork/(float)totalWork)*(float)100;
        textView_percent1.setText(String.format("%.1f", howmanypercent)+"%");
        progressbar1 = findViewById(R.id.determinateBar1);
        progressbar1.setProgress((int)howmanypercent);
        WhatyourClass(year, month, date);//현재 계급, 다음 계급, 다음계급까지 얼마나 남았는지 출력


        Main_background = findViewById(R.id.Main_background);

        String imgpath = getCacheDir() +"/"+ imgName; // 내부 저장소에 저장되어 있는 이미지 경로 저장
        Bitmap bm = BitmapFactory.decodeFile(imgpath); //imgpath에 존재하는 이미지 가져옴.
        Main_background.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋


       //배경화면 클릭을 하면 갤러리를 키는 함수
        Main_background.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });
    }



    // 갤러리를 실행시키고 갤러리에서 가져온 이미지를 비트맵을 사용해서 저장시키는 함수
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 101:{
                final Bundle extras = data.getExtras();
                if (resultCode == RESULT_OK) {

                    ContentResolver resolver = getContentResolver(); //ContentResolver 생성자 생성
                    try {
                        //InputStream instream = resolver.openInputStream(ImagefileUri); //resolver 생성자에 openInputStream함수에 ImageUri입력
                        Bitmap imgBitmap = extras.getParcelable("data");
                        //Bitmap imgBitmap = BitmapFactory.decodeStream(instream); //Bitmap factory에 decodeStream에 instream 입력.
                        Main_background.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                        //instream.close();   // 스트림 닫아주기
                        saveBitmapToJpeg(imgBitmap);    // 비트맵을 이미지 형태로 저장
                    } catch (Exception e) {}
                }
                break;
        }
            case 1:{
                ImagefileUri = data.getData(); //갤러리에서 이미지 가져오기
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(ImagefileUri, "image/*");

                //300은 성공
                intent.putExtra("outputX", 340);// 크롭한 이미지의 x축 크기
                intent.putExtra("outputY", 340);// 크롭한 이미지의 y축 크기
                intent.putExtra("aspectX", 1);// crop 박스의 x축 크기
                intent.putExtra("aspectY", 1);// crop 박스의 y축 크기
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 101);
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
        progressbar2 = findViewById(R.id.determinateBar2);
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
        progressbar2.setProgress((int)percent);
        textView_nextClassD_day = findViewById(R.id.nextClassD_day);//다음 계급까지 남은 날
        textView_nextClassD_day.setText("D-"+(int)(HowmanyNextClass-1));
    }
}