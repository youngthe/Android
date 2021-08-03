package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

        private static final int ImageCrop = 1;
        private static final int ImageSet = 2;
        int ComeYear=0, ComeMonth=0, ComeDay=0;
        int OutYear=0, OutMonth=0, OutDay=0;
        long totalWork=0;//총 복무일
        TextView Current_class; //현재 계급
        TextView Next_class; //다음 계급
        TextView textView_endDday;
        TextView textView_percent1;
        TextView textView_percent2;
        TextView textView_nextClassD_day;
        ProgressBar progressbar1;
        ProgressBar progressbar2;
        ImageView Main_background;
        ImageView Image1;
        ImageView Image2;
        ImageView Image3;
        ImageView Image4;
        Uri ImagefileUri;
        SQLiteDatabase SQLitedb;
        String backimg = "background.png";    // 이미지 이름
        String img1 = "image1.png";
        String img2 = "image2.png";
        String img3 = "image3.png";
        String img4 = "image4.png";
        String set_img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foundation();
        comming_img();//내부 저장소에 저장되어 있는 이미지 불러오기
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);
        foundation();
        comming_img();//내부 저장소에 저장되어 있는 이미지 불러오기
    }

    //내부 저장소에 저장되어 있는 이미지 불러오기
    public void comming_img(){
        String imgpath = getCacheDir() +"/"+ backimg; // 내부 저장소에 저장되어 있는 이미지 경로 저장
        Bitmap bm = BitmapFactory.decodeFile(imgpath); //imgpath에 존재하는 이미지 가져옴.
        Main_background = findViewById(R.id.Main_background);
        Main_background.setImageBitmap(bm);

        imgpath = getCacheDir() +"/"+ img1; //img1 가져옴
        bm = BitmapFactory.decodeFile(imgpath);
        Image1 = findViewById(R.id.ImageView1);
        Image1.setImageBitmap(bm);

        imgpath = getCacheDir() +"/"+ img2; //img2 가져옴
        bm = BitmapFactory.decodeFile(imgpath);
        Image2 = findViewById(R.id.ImageView2);
        Image2.setImageBitmap(bm);

        imgpath = getCacheDir() +"/"+ img3; //img3 가져옴
        bm = BitmapFactory.decodeFile(imgpath);
        Image3 = findViewById(R.id.ImageView3);
        Image3.setImageBitmap(bm);

        imgpath = getCacheDir() +"/"+ img4; //img4 가져옴
        bm = BitmapFactory.decodeFile(imgpath);
        Image4 = findViewById(R.id.ImageView4);
        Image4.setImageBitmap(bm);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ImageCrop){
            ImagefileUri = data.getData(); //갤러리에서 이미지 가져오기
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(ImagefileUri, "image/*");
                intent.putExtra("outputX", 350);// 크롭한 이미지의 x축 크기
                intent.putExtra("outputY", 350);// 크롭한 이미지의 y축 크기
                intent.putExtra("aspectX", 1);// crop 박스의 x축 크기
                intent.putExtra("aspectY", 1);// crop 박스의 y축 크기
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, ImageSet);
        }
        else if(requestCode==ImageSet){
            final Bundle extras = data.getExtras();
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap imgBitmap = extras.getParcelable("data");

                        if(set_img=="background.png"){
                            Main_background.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else if(set_img=="image1.png"){
                            Image1.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else if(set_img=="image2.png"){
                            Image2.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else if(set_img=="image3.png"){
                            Image3.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else{
                            Image4.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }
                    } catch (Exception e) {}
                }
        }

        /*
        switch(requestCode) {
            case ImageCrop:{
                ImagefileUri = data.getData(); //갤러리에서 이미지 가져오기
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(ImagefileUri, "image/*");
                intent.putExtra("outputX", 350);// 크롭한 이미지의 x축 크기
                intent.putExtra("outputY", 350);// 크롭한 이미지의 y축 크기
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

                        if(set_img=="background.png"){
                            Main_background.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else if(set_img=="image1.png"){
                            Image1.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else if(set_img=="image2.png"){
                            Image2.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else if(set_img=="image3.png"){
                            Image3.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }else{
                            Image4.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                            saveBitmapToJpeg(imgBitmap, set_img);    // 비트맵을 이미지 형태로 저장
                        }
                    } catch (Exception e) {}
                }
                break;
            }
            case RESULT_CANCELED:{
                finish();
            }
        }*/
    }

    public void onClickModify(View v){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    //기초적으로 앱이 실행될 때 연산값을 연산하는 함수
    private void foundation(){
        getData(); // 데이터베이스에서 입대 정보를 가져오는 함수
        int howmanyWork = countdday(ComeYear,ComeMonth,ComeDay); //얼마나 복무했는가
        long remainingWork = totalWork - howmanyWork; //얼마 남았는지
        textView_endDday = findViewById(R.id.endD_day);
        textView_endDday.setText("D-"+(remainingWork-1)); //얼마 남았는지 D-DAY 출력
        //군 복무 percent 구하는 함수
        textView_percent1 = findViewById(R.id.percent1);
        float howmanypercent = (((float)howmanyWork+1)/(float)totalWork)*(float)100;
        textView_percent1.setText(String.format("%.1f", howmanypercent)+"%");
        progressbar1 = findViewById(R.id.determinateBar1);
        progressbar1.setProgress((int)howmanypercent);
        WhatyourClass(ComeYear, ComeMonth, ComeDay, OutYear, OutMonth, OutDay);//현재 계급, 다음 계급, 다음계급까지 얼마나 남았는지 출력
    }

    //배경화면 클릭을 하면 갤러리를 키는 함수
    public void onClickBackground(View view){
        set_img = backimg;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ImageCrop);
    }
    public void Image1(View view){
        set_img = img1;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ImageCrop);
    }
    public void Image2(View view){
        set_img = img2;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ImageCrop);
    }
    public void Image3(View view){
        set_img = img3;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ImageCrop);
    }
    public void Image4(View view){
        set_img = img4;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ImageCrop);
    }

    //데이터베이스에서 데이터 가져오는 함수
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
                        "day integer)";
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


    //위 함수에서 사용하는 이미지 저장함수
    public void saveBitmapToJpeg(Bitmap bitmap, String set_img) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), set_img);    // 파일 경로와 이름 넣기
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
    public void WhatyourClass(int Comeyear, int Comemonth, int Comeday, int Outyear, int Outmonth, int Outday) {
        Calendar todayCal = Calendar.getInstance(); //오늘 날짜 가져오기
        Calendar startDay = Calendar.getInstance();  //입대일
        Calendar level1 = Calendar.getInstance(); //훈련병 //2020.03.09
        Calendar level2 = Calendar.getInstance(); //이병   //2020.05.09
        Calendar level3 = Calendar.getInstance(); //일병   //2020.11.09
        Calendar level4 = Calendar.getInstance(); //상병   //2021.05.09
        Calendar level5 = Calendar.getInstance(); //병장   //2021.12.08
        Comemonth=Comemonth-1;
        startDay.set(Comeyear, Comemonth, Comeday);
        level1.set(Comeyear, Comemonth + 1, Comeday); //훈련병(1개월)
        level2.set(Comeyear, Comemonth + 2, Comeday); //훈련병(1개월)+이병(2개월)
        level3.set(Comeyear, Comemonth + 8, Comeday); //이병(2개월)+일병(6개월)
        level4.set(Comeyear, Comemonth + 14, Comeday); //이병(2개월)+일병(6개월)+상병(6개월)
        level5.set(Outyear, Outmonth -1, (Outday+1)); //전역

        long today = todayCal.getTimeInMillis() / 86400000; //오늘 날짜
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
        textView_percent1 = findViewById(R.id.percent1);
        textView_percent2 = findViewById(R.id.percent2);
        //현재 계급, 다음계급, 다음계급까지 남은 일수 출력
        if(today < start) {
            HowmanyNextClass = start-today;
            Current_class.setText("민간인");
            Next_class.setText("훈련병");
            total = start-today;
            percent = ((float)(today+1-start)/(float)total)*(float)100;
        }else if (today < onelevel) {
            HowmanyNextClass = onelevel-today;
            Current_class.setText("훈련병");
            Next_class.setText("이병");
            total = onelevel-start;
            percent = ((float)(today+1-start)/(float)total)*(float)100;
        }else if (today < twolevel) {
            HowmanyNextClass = twolevel-today;
            Current_class.setText("이병");
            Next_class.setText("일병");
            total = twolevel-onelevel;
            percent = ((float)(today+1-onelevel)/(float)total)*(float)100;
        }else if (today < treelevel) {
            HowmanyNextClass = treelevel-today;
            Current_class.setText("일병");
            Next_class.setText("상병");
            total = treelevel-twolevel;
            percent = ((float)(today+1-twolevel)/(float)total)*(float)100;
        }else if (today < fourlevel) {
            HowmanyNextClass= fourlevel-today;
            Current_class.setText("상병");
            Next_class.setText("병장");
            total = fourlevel-treelevel;
            percent = ((float)(+1-treelevel)/(float)total)*(float)100;
        }else{
            HowmanyNextClass = fivelevel-today;
            Current_class.setText("병장");
            Next_class.setText("민간인");
            total = fivelevel-fourlevel;
            percent = ((float)(today+1-fourlevel)/(float)total)*(float)100;


        }
        //남은 날이 0 이상일 경우 전역
        if(HowmanyNextClass<=1){
            textView_endDday = findViewById(R.id.endD_day);
            textView_percent1.setText("100.0%");
            textView_percent2.setText("100.0%");
            textView_endDday.setText("전역");
            textView_nextClassD_day = findViewById(R.id.nextClassD_day);
            textView_nextClassD_day.setText(" 축하합니다 ♬");
            progressbar2 = findViewById(R.id.determinateBar2);
            progressbar2.setProgress((int)percent);
        }else {
            textView_percent2.setText(String.format("%.1f", percent) + "%");
            progressbar2 = findViewById(R.id.determinateBar2);
            progressbar2.setProgress((int)percent);
            textView_nextClassD_day = findViewById(R.id.nextClassD_day);//다음 계급까지 남은 날
            textView_nextClassD_day.setText("D-" + (int) (HowmanyNextClass - 1));
        }
    }
}