package com.example.nightlight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class act_signup extends AppCompatActivity {

    EditText editTextId;
    EditText editTextPw;
    EditText editTextCheckPw;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_signup);

        editTextId = (EditText) findViewById(R.id.editTextCreateId);
        editTextPw = (EditText) findViewById(R.id.editTextCreatePw);
        editTextCheckPw = (EditText) findViewById(R.id.editTextCheckPw);
        btnSignup = (Button) findViewById(R.id.btnSingup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextId.getText().toString();
                String pw = editTextPw.getText().toString();
                String checkPw = editTextCheckPw.getText().toString();

                if(pw.equals(checkPw)){
                    try {
                        String result;
                        SignupRegister sign = new SignupRegister();
                        result = sign.execute(id, pw).get();
                        Toast.makeText(act_signup.this, result, Toast.LENGTH_SHORT).show();
                        if (result.equals("회원 가입이 완료되었습니다 !")) {
                            finish();
                        }
                    }
                    catch (Exception e) {
                        Log.i("DB", ".......ERROR.......!");
                    }
                }
                else{
                    Toast.makeText(act_signup.this, "잘못입력된 값이 있습니다.", Toast.LENGTH_SHORT).show();
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
