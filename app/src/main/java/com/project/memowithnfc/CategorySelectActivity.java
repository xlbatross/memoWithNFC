package com.project.memowithnfc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.memowithnfc.categoryselectListView.CategoryListAdapter;
import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.vo.Category;

import java.util.ArrayList;

public class CategorySelectActivity extends AppCompatActivity {

    private DBHelper db;
    private ArrayList<Category> mList;
    private CategoryListAdapter cAdapter;
    private Category _new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category_select);

        init_data();
        init_category();
        init_add_category();
        init_cheakbar();
    }

    public void init_data() {
        db = new DBHelper(getApplicationContext());
        mList = db.getAllCategories();
        _new = new Category();
    }

    public void init_category() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.category_list);
        LinearLayoutManager sLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(sLinearLayoutManager);

        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        cAdapter = new CategoryListAdapter(this, mList);
        mRecyclerView.setAdapter(cAdapter);
    }

    public void init_add_category() {
        EditText new_category = (EditText) findViewById(R.id.add_category_name);
        Button add = (Button) findViewById(R.id.add_category_button);

        new_category.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    _new.setName(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0,0);

                if(_new.getName() == null)
                    toast.setText("카테고리 이름이 작성되지 않았습니다.");
                else if(_new.getName().indexOf(" ") == 0)
                    toast.setText("공백문자가 먼저 올 수 없습니다.");
                else if(_new.getName().indexOf("\n") != -1)
                    toast.setText("개행문자는 들어갈 수 없습니다.");
                else if(db.insertCategory(_new) > 0)
                {
                    mList.add(db.getCategoryByName(_new.getName()));
                    toast.setText("카테고리 추가 완료!");
                    cAdapter.notifyDataSetChanged();
                }
                else
                    toast.setText("카테고리 추가에 실패했습니다. 다시 시도해주세요.");
                toast.show();
            }
        });
    }

    public void init_cheakbar() {
        Button ok = (Button) findViewById(R.id.category_select_check);
        Button cancel = (Button) findViewById(R.id.category_select_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cAdapter.getSelectedItem() != -1) {
                    Category selected = new Category();
                    selected.setName(mList.get(cAdapter.getSelectedItem()).getName());
                    selected.setId(mList.get(cAdapter.getSelectedItem()).getId());

                    Intent intent = new Intent();
                    intent.putExtra("result", selected);
                    setResult(RESULT_OK, intent);

                    //액티비티(팝업) 닫기
                    finish();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "카테고리가 선택되지 않았습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0,0);
                    toast.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 종료
            }
        });
    }

    // 안드로이드 백버튼 막기;
    @Override
    public void onBackPressed() {
        return;
    }

    //바깥레이어 클릭시 안닫히게
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}

// 키보드 자동 올리기
//InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

//키보드 내리기
//InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
