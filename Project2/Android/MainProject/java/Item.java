package com.iot.project1;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Item {
    public static final int TYPE_LIFE = 0;                  // 생명 아이템
    public static final int TYPE_BONUS = 1;                 // 점수 2배 아이템
    public static final int TYPE_BONUS2 = 2;           // 즉시 50점 추가 아이템
    public static final int TYPE_BOMB = 3;                  // 폭탄 아이템

    private Drawable image;                                 // 이미지
    private Point position;                                 // 좌표
    private Point size;                                     // 크기
    private int type;                                       // 아이템 종류

    public Item(Drawable image, Point position, Point size, int type) {
        this.image = image;
        this.position = position;
        this.size = size;
        this.type = type;
    }

    public Rect getRect() {                                 // 공과 충돌 여부 판정
        return new Rect(position.x, position.y, position.x + size.x, position.y + size.y);
    }

    public void draw(Canvas canvas) {
        image.setBounds(getRect());
        image.draw(canvas);
    }

    public int getType() {
        return type;
    }
}
