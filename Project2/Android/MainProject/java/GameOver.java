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

import java.util.List;

public class GameOver extends AppCompatActivity {
    private MediaPlayer mediaPlayer;                                        // 배경음악용
    private ScoreManager scoreManager;                                      // 랭킹 관리 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);

        mediaPlayer = MediaPlayer.create(this, R.raw.lastbgm);
        mediaPlayer.setLooping(true);                                       // 자동 반복
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.start();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // ScoreManager 초기화 (랭킹 저장/불러오기 담당)
        scoreManager = new ScoreManager(this);

        int score = getIntent().getIntExtra("score", 0);    // 전 GameView에서 전달한 점수 데이터를 Intent를 통해
        String nickname = getIntent().getStringExtra("nickname");     // score라는 키로 값을 꺼낸다.

        Toast.makeText(GameOver.this, "게임 종료!!!!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();

        TextView nametext = findViewById(R.id.nametext);
        nametext.setText("Player : " + nickname);
        TextView scoretext = findViewById(R.id.scoretext);
        scoretext.setText("Score : " + score);                               // 받은 점수를 텍스트 형태로 출력

        // 현재 플레이어의 점수를 랭킹에 추가 (자동으로 정렬되고 상위 3개만 저장됨)
        scoreManager.addScore(nickname, score);

        // 화면에 랭킹 표시 (1위~3위)
        displayRankings();

        // TOP 3에 진입했는지 확인하고 메시지 출력
        if (scoreManager.isTopScore(score)) {
            Toast.makeText(this, "축하합니다! TOP3에 랭크되었습니다!", Toast.LENGTH_LONG).show();
        }

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
                intent.putExtra("nickname", nickname);                // 닉네임을 다시 전달
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
    }

    // 랭킹을 화면에 표시하는 메서드
    private void displayRankings() {
        TextView rank1 = findViewById(R.id.rank1);                          // XML에 있는 1위 텍스트뷰
        TextView rank2 = findViewById(R.id.rank2);                          // XML에 있는 2위 텍스트뷰
        TextView rank3 = findViewById(R.id.rank3);                          // XML에 있는 3위 텍스트뷰

        // ScoreManager에서 저장된 랭킹 목록 가져오기 (점수 높은 순으로 정렬됨)
        List<ScoreManager.ScoreEntry> rankings = scoreManager.getRankings();

        // 1위 표시 (랭킹 데이터가 1개 이상일 때)
        if (rankings.size() > 0) {
            ScoreManager.ScoreEntry entry = rankings.get(0);                // 첫 번째(0번) = 1위
            rank1.setText("1위 : " + entry.nickname + "  " + entry.score + "점");
        } else {
            rank1.setText("1위 : -");                                        // 데이터 없으면 "-" 표시
        }

        // 2위 표시 (랭킹 데이터가 2개 이상일 때)
        if (rankings.size() > 1) {
            ScoreManager.ScoreEntry entry = rankings.get(1);                // 두 번째(1번) = 2위
            rank2.setText("2위 : " + entry.nickname + "  " + entry.score + "점");
        } else {
            rank2.setText("2위 : -");                                        // 데이터 없으면 "-" 표시
        }

        // 3위 표시 (랭킹 데이터가 3개 이상일 때)
        if (rankings.size() > 2) {
            ScoreManager.ScoreEntry entry = rankings.get(2);                // 세 번째(2번) = 3위
            rank3.setText("3위 : " + entry.nickname + "  " + entry.score + "점");
        } else {
            rank3.setText("3위 : -");                                        // 데이터 없으면 "-" 표시
        }
    }

    @Override
    protected void onDestroy() {                                            // 액티비티가 완전히 종료될 때
        super.onDestroy();
        if (mediaPlayer != null) {                                          // 배경음악 리소스 해제
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
