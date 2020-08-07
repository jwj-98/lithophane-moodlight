package com.example.nightlight;

        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;

        import android.app.AlarmManager;
        import android.app.DatePickerDialog;
        import android.app.PendingIntent;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.DatePicker;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Locale;

public class TimePicker extends AppCompatActivity {

    MainDeviceData deviceData;
    Intent intent_;
    SharedPreferences shareData;
    String id;

    //알람시간
    private Calendar calendar;
    private android.widget.TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        shareData = getSharedPreferences("shareData", MODE_PRIVATE);
        id = shareData.getString("userId", null);

        intent_ = getIntent();
        deviceData = (MainDeviceData) intent_.getSerializableExtra("data");

        //무드등 기능 보기 설정
        Spinner spinner_function = (Spinner)findViewById(R.id.spinner_function);
        ArrayAdapter adapter_function = ArrayAdapter.createFromResource(this,
                R.array.spinner_function, android.R.layout.simple_spinner_item);
        adapter_function.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_function.setAdapter(adapter_function);

        this.calendar = Calendar.getInstance();
        //현재 날짜 표시
        displayDate();

        this.timePicker = findViewById(R.id.timePicker);

        findViewById(R.id.btnCalender).setOnClickListener(mClickListener);
        findViewById(R.id.btnAlarm).setOnClickListener(mClickListener);
    }
    //날짜표시
    private void displayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ((TextView) findViewById(R.id.txtDate)).setText(format.format(this.calendar.getTime()));
    }

    //DatePickerDialog 호출
    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //알람 날짜 설정
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, dayOfMonth);

                //날짜표시
                displayDate();
            }
        }, this.calendar.get(Calendar.YEAR),this.calendar.get(Calendar.MONTH),this.calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    //알람등록
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm() {
        //알람 시간 설정
        this.calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
        this.calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
        this.calendar.set(Calendar.SECOND, 0);

        //현재일보다 이전이면 등록 실패
        if(this.calendar.before(Calendar.getInstance())){
            Toast.makeText(this, "알람시간이 현재보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        //receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("data", deviceData);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //알람설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.calendar.getTimeInMillis(), pendingIntent);

        // Toast 보여주기 (알람 시간 표시)
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Toast.makeText(this, "Alarm : " + format.format(calendar.getTime()), Toast.LENGTH_LONG).show();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btnCalender:
                    //달력
                    showDatePicker();
                    break;

                case R.id.btnAlarm:
                    //알람등록
                    setAlarm();
                    break;
            }
            finish();
        }
    };
}