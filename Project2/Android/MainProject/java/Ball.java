package com.iot.project1;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class Ball {
    private Drawable image = null;                  // 공의 이미지 저장
    private Point point = new Point();              // 공의 현재 위치 저장
    private Point size = new Point();               // 공의 크기 저장
    private Point delta;                            // 공의 이동 방향 및 속도 저장
    private boolean stopped = false;                // 멈춤 상태 여부

    public Drawable getImage() {
        return image;
    }

    public Point getPoint() {
        return point;
    }

    public Point getSize() {
        return size;
    }

    public Point getDelta() {
        return delta;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setSize(Point size) {
        this.size = size;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setDelta(int dx, int dy) {
        delta = new Point(dx, dy);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public void draw(Canvas canvas) {               // 공의 이미지를 화면에 그림
        image.setBounds(point.x, point.y, point.x + size.x, point.y + size.y);      // 이미지가 그려질 사각형 영역
        image.draw(canvas);
    }

    public void move(Rect surfaceFrame) {
        if (stopped) return;                        // 멈춘 상태면 이동 안 함

        Random random = new Random();               // 튕기는 방향을 랜덤하게 조정하기 위해 랜덤 사용

        // 좌우 벽 튕기기
        if (point.x + delta.x < 0 || point.x + delta.x + size.x > surfaceFrame.right) {
            delta.x *= -1;                          // 충돌하면 X방향 반전
                                                    // (왼쪽 벽에 충돌하거나 오른쪽 벽에 충돌하면)
            // 불규칙하게 변화 (±0~20)
            int dxJitter = random.nextInt(41) - 20; // -20 ~ +20
            delta.x += dxJitter;
        } else {                                    // 만약 충돌 안하면 정상적으로 x좌표 업데이트(그냥 쭉 이동)
            point.x += delta.x;
        }

        // 위쪽 벽 튕기기
        if (point.y + delta.y < 0) {                // 위쪽 벽에 충돌하면
            delta.y *= -1;                          // 충돌하면 Y반향 반전

            // 불규칙하게 변화
            int dyJitter = random.nextInt(41) - 20; // -20 ~ +20
            delta.y += dyJitter;                    // 랜덤 요소 추가
        } else {                                    // 만약 충돌 안하면 정상적으로 y좌표 업데이트(그냥 쭉 이동)
            point.y += delta.y;
        }

        // 속도 제한 (너무 빨라지지 않도록)
        int maxSpeed = 70;                          // 너무 빨라지는 것을 방지하기 위해 x,y방향 모두 -70 ~ 70사이에 들어오도록 조절
        delta.x = Math.max(-maxSpeed, Math.min(maxSpeed, delta.x));
        delta.y = Math.max(-maxSpeed, Math.min(maxSpeed, delta.y));
    }
}
