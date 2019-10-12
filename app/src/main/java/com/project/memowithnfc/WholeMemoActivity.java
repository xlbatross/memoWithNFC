package com.project.memowithnfc;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
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
import android.widget.Button;
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

public class WholeMemoActivity extends AppCompatActivity {

    private int memo_id;
    private DBHelper db;
    private Memo ex_memo;
    private Memo new_memo;
    private boolean change;

    private TextView category_select;
    private TextView date;
    private TextView time;
    private ToggleButton alarmSetting;
    private EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_memo);

        init_data();
        init_toolbar();
        init_category_select();
        init_dateTime();
        init_content();
        init_delete();
    }

    public void init_data() {
        memo_id = getIntent().getIntExtra("memo_id", 1);

        db = new DBHelper(this);
        ex_memo = db.getMemo(memo_id);
        new_memo = db.getMemo(memo_id);
        change = false;

        category_select = (TextView) findViewById(R.id.category_selector_whole_memo);
        date = (TextView) findViewById(R.id.date_selector_whole_memo);
        time = (TextView) findViewById(R.id.time_selector_whole_memo);
        alarmSetting = (ToggleButton) findViewById(R.id.alarm_setting_whole_memo);
        content = (EditText) findViewById(R.id.content_text_whole_memo);

        category_select.setText(ex_memo.getCategory_name());
        date.setText(ex_memo.getDate());
        time.setText(ex_memo.getTime());
        alarmSetting.setChecked(ex_memo.getAlarmSetting() == 111);
        content.setText(ex_memo.getContent());

        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.whole_memo_container);
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

    public void init_toolbar() {
        // 툴바 생성 및 설정
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_whole_memo) ;
        setSupportActionBar(tb);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀을 생략
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
                //overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_silde_out_bottom); // 전환 애니메이션 삭제
                return true;
            }
            case R.id.memo_add: {
                if(alarmSetting.isChecked())
                    new_memo.setAlarmSetting(111);
                else
                    new_memo.setAlarmSetting(0);

                if(new_memo.getAlarmSetting() != ex_memo.getAlarmSetting())
                    change = true;

                Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0,0);
                if(change) {
                    if(new_memo.getContent() == null)
                        toast.setText("메모 내용이 없습니다.");
                    else if(new_memo.getContent().indexOf(" ") == 0 || new_memo.getContent().indexOf("\n") == 0)
                        toast.setText("메모 내용 수정 시 공백문자나 개행이 먼저 올 수 없습니다.");
                    else if(db.updateMemo(new_memo) > 0) {
                        toast.setText("메모 수정 완료!");

                        Intent intent = new Intent();
                        intent.putExtra("change_db", 1);

                        finish();
                    }
                    else
                        toast.setText("메모 수정에 실패했습니다. 다시 시도해주세요.");
                }
                else
                    toast.setText("메모가 변경되지 않았습니다.");
                toast.show();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void init_category_select() {
        category_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WholeMemoActivity.this, CategorySelectActivity.class);
                startActivityForResult(intent,3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3) {
            if(resultCode == RESULT_OK) {
                Category result = (Category) data.getSerializableExtra("result");
                category_select.setText(result.getName());
                category_select.setTextColor(Color.BLACK);
                new_memo.setCategory_id(result.getId());

                if(new_memo.getCategory_id() != ex_memo.getCategory_id())
                    change = true;
            }
        }
    }

    public void init_dateTime() {
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
                String monthofyears = (monthOfYear + 1 >= 10 ? String.valueOf(monthOfYear + 1) : "0" + String.valueOf(monthOfYear + 1));
                String dayofmonths = (dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0" + String.valueOf(dayOfMonth));
                date.setText(String.valueOf(year) + "-" + monthofyears + "-" + dayofmonths);
                date.setTextColor(Color.BLACK);
                new_memo.setDate(date.getText().toString());

                if(!new_memo.getDate().equals(ex_memo.getDate()))
                    change = true;
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
                String hourofdays = (hourOfDay >= 10 ? String.valueOf(hourOfDay) : "0" + String.valueOf(hourOfDay));
                String minutes = (minute >= 10 ? String.valueOf(minute) : "0" + String.valueOf(minute));
                time.setText(hourofdays + ":" + minutes);
                time.setTextColor(Color.BLACK);
                new_memo.setTime(time.getText().toString());

                if(!new_memo.getTime().equals(ex_memo.getTime()))
                    change = true;
            }
        };

        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(this, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void init_content() {
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new_memo.setContent(String.valueOf(s));
                if(!new_memo.getContent().equals(ex_memo.getContent()))
                    change = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void init_delete() {
        Button delete = (Button) findViewById(R.id.whole_memo_delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_dialog(v.getContext());
            }
        });
    }

    public void delete_dialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("메모 삭제"); // 제목 셋팅

        alertDialogBuilder // AlertDialog 셋팅
                .setMessage("메모를 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                db.deleteMemo(memo_id);
                                finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}
