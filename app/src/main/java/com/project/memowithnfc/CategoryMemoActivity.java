package com.project.memowithnfc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.memowithnfc.categorymemoView.CategoryMemoAdapter;
import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.vo.Category;

import org.w3c.dom.Text;

public class CategoryMemoActivity extends AppCompatActivity {

    private int category_id;
    private DBHelper db;
    private RecyclerView nRecyclerView;
    private RecyclerView pRecyclerView;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_memo);

        init_id();
        init_toolbar();
        init_previous_memo_button();
        init_menu();
    }

    public void init_id() {
        category_id = getIntent().getIntExtra("category_id", 0);
        db = new DBHelper(this);

        title = (TextView) findViewById(R.id.toolbar_title);

        nRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_next_memo);
        nRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        pRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_previous_memo);
        pRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void init_toolbar() {
        // 툴바 생성 및 설정
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(tb);

        tb.setNavigationIcon(R.drawable.icons8_left_32);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀을 생략
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 툴바에 메뉴 버튼을 추가
        getMenuInflater().inflate(R.menu.appbar_overflow, menu);
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
            case R.id.menu_category_name_change: {
                change_category_name();
                return true;
            }
            case R.id.menu_category_delete: {
                delete_category();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void change_category_name() {
        final EditText et = new EditText(CategoryMemoActivity.this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryMemoActivity.this);

        alertDialogBuilder
                .setTitle("카테고리 이름 변경")
                .setMessage("변경할 이름을 적어주세요.")
                .setCancelable(false)
                .setView(et)
                .setPositiveButton("변경",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Category ct = new Category();
                                ct.setId(category_id);
                                ct.setName(et.getText().toString());

                                if(db.updateCategory(ct) > 0) {
                                    title.setText(db.getCategory(category_id).getName());
                                    Toast.makeText(getApplicationContext(), "변경 완료", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "변경 실패", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void delete_category() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryMemoActivity.this);

        alertDialogBuilder
                .setTitle("카테고리 삭제")
                .setMessage("카테고리를 삭제하시겠습니까? 카테고리에 해당하는 메모는 동시에 삭제됩니다.")
                .setCancelable(false)
                .setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                real_Delete_category();
                            }
                        })
                .setNegativeButton(R.string.KoreanCancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void real_Delete_category() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryMemoActivity.this);

        alertDialogBuilder
                .setTitle("최종 확인")
                .setMessage("정말로 카테고리를 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.deleteCategory(category_id);
                                finish();
                            }
                        })
                .setNegativeButton(R.string.KoreanCancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void init_previous_memo_button() {
        TextView previous_text = (TextView) findViewById(R.id.previous_memo_button);
        LinearLayout bottom_sheet = (LinearLayout) findViewById(R.id.previous_memo_container);
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(bottom_sheet);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        previous_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    public void init_menu() {
        Button add_memo = (Button) findViewById(R.id.add_memo);
        add_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteMemoActivity.class);
                startActivity(intent);//액티비티 띄우기
            }
        });

        Button regist_nfc = (Button) findViewById(R.id.register_nfc);
        regist_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NfcCheckActivity.class);
                intent.putExtra("category_name", title.getText());
                startActivity(intent);//액티비티 띄우기
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
            processIntent(getIntent());

        if(category_id != 0) {
            title.setText(db.getCategory(category_id).getName());
            nRecyclerView.setAdapter(new CategoryMemoAdapter(this, db.getNextMemosByCategory(category_id)));
            pRecyclerView.setAdapter(new CategoryMemoAdapter(this, db.getPreviousMemosByCategory(category_id)));
        }

        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            Toast.makeText(getApplicationContext(), "NFC에 저장된 카테고리는 존재하지 않습니다.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void processIntent(Intent intent){
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 은 MIME type을 포함하고, 만약 주석 처리를 제거하고 AAR을 설정했으면 record 1 은 AAR 이다.
        Category ct =  db.getCategoryByName(new String(msg.getRecords()[0].getPayload()));
        category_id = (ct.getId() > 0 ? ct.getId() : 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}
