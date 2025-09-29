package com.iot.project1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class LoadingActivity extends AppCompatActivity {
    public static final long DELAY_MS = 100;                        // 프로세스바 업데이트 간격(0.1초)
    public static final int WAHT_PROGRESS = 1;                      // 메시지 식별자
    private ProgressBar progressBar;
    private TextView textView;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(progressBar.getProgress() < 100) {                                   // 바의 현재 진행도가 100 미만이면 1씩 증가
                progressBar.setProgress(progressBar.getProgress() + 1);
                textView.setText("LOADING: " + progressBar.getProgress() + "%");    // 텍스트뷰에 현재 진행률 %표시
                // DELAY_MS초 이후에 WHAT_PROGRESS 메시지를 또 보낸다.
                sendEmptyMessageDelayed(WAHT_PROGRESS, DELAY_MS);                   // 메시지 처리후, DELAY_MS만큼 지연을 주고
            }                                                                       // 다시 WHAT_PROGRESS 메시지를 보냄
            if(progressBar.getProgress() == 100) {                                  // 바가 100%에 도달하면
                removeMessages(WAHT_PROGRESS);                                      // 남은 메시지 모두 제거하고
                String nickname = getIntent().getStringExtra("nickname");     // Main액티비티에서 넘긴 nickname값을 가져옴
                Intent intent = new Intent(LoadingActivity.this, SubActivity.class);
                intent.putExtra("nickname", nickname);                        // 저장된 nickname값을 다시
                startActivity(intent);                                              // nickname이라는 키로 넘김
                finish();                                                           // 다음 화면으로 전환하고 종료
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        handler.sendEmptyMessageDelayed(WAHT_PROGRESS, DELAY_MS);   // 최초의 메시지 예약 (무조건 최초로 한번 실행)

        Log.i("LoadingView", "Loading now!!");             // 로그캣에 메시지 출력

        Toast.makeText(LoadingActivity.this, "로딩중 입니다. 잠시만 기다려 주세요.", Toast.LENGTH_LONG).show();
                                                                    // 토스트 메세지로 로딩중임을 출력
    }
}