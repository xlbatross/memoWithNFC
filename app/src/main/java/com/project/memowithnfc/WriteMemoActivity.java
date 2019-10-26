package com.project.memowithnfc;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.vo.Category;
import com.project.memowithnfc.vo.Memo;

import java.util.Calendar;

public class WriteMemoActivity extends AppCompatActivity {

    private DBHelper db;
    private Memo memo;
    private TextView category_select;
    private TextView date;
    private TextView time;
    private Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_memo);

        init_data();
        init_toolbar();
        init_category_select();
        init_dateTime();
        init_content();
    }

    public void init_data() {
        db = new DBHelper(this);
        memo = new Memo();
        memo.setCategory_id(0);
    }

    public void init_toolbar() {
        // 툴바 생성 및 설정
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(tb);

        tb.setNavigationIcon(R.drawable.icons8_left_32);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀을 생략

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        tv.setText(R.string.write_memo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 툴바에 메뉴 버튼을 추가
        getMenuInflater().inflate(R.menu.appbar_add, menu);
        return true;
    }

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.memo_add: {
                check_memo();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void check_memo() {
        ToggleButton alarm = findViewById(R.id.alarm_setting);

        if(alarm.isChecked())
            memo.setAlarmSetting(111);
        else
            memo.setAlarmSetting(0);

        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0,0);

        long k = 0; // 푸시 알람용
        if(memo.getCategory_id() == 0)
            toast.setText("카테고리가 선택되지 않았습니다.");
        else if(memo.getDate() == null)
            toast.setText("날짜가 선택되지 않았습니다.");
        else if(memo.getTime() == null)
            toast.setText("시간이 선택되지 않았습니다.");
        else if(memo.getContent() == null)
            toast.setText("메모가 작성되지 않았습니다.");
        else if(memo.getContent().indexOf(" ") == 0 || memo.getContent().indexOf("\n") == 0)
            toast.setText("공백문자나 개행이 먼저 올 수 없습니다.");
        else if((k = db.insertMemo(memo)) > 0) {
            toast.setText("메모 작성 완료!");

            if(memo.getAlarmSetting()>0)
                initAlarmManager();

            finish();
        }
        else
            toast.setText("메모 작성에 실패했습니다. 다시 시도해주세요.");
        toast.show();
    }


    ////////////////////////////////////////////// hasukjun

    // 메모를 처음 등록할떄 알고리즘
    //
    private void initAlarmManager(){
        // 메모 전체화면 열어주기 뷰홀더를 이용하여 메모 아이디를 넘기고 있음
        // 메모 아이디만 넘겨주면 전체화면을 열수 있다.
        // 메모 아이디를 리시버에 넘겨준다.
        // 페딩 인텐트에 메모 아이드를 보내 줄 수 있도록한다.
        // 현재 여기서 클릭될 메모 아이디를 받아올수 있는가

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("test3", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MyBroadcastReceiver.class);//  인텐트가 어디로 갈지 적어줘야 한다.

        // 인텐트로 전달할 값
        intent.putExtra("memo_id", memo.getId());
        intent.putExtra("category_name", memo.getCategory_name());
        intent.putExtra("content", memo.getContent());

        PendingIntent m_pendingIntent = PendingIntent.getBroadcast(this, memo.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // define context and intent for receiver broadcast  나중에 알람을 찾기 위해 메모 아이디로 알람을 서비스에 등록한다.

        // set alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);// get alarm service
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), m_pendingIntent); // set alarm
    }

    ////////////////////////////////////////////// hasukjun

    public void init_category_select() {
        category_select = (TextView) findViewById(R.id.category_selector);

        category_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WriteMemoActivity.this, CategorySelectActivity.class);
                startActivityForResult(intent,2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                Category result = (Category) data.getSerializableExtra("result");
                category_select.setText(result.getName());
                category_select.setTextColor(Color.BLACK);
                memo.setCategory_name(result.getName());
                memo.setCategory_id(result.getId());
            }
        }
    }

    public void init_dateTime() {
        date = (TextView) findViewById(R.id.date_selector);
        time = (TextView) findViewById(R.id.time_selector);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker_on();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepicker_on();
            }
        });
    }

    public void datepicker_on() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String monthofyears = (monthOfYear + 1 >= 10 ? String.valueOf(monthOfYear + 1) : "0" + String.valueOf(monthOfYear + 1));
                String dayofmonths = (dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth));
                date.setText(year + "-" + monthofyears + "-" + dayofmonths);
                date.setTextColor(Color.BLACK);
                memo.setDate(date.getText().toString());
            }
        };

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void timepicker_on() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                String hourofdays = (hourOfDay >= 10 ? String.valueOf(hourOfDay) : "0" + hourOfDay);
                String minutes = (minute >= 10 ? String.valueOf(minute) : "0" + minute);
                time.setText(hourofdays + ":" + minutes);
                time.setTextColor(Color.BLACK);
                memo.setTime(time.getText().toString());
            }
        };
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void init_content() {
        EditText content = findViewById(R.id.content_in_write_memo);

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                memo.setContent(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ConstraintLayout cl = findViewById(R.id.write_memo_container);
        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(cl, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 원하는 동작
                        content.clearFocus();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        content.setFocusable(true);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}