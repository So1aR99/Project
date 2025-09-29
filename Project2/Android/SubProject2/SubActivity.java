package com.iot.project3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SubActivity extends AppCompatActivity {
    public static final String TAG_MSG = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        EditText editText = findViewById(R.id.editText);
        Button saveBtn = findViewById(R.id.savebtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo = editText.getText().toString();

                // SharedPreferences에 메모 저장
                SharedPreferences prefs = getSharedPreferences("MyMemo", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // 저장된 메모 개수 불러오기 (기본 0)
                int count = prefs.getInt("memo_count", 0);

                // 다음 저장 인덱스 계산
                int nextIndex = count + 1;

                // key 예: memo_1, memo_2, ...
                String key = "memo_" + nextIndex;

                // 새 메모 저장
                editor.putString(key, memo);

                // 메모 개수 업데이트
                editor.putInt("memo_count", nextIndex);

                editor.apply();

                Toast.makeText(SubActivity.this, "메모가 저장되었습니다!", Toast.LENGTH_SHORT).show();

                finish();

                Intent intent = new Intent(SubActivity.this, SaveActivity.class);
                startActivity(intent);


            }
        });
    }
}