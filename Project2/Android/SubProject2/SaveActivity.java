package com.iot.project3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SaveActivity extends AppCompatActivity {

    private TextView[] memoTextViews;
    private Button[] deleteButtons;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        memoTextViews = new TextView[] {
                findViewById(R.id.textView2),
                findViewById(R.id.textView3),
                findViewById(R.id.textView4),
                findViewById(R.id.textView5),
                findViewById(R.id.textView6),
                findViewById(R.id.textView7),
        };

        deleteButtons = new Button[] {
                findViewById(R.id.delbtn1),
                findViewById(R.id.delbtn2),
                findViewById(R.id.delbtn3),
                findViewById(R.id.delbtn4),
                findViewById(R.id.delbtn5),
                findViewById(R.id.delbtn6)
        };

        prefs = getSharedPreferences("MyMemo", MODE_PRIVATE);

        loadMemos();

        for (int i = 0; i < deleteButtons.length; i++) {
            final int index = i + 1;
            deleteButtons[i].setOnClickListener(v -> {
                deleteMemo(index);
            });
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaveActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button button1 = findViewById(R.id.memobtn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SaveActivity.this, SubActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    private void loadMemos() {
        int count = prefs.getInt("memo_count", 0);

        for (int i = 0; i < memoTextViews.length; i++) {
            if (i < count) {
                String memo = prefs.getString("memo_" + (i + 1), "비어 있음");
                memoTextViews[i].setText(memo);
                deleteButtons[i].setVisibility(View.VISIBLE);
            } else {
                memoTextViews[i].setText("비어 있음");
                deleteButtons[i].setVisibility(View.GONE);
            }
        }
    }

    private void deleteMemo(int index) {
        SharedPreferences.Editor editor = prefs.edit();

        int count = prefs.getInt("memo_count", 0);

        if (index > count) return;

        editor.remove("memo_" + index);

        for (int i = index + 1; i <= count; i++) {
            String oldKey = "memo_" + i;
            String newKey = "memo_" + (i - 1);
            String value = prefs.getString(oldKey, null);
            if (value != null) {
                editor.putString(newKey, value);
                editor.remove(oldKey);
            }
        }

        editor.putInt("memo_count", count - 1);
        editor.apply();

        loadMemos();
    }
}
