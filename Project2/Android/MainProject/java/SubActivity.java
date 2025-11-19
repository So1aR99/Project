package com.iot.project1;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        String nickname = getIntent().getStringExtra("nickname");         // 로딩 액티비티에서 보낸 nickname값을 가져옴
        setContentView(new GameView(this, nickname));                    // GameView라는 커스텀 뷰 클래스를 화면에 직접 붙임
        ActionBar actionBar = getSupportActionBar();                            // XML대신 코드로 뷰를 생성해 화면에 보여줌
        actionBar.hide();

        Toast.makeText(SubActivity.this, "게임 시작!!", Toast.LENGTH_SHORT).show();
                                                                                // 토스트 메세지로 게임 시작 알림
    }
}