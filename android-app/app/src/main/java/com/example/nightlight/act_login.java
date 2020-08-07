package com.example.nightlight;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class act_login extends AppCompatActivity {

    EditText editTextId;
    EditText editTextPw;
    Button btnLogin;
    TextView btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_login);

        //로그인 이력이 있는지 확인합니다. 있다면 자동 로그인 합니다.
        SharedPreferences shareData = getSharedPreferences("shareData", MODE_PRIVATE);
        if(shareData.getBoolean("firstRun", true) == false) {

            String id = shareData.getString("userId", null);
            String pw = shareData.getString("userPw", null);


            try {
                String result;
                LoginRegister login = new LoginRegister();
                result = login.execute(id, pw).get();

                if (result.equals("connected")) {//계정이 일치합니다.

                    Toast.makeText(act_login.this, id + "님의 계정으로 로그인 합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(act_login.this, Main.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();


                }
                else {
                    Toast.makeText(act_login.this, "자동 로그인에 실패했습니다.", Toast.LENGTH_LONG).show();
                }


            }
            catch (Exception e) {
                Log.i("DB", ".......ERROR.......!");
            }
        }

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextPw = (EditText) findViewById(R.id.editTextPw);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (TextView) findViewById(R.id.textViewCreateAccount);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //회원가입버튼 이벤트
                Intent intent = new Intent(act_login.this, act_signup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //로그인버튼 이벤트

                String id = editTextId.getText().toString();
                String pw = editTextPw.getText().toString();

                if(id.length() == 0 || pw.length() == 0) {
                    //아이디와 비밀번호는 필수 입력사항입니다.
                    Toast toast = Toast.makeText(act_login.this, "ID와 비밀번호는 필수 입력사항입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                // 아이디 pw를 체크합니다
                else {
                    try {// login 레지스터 객체 생성
                        String result;
                        LoginRegister login = new LoginRegister();
                        result = login.execute(id, pw).get();

                        if (result.equals("connected")) {//계정이 일치합니다.
                            SharedPreferences shareData = getSharedPreferences("shareData", MODE_PRIVATE);//최초에만 로그인하기때문에 최초실행 확인용 데이터 저장

                            shareData.edit().putBoolean("firstRun", false).apply();
                            shareData.edit().putString("userId", id).apply();
                            shareData.edit().putString("userPw", pw).apply();

                            Toast.makeText(act_login.this, id + "님의 계정으로 로그인 합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(act_login.this, Main.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();



                        }
                        else {
                           Toast.makeText(act_login.this, "ID또는 비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (Exception e) {
                        Log.i("DB", ".......ERROR.......!");
                    }
                }



            }
        });

    }

    @Override //액티비티 종료 애니에이션을 삭제합니다.
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

}
