package com.project.memowithnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.project.memowithnfc.db.DBHelper;
import com.project.memowithnfc.vo.Category;

import java.nio.charset.Charset;

public class NfcCheckActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private int category_id;
    private Category category;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_check);

        init_data();
    }

    public void init_data() {
        db = new DBHelper(this);
        category_id = getIntent().getIntExtra("category_id", 0);
        category = db.getCategory(category_id);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected  void onPause() {
        if(nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag != null) {
            NdefMessage ndefMessage = new NdefMessage
                    (new NdefRecord[]{
                            createMimeRecord("text/plain", String.valueOf(category.getId()).getBytes())
                            , NdefRecord.createApplicationRecord("com.project.memowithnfc")
                    });

            writeTag(tag, ndefMessage);
        }
    }

    public void writeTag(Tag tag, NdefMessage message)  {
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    // Let's try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                    }
                }
                else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
                finish();
                Toast.makeText(this, "NFC 등록 성공!", Toast.LENGTH_LONG).show();
            }
            catch(Exception e) {
                e.printStackTrace();
                finish();
                Toast.makeText(this, "NFC 등록 실패", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 커스텀 MIME 타입을 캡슐화한 NDEF 레코드를 생성한다.
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload){
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
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

// NFC 고유 아이디 추출용 함수. 지금은 사용하지 않음
    /*
    public static final String CHARS = "0123456789ABCDEF";

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<data.length; ++i) {
            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F))
                    .append(CHARS.charAt(data[i] & 0x0F));
        }
        return sb.toString();
    } */
