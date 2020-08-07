package com.example.nightlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Con_lithophane extends AppCompatActivity {

    Intent intent;
    ImageView mImageView;
    TextView mResultTv;
    View mColorView;
    TextView Viewrgb;
    SeekBar lcBar;
    SharedPreferences shareData;
    String id;
    Bitmap bitmap;
    ImageView btnOn;
    MainDeviceData deviceData;
    Button btnReservation;

    int r,g,b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_lithophane);

        shareData = getSharedPreferences("shareData", MODE_PRIVATE);
        id = shareData.getString("userId", null);

        intent = getIntent();
        deviceData = (MainDeviceData) intent.getSerializableExtra("data");

        mImageView = findViewById(R.id.imageView);
        mResultTv = findViewById(R.id.resultTv);
        mColorView = findViewById(R.id.colorView);
        Viewrgb = findViewById(R.id.rgb);
        lcBar = findViewById(R.id.LCBar);
        btnOn = findViewById(R.id.btnOn);
        btnReservation = findViewById(R.id.btnReservation);

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache(true);

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send = deviceData.getMac() + "0";
                sendMqtt(send);
            }
        });


        //사용자가 터치했을 때 image view
        mImageView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ){
                    lcBar.setProgress(100);
                    bitmap = mImageView.getDrawingCache();

                    int pixel = bitmap.getPixel( (int)event.getX(), (int)event.getY() );

                    //RGB값 얻기
                    r = Color.red(pixel);
                    g = Color.green(pixel);
                    b = Color.blue(pixel);

                    //HEX값 얻기
                    String hex = "#"+ Integer.toHexString(pixel);

                    //선택한 색상에 따라 view의 배경색깔 설정
                    mColorView.setBackgroundColor(Color.rgb(r,g,b));
                    //textView에 RGB와 HEX 값을 설정
                    Viewrgb.setText(r +", "+g+", "+ b);

                    String send = deviceData.getMac() + "1" + String.format("%03d", r) + String.format("%03d", g) + String.format("%03d", b);
                    sendMqtt(send);
                }
                return true;
            }
        });

        //화면밝기 코드 >> 추후 무드등 조명 밝기 조절 코드로 수정

        lcBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                val = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setBrightness(val);
            }
        });


        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Con_lithophane.this, TimePicker.class);
                intent.putExtra("data", deviceData);
                startActivity(intent);
            }
        });
    }

    private void setBrightness(int value){
        double val = (double)value/100.0;
        int ar, ag, ab;
        ar = (int)(val * r);
        ag = (int)(val * g);
        ab = (int)(val * b);

        mColorView.setBackgroundColor(Color.rgb(ar, ag, ab));
        Viewrgb.setText(ar +", "+ag+", "+ ab);

        String send = deviceData.getMac() + "1" + String.format("%03d", ar) + String.format("%03d", ag) + String.format("%03d", ab);
        sendMqtt(send);
    }

    void sendMqtt(String data){
        MqttRegister mqttRegister = new MqttRegister();
        mqttRegister.execute(id,data);
    }
}