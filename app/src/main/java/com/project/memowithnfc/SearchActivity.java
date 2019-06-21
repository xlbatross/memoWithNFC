package com.project.memowithnfc;

import android.app.Service;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.searchMemoView.SearchMemoAdapter;
import com.project.memowithnfc.vo.Memo;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private DBHelper db;
    private ArrayList<Memo> sList;
    private SearchMemoAdapter sAdapter;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        set_data();
        init_RecylerVIew();
        init_search();
        init_back();
    }

    public void set_data() {
        // MainActivity에서 RecyclerView의 데이터에 접근시 사용됩니다.
        db = new DBHelper(getApplicationContext());
        sList = new ArrayList<>();
    }

    public void init_RecylerVIew() {
        RecyclerView sRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search_result);
        LinearLayoutManager sLinearLayoutManager = new LinearLayoutManager(this);
        sRecyclerView.setLayoutManager(sLinearLayoutManager);

        //RecyclerView의 줄(row) 사이에 수평선을 넣기위해 사용됩니다.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(sRecyclerView.getContext(),
                sLinearLayoutManager.getOrientation());
        sRecyclerView.addItemDecoration(dividerItemDecoration);

        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        sAdapter = new SearchMemoAdapter(this, sList);
        sRecyclerView.setAdapter(sAdapter);
    }

    public void init_search() {
        et = (EditText) findViewById(R.id.editText);
        et.setFocusableInTouchMode(true);
        et.requestFocus();

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                sList.clear();
                if(s.length() != 0) {
                    sList.addAll(db.getAllMemosByContent(String.valueOf(s)));
                }
                sAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });

        ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.search_container);

        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(cl, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 원하는 동작
                        et.clearFocus();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    public void init_back() {
        ImageButton back = (ImageButton) findViewById(R.id.imageButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 검색 기능을 종료하고 다시 이전 액티비티로 넘어간다.
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //overridePendingTransition(0, 0); // 전환 애니메이션 삭제
    }

    @Override
    public void onResume() {
        super.onResume();
        sList.clear();
        if(et.getText().toString().length() != 0) {
            sList.addAll(db.getAllMemosByContent(String.valueOf(et.getText().toString())));
        }
        sAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDB();
        Debug.stopMethodTracing();
    }
}

