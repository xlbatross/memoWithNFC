package com.project.memowithnfc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.memowithnfc.categorymemoView.CategoryMemoAdapter;
import com.project.memowithnfc.mainCategoryView.MainAdapter;
import com.project.memowithnfc.mainCategoryView.DividerItemDecoration;
import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.vo.Category;
import com.project.memowithnfc.vo.Memo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private RecyclerView cRecyclerView;
    private MainAdapter cAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create_data();
        init_channel();
        init_toolbar();
        init_category();
        init_menu();
    }

    public void create_data() {
        db = new DBHelper(getApplicationContext());

        Category cg = new Category("할일");
        Category cg2 = new Category("카테고리");
        Category cg3 = new Category("테스트");
        db.insertCategory(cg);
        db.insertCategory(cg2);
        db.insertCategory(cg3);

        ArrayList<Category> cLIst = db.getAllCategories();

        db.insertMemo(new Memo("2019-11-14", "14:00", "나의 할일은? 공부하는 것인가? 그래야 하는 것인가?", 1, cLIst.get(0).getId()));
        db.insertMemo(new Memo("2019-11-14", "14:00", "너의 할일은?", 1, cLIst.get(0).getId()));
        db.insertMemo(new Memo("2019-11-14", "15:12", "우리의 할일은?", 1, cLIst.get(0).getId()));
        db.insertMemo(new Memo("2019-10-14", "14:00", "테스트 데이터", 1, cLIst.get(1).getId()));
        db.insertMemo(new Memo("2019-10-15", "14:00", "테스트 데이터", 1, cLIst.get(1).getId()));
        db.insertMemo(new Memo("2019-10-16", "14:00", "테스트 데이터", 1, cLIst.get(1).getId()));
        db.insertMemo(new Memo("2019-10-17", "14:00", "테스트 데이터", 1, cLIst.get(1).getId()));
        db.insertMemo(new Memo("2019-10-18", "14:00", "테스트 데이터", 1, cLIst.get(1).getId()));
        db.insertMemo(new Memo("2019-11-14", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
        db.insertMemo(new Memo("2019-11-15", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
        db.insertMemo(new Memo("2019-11-16", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
        db.insertMemo(new Memo("2019-11-17", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
        db.insertMemo(new Memo("2019-11-18", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
        db.insertMemo(new Memo("2019-11-19", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
        db.insertMemo(new Memo("2019-10-19", "14:00", "테스트 데이터", 1, cLIst.get(2).getId()));
    }

    public void init_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("test", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void init_toolbar() {
        // 툴바 생성 및 설정
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(tb);

        tb.setNavigationIcon(R.drawable.icons8_search_filled_30); // 서치 아이콘으로 네비게이션 아이콘 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀을 생략

        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        Typeface face = ResourcesCompat.getFont(getApplicationContext(), R.font.bmjua); // fontFamily 변경
        tv.setTypeface(face);
        tv.setTextSize(25); // 단위는 sp
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 툴바에 메뉴 버튼을 추가
        getMenuInflater().inflate(R.menu.appbar_main, menu);
        return true;
    }

    //ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);//액티비티 띄우기
                //overridePendingTransition(R.anim.anim_slide_in_top, R.anim.anim_silde_out_bottom); // 전환 애니메이션 삭제
                return true;
            }
            case R.id.setting: {
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void init_category() {
        cRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_in_main);
        cAdapter = new MainAdapter(this, db);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cRecyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

    public void init_menu() {
        LinearLayout add_memo = (LinearLayout) findViewById(R.id.add_memo);
        add_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteMemoActivity.class);
                startActivity(intent);//액티비티 띄우기
            }
        });

        LinearLayout regist_nfc = (LinearLayout) findViewById(R.id.register_nfc);
        regist_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategorySelectActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Category result = (Category) data.getSerializableExtra("result");
                Intent intent = new Intent(getApplicationContext(), NfcCheckActivity.class);
                intent.putExtra("category_name", result.getName());
                startActivity(intent);//액티비티 띄우기
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        cAdapter = new MainAdapter(this, db);
        cRecyclerView.setAdapter(cAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}

