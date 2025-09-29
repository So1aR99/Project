package com.iot.project1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameOver extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);

        mediaPlayer = MediaPlayer.create(this, R.raw.lastbgm);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.start();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        int score = getIntent().getIntExtra("score", 0);    // 전 GameView에서 전달한 점수 데이터를 Intent를 통해
                                                                            // score라는 키로 값을 꺼낸다.
        String nickname = getIntent().getStringExtra("nickname");

        TextView nametext = findViewById(R.id.nametext);
        nametext.setText("Player : " + nickname);
        TextView scoretext = findViewById(R.id.scoretext);
        scoretext.setText("Score: " + score);                               // 받은 점수를 텍스트 형태로 출력

        Button btn1 = findViewById(R.id.replaybtn);                         // replay버튼을 누르면
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                   // 게임 화면으로 넘어감
                if(mediaPlayer != null) {                                   // 배경 음악 종료
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Intent intent = new Intent(GameOver.this, SubActivity.class);
                intent.putExtra("nickname", nickname);                //
                startActivity(intent);
                finish();
                Log.i("GameOverView", "Replay");                   // 로그캣에 Replay 출력
            }
        });

        Button btn2 = findViewById(R.id.homebtn);                           // 홈 버튼을 누르면
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                   // 메인 화면으로 넘어감
                if(mediaPlayer != null) {                                   // 배경 음악 종료
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                Intent intent = new Intent(GameOver.this, MainActivity.class);
                startActivity(intent);
                finish();
                Log.i("GameOverView", "Go Main");                  // 로그캣에 GO Main 출력
            }
        });
        Toast.makeText(GameOver.this, "게임 종료!!!!!!!!!!!!!!!!", Toast.LENGTH_LONG).show();
    }                                                                       // 토스트 메세지로 게임 종료 알림
}