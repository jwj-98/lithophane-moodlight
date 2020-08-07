package com.example.nightlight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main extends AppCompatActivity {

    private ListView listView;
    private MainDeviceData deviceData;
    private MainAdapter mainAdapter;
    private TextView btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        btnLogout = (TextView) findViewById(R.id.btnLogout);

        SharedPreferences shareData = getSharedPreferences("shareData", MODE_PRIVATE);
        String id = shareData.getString("userId", null);
        String result = "";

        GetDevice getDevice = new GetDevice();
        try {
            result = getDevice.execute(id).get();
            Log.d("tag", result);
        }catch (Exception e){}

        String[] device = result.split(";");
        Log.d("tag", device[0]);
        String[] mac = new String[device.length];
        String[] nick = new String[device.length];
        String[] type = new String[device.length];
        final ArrayList<MainDeviceData> deviceList = new ArrayList<MainDeviceData>();

        for(int i = 0; i < device.length; i++){
            String[] s = device[i].split(",");
            deviceData = new MainDeviceData(s[0], s[1], s[2]);
            deviceList.add(deviceData);
        }

        deviceList.add(new MainDeviceData("", "⊕ 기기 등록", "regist_device"));

        mainAdapter = new MainAdapter(this, deviceList);
        listView.setAdapter(mainAdapter);

        final String lithophane = "lithophane";
        final String regist = "regist";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( deviceList.get(position).getType().contains(lithophane) ){
                    Intent intent = new Intent(Main.this, Con_lithophane.class);
                    //intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("data", deviceList.get(position));
                    startActivity(intent);
                }else if(deviceList.get(position).getType().contains(regist)){

                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shareData = getSharedPreferences("shareData", MODE_PRIVATE);
                shareData.edit().putBoolean("firstRun", true).apply();
                shareData.edit().putString("userId", null).apply();
                shareData.edit().putString("userPw", null).apply();
                Intent intent = new Intent(Main.this, act_login.class);

                startActivity(intent);
                finish();
            }
        });



    }
}