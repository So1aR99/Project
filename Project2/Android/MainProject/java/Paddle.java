package com.iot.project1;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Paddle {
    private Drawable image = null;                          // 패들 이미지 저장
    private Point point = new Point();                      // 패들 현재 위치 저장
    private Point size = new Point();                       // 패들 크기 저장

    public Drawable getImage() { return image; }
    public Point getPoint() { return point; }
    public Point getSize() { return size; }

    public void setImage(Drawable image) { this.image = image; }
    public void setSize(Point size) { this.size = size; }
    public void setPoint(Point point) { this.point = point; }

    public void draw(Canvas canvas) {
        if (image != null) {                // 이미지가 있으면 현재 위치와 크기에 맞게 그림
            image.setBounds(point.x, point.y, point.x + size.x, point.y + size.y);
            image.draw(canvas);
        }
    }

    // 패들의 Rect를 반환하는 메서드 (충돌 감지용)
    public Rect getRect() {                 // 현재 패들의 위치와 크기를 기준으로 객체를 반환, 공이 패들에 닿았는지 확인
        return new Rect(point.x, point.y, point.x + size.x, point.y + size.y);
    }
}