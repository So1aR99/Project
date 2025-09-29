package com.iot.project1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.mainbgm);
        mediaPlayer.setLooping(true);                                        // 자동 반복
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.start();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        EditText nicknameText = findViewById(R.id.nicknameText);             // nicknameText인 EditText를 찾아 연결
        Button Playbtn = findViewById(R.id.playbtn);                         // playbtn인 Button을 찾아 연결
        Playbtn.setOnClickListener(new View.OnClickListener() {              // 첫 화면에 있는 PLAY버튼을 누르면 다음 화면인 로딩화면으로
            @Override
            public void onClick(View v) {
                String nickname = nicknameText.getText().toString();        // EditText에 입력된 값을 String으로 변환해 nickname에 저장

                if(nickname.isEmpty()) {                                    // 입력된 값이 비어있는지 확인한 후
                    Toast.makeText(MainActivity.this, "닉네임을 입력하세요!!!!", Toast.LENGTH_SHORT).show();
                    return;                                                 // 비어있으면 토스트 메세지로 입력 메시지 출력
                }
                if (mediaPlayer != null) {                                  // 다음 화면으로 넘어가기 전에 음악 정리
                    mediaPlayer.stop();                                     // 음악 중지
                    mediaPlayer.release();                                  // 리소스 해제
                    mediaPlayer = null;
                }
                Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                intent.putExtra("nickname", nickname);                // nickname에 저장된 값을 nickname이라는 키로 넘김
                startActivity(intent);                                      // intent를 실행해 다음 화면으로 전환
            }
        });
        Button Rulesbtn = findViewById(R.id.rulesbtn);
        Rulesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {                                  // 룰 화면으로 넘어가면 배경음악 멈춤
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                Intent intent = new Intent(MainActivity.this, RulesActivity.class);
                startActivity(intent);
            }
        });
    }
}
