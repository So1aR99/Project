package com.iot.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameView.class.getName();                 // 로그캣 출력 시 사용할 태그 이름
    private final SurfaceHolder holder;
    private Ball ball;
    private Paddle paddle;
    private Thread renderer;
    private volatile boolean running = false;                                   // 게임 작동 상태
    private boolean gameOver = false;                                           // 게임 종료 상태
    private boolean gameOverHandled = false;                                    // 중복 실행 방지
    private Context context;
    private int score = 0;                                                      // 현재 점수 저장
    private String nickname;                                                    // 닉네임
    private MediaPlayer mediaPlayer;                                            // 배경음악용
    private SoundPool soundPool;                                                // 효과음
    private int bounceSoundId;                                                  // 공 튀기기 효과음
    private int gameOverSoundId;                                                // 게임 종료 효과음
    private int itemSoundId;                                                    // 아이템 효과음 (폭탄 제외)
    private int bombsoundId;                                                    // 폭탄 효과음
    private boolean soundLoaded = false;                                        // 이전에 있던 사운드 로드 완료 변수
    private int life = 3;                                                       // 처음 목숨 3개
    private Drawable heartDrawable;                                             // 목숨(하트) 이미지
    private final List<Item> items = new ArrayList<>();
    private long lastItemSpawnTime = 0;                                         // 아이템 마지막 생성 시간
    // 보너스 아이템 관련 변수
    private long lastBonusSpawnTime = 0;                                        // 보너스 아이템 마지막 생성 시간
    private long bonusItemSpawnedTime = 0;                                      // 보너스 아이템 생성 시간
    private boolean isBonusActive = false;
    private long bonusActivatedTime = 0;
    private long lastInstantScoreSpawnTime = 0;
    private boolean instantScoreItemActive = false;                             // 현재 50점 아이템이 화면에 있는지
    private long instantScoreItemSpawnedTime = 0;                               // 아이템 생성 시간 저장
    private long lastBombSpawnTime = 0;                                         // 마지막 폭탄 생성 시간
    private long bombItemSpawnedTime = 0;                                       // 폭탄 아이템 생성 시간 저장

    public GameView(Context context, String nickname) {                         // context와 nickname을 받아 초기화
        super(context);
        this.context = context;
        this.nickname = nickname;
        holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);                                                     // 터치 이벤트를 받을 수 있게 활성화
        setFocusableInTouchMode(true);
    }
    private void spawnLifeItem() {
        Drawable itemDrawable = getResources().getDrawable(R.drawable.heart, null);
        int size = 80;                                                          // 하트 크기
        int maxX = holder.getSurfaceFrame().right - size;                       // 생성위치의 최대값
        int maxY = holder.getSurfaceFrame().bottom - size - 200;

        int x = (int)(Math.random() * maxX);                                    // 무작위 위치
        int y = (int)(Math.random() * maxY);                                    // 무작위 위치

        Item lifeItem = new Item(itemDrawable, new Point(x, y), new Point(size, size), Item.TYPE_LIFE);
        items.add(lifeItem);                                                    // 아이템을 무작위 위치에 생성해 리스트에 추가
    }

    private void spawnBonusItem() {
        Drawable itemDrawable = getResources().getDrawable(R.drawable.bonus, null);
        int size = 150;                                                         // 보너스 아이템 크기
        int maxX = holder.getSurfaceFrame().right - size;
        int maxY = holder.getSurfaceFrame().bottom - size - 200;

        int x = (int)(Math.random() * maxX);
        int y = (int)(Math.random() * maxY);

        Item bonusItem = new Item(itemDrawable, new Point(x, y), new Point(size, size), Item.TYPE_BONUS);
        items.add(bonusItem);
    }
    private void spawnInstantScoreItem() {
        Drawable itemDrawable = getResources().getDrawable(R.drawable.bonus_score, null);
        int size = 50;                                                          // 보너스2 아이템 크기
        int maxX = holder.getSurfaceFrame().right - size;
        int maxY = holder.getSurfaceFrame().bottom - size - 200;

        int x = (int)(Math.random() * maxX);
        int y = (int)(Math.random() * maxY);

        Item instantScoreItem = new Item(itemDrawable, new Point(x, y), new Point(size, size), Item.TYPE_BONUS2);
        items.add(instantScoreItem);
    }

    private void spawnBombItem() {
        Drawable itemDrawable = getResources().getDrawable(R.drawable.bomb, null);
        int size = 100;                                                          // 폭탄 크기
        int maxX = holder.getSurfaceFrame().right - size;
        int maxY = holder.getSurfaceFrame().bottom - size - 200;

        int x = (int)(Math.random() * maxX);
        int y = (int)(Math.random() * maxY);

        Item bombItem = new Item(itemDrawable, new Point(x, y), new Point(size, size), Item.TYPE_BOMB);
        items.add(bombItem);
    }
    private Rect ballRect() {                                                    // 공의 위치와 크기를 바탕으로 충돌 감지용 사각형 생성
        Point p = ball.getPoint();
        Point s = ball.getSize();
        return new Rect(p.x, p.y, p.x + s.x, p.y + s.y);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        ball = new Ball();                                                      // 공 생성
        ball.setImage(getResources().getDrawable(R.drawable.zzangball, null));
        ball.setSize(new Point(100, 100));
        ball.setPoint(new Point(0, 0));
        ball.setDelta(30, 60);                                           // 공의 크기, 초기 위치, 움직임 방향 설정

        paddle = new Paddle();                                                  // 패들 생성
        paddle.setImage(getResources().getDrawable(R.drawable.paddle, null));
        paddle.setSize(new Point(200, 40));                               // 패들 크기, 초기 위치, 움직임 방향 설정
        paddle.setPoint(new Point(
                (holder.getSurfaceFrame().right - 200) / 2,
                holder.getSurfaceFrame().bottom - 150));

        running = true;                                                         // 게임 상태 및 점수 초기화
        gameOver = false;
        gameOverHandled = false;
        score = 0;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)                                               // 동시에 몇 개까지 재생할 수 있는지 (필요시 5~10도 가능)
                .setAudioAttributes(audioAttributes)
                .build();

        mediaPlayer = MediaPlayer.create(context, R.raw.gamebgm);
        mediaPlayer.setLooping(true);                                           // 음악이 종료되면 다시 처음부터 재생
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.start();

        heartDrawable = getResources().getDrawable(R.drawable.life, null);

        // 사운드 리소스 미리 로딩
        bounceSoundId = soundPool.load(context, R.raw.ballsound, 1);
        gameOverSoundId = soundPool.load(context, R.raw.gameover, 1);
        itemSoundId = soundPool.load(context, R.raw.itemsound, 1);
        bombsoundId = soundPool.load(context, R.raw.bombsound, 1);

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                if (sampleId == bounceSoundId || sampleId == gameOverSoundId) {
                    soundLoaded = true;
                }
            }
        });
        // 게임 루프(람다식으로 새 스레드 생성)
        renderer = new Thread(() -> {
            Drawable background = getResources().getDrawable(R.drawable.background, null);
            background.setBounds(holder.getSurfaceFrame());                     // 배경 설정

            while (running) {                                                   // 실행 중이면 계속 반복
                // 충돌 감지
                Rect paddleRect = paddle.getRect();
                Rect expandedPaddleRect = new Rect(                             // 패들 충돌 영역
                        paddleRect.left,
                        paddleRect.top - 20,                                // 충돌 영역을 위쪽으로 20만큼 확장해 충돌 감지
                        paddleRect.right,
                        paddleRect.bottom
                );

                if (Rect.intersects(ballRect(), expandedPaddleRect)) {          // 공과 패들이 겹치고
                    Point delta = ball.getDelta();                              // 공의 아래로 움직이는 상태이면

                    if (delta.y > 0) {                                          // 공의 Y방향 속도를 반대로 바꿔 튕김
                        soundPool.play(bounceSoundId, 1, 1, 1, 0, 1);
                        // 가져온 사운드ID, 왼쪽볼륨, 오른쪽볼룸, 우선순위, 반복, 재생속도

                        int newX = (int)(delta.x * 1.1);
                        int newY = (int)(-Math.abs(delta.y) * 1.1);             // 충돌할 때마다 속도 1.1배 증가
                        ball.setDelta(newX, newY);
                        int pointsToAdd = isBonusActive ? 20 : 10;
                        score += pointsToAdd;
                        Log.d(TAG, "Ball bounced! Score: " + score);       // 로그캣에 점수 출력
                    }
                }

                ball.move(holder.getSurfaceFrame());                            // 공의 위치를 움직임에 맞게 변화

                int ballBottom = ball.getPoint().y + ball.getSize().y;          // 공의 아래쪽 좌표와 화면 아래쪽 좌표
                int screenBottom = holder.getSurfaceFrame().bottom;

                if (ballBottom >= screenBottom) {                               // 공이 화면 아래에 닿으면
                    boolean paddleCollision = Rect.intersects(ballRect(), paddle.getRect());

                    if (!paddleCollision && !gameOverHandled) {
                        life--;                                                 // 목숨 감소
                        Log.d(TAG, "Life lost! Remaining: " + life);

                        if (life <= 0) {                                        // 목숨이 0이면
                            Log.d(TAG, "GAME OVER!");
                            ball.setStopped(true);
                            running = false;
                            gameOver = true;
                            gameOverHandled = true;                             // 게임 종료

                            if (soundLoaded) {                                  // 게임 종료 사운드 출력
                                soundPool.play(gameOverSoundId, 1f, 1f, 1, 0, 1f);
                            }

                            post(() -> {                                        // 메인 스레드에서 GameOver 액티비티로 전환
                                Intent intent = new Intent(context, GameOver.class);
                                intent.putExtra("score", score);        // 점수와 닉네임 전달
                                intent.putExtra("nickname", nickname);
                                context.startActivity(intent);
                            });
                            return;
                        } else {                                                // 목숨이 남아있다면 공 위치 초기화
                            ball.setPoint(new Point(0, 0));
                            ball.setDelta(30, 60);
                        }
                    }
                }
                // 아이템 생성 (30초마다)
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastItemSpawnTime > 30000) {                  // 현재시간 - 마지막 생성 시간 > 30초이면
                    spawnLifeItem();
                    lastItemSpawnTime = currentTime;                            // 마지막으로 아이템을 생선한 시점
                }
                // 보너스 아이템 15초마다 생성
                if (!isBonusActive && currentTime - lastBonusSpawnTime > 15000) {
                    spawnBonusItem();                                           // 보너스가 활성화되지 않은 상태에서 15초마다
                    lastBonusSpawnTime = currentTime;                           // 보너스 생성
                    bonusItemSpawnedTime = currentTime;
                }
                // 보너스2 아이템 10초마다 생성
                if (!instantScoreItemActive && currentTime - lastInstantScoreSpawnTime > 10000) {
                    spawnInstantScoreItem();
                    instantScoreItemActive = true;
                    instantScoreItemSpawnedTime = currentTime;
                    Log.d(TAG, "Instant score item spawned");
                }
                // 보너스 효과 5초후 종료
                if (isBonusActive && (currentTime - bonusActivatedTime > 5000)) {
                    isBonusActive = false;
                    Log.d(TAG, "Bonus effect ended.");
                }
                // 13초마다 폭탄 아이템 생성
                if (currentTime - lastBombSpawnTime > 13000) {
                    spawnBombItem();
                    lastBombSpawnTime = currentTime;
                    bombItemSpawnedTime = currentTime;                          // 생성 시간 기록
                }
                // 아이템 충돌 및 보너스 아이템 사라짐 처리
                Iterator<Item> iterator = items.iterator();                     // 아이템 리스트의 반복자 생성
                while (iterator.hasNext()) {
                    Item item = iterator.next();                                // 리시트의 다음 아이템 가져오기

                    if (Rect.intersects(ballRect(), item.getRect())) {          // 공과 아이템이 충돌 했는지
                        if (item.getType() == Item.TYPE_LIFE) {                 // 목숨 아이템 최대 5개까지 증가
                            if (life < 5) {
                                life++;
                                Log.d(TAG, "Life item collected! Life = " + life);
                            }
                            if (soundLoaded) {
                                soundPool.play(itemSoundId, 1f, 1f, 1, 0, 1f);
                            }
                            iterator.remove();
                        } else if (item.getType() == Item.TYPE_BONUS) {         // 보너스 아이템 5초동안 점수 2배
                            isBonusActive = true;
                            bonusActivatedTime = currentTime;
                            Log.d(TAG, "Bonus item collected! Double score for 5 seconds.");
                            if (soundLoaded) {
                                soundPool.play(itemSoundId, 1f, 1f, 1, 0, 1f);
                            }
                            iterator.remove();
                        } else if (item.getType() == Item.TYPE_BONUS2) {        // 보너스2 아이템 50점 추가
                            score += 50;
                            Log.d(TAG, "Instant score item collected! +50 points.");
                            if (soundLoaded) {
                                soundPool.play(itemSoundId, 1f, 1f, 1, 0, 1f);
                            }
                            iterator.remove();
                            instantScoreItemActive = false;                     // 아이템 먹었을 때도 상태 초기화
                            lastInstantScoreSpawnTime = currentTime;            // 먹은 시점에 시간 갱신
                        } else if (item.getType() == Item.TYPE_BOMB) {          // 폭탄 아이템 획득 시 목숨 1 감소, 점수 -30
                            life--;
                            score = Math.max(0, score - 30);
                            if (soundLoaded) {
                                soundPool.play(bombsoundId, 1f, 1f, 1, 0, 1f);
                            }
                            iterator.remove();
                            if (life <= 0 && !gameOverHandled) {                // 폭탄으로 생명 0이면 게임 오버
                                Log.d(TAG, "GAME OVER by bomb!");
                                ball.setStopped(true);
                                running = false;
                                gameOver = true;
                                gameOverHandled = true;

                                if (soundLoaded) {
                                    soundPool.play(gameOverSoundId, 1f, 1f, 1, 0, 1f);
                                }

                                post(() -> {
                                    Intent intent = new Intent(context, GameOver.class);
                                    intent.putExtra("score", score);
                                    intent.putExtra("nickname", nickname);
                                    context.startActivity(intent);
                                });
                                return;                                         // 더 이상 진행하지 않도록 리턴
                            }
                        }
                    } else {
                        if (item.getType() == Item.TYPE_BONUS && currentTime - bonusItemSpawnedTime > 5000) {
                            iterator.remove();                                  // 보너스 아이템 5초 후 자동 삭제
                            Log.d(TAG, "Bonus item disappeared after 5 seconds.");
                        } else if (item.getType() == Item.TYPE_BONUS2 && currentTime - instantScoreItemSpawnedTime > 2000) {
                            iterator.remove();                                  // 보너스2 아이템 2초 후 자동 삭제
                            instantScoreItemActive = false;
                            lastInstantScoreSpawnTime = currentTime;
                            Log.d(TAG, "Instant score item disappeared after 2 seconds.");
                        } else if (item.getType() == Item.TYPE_BOMB && currentTime - bombItemSpawnedTime > 4000) {
                            iterator.remove();                                  // 폭탄 아이템 4초 후 자동 삭제
                            Log.d(TAG, "Bomb item disappeared after 4 seconds.");
                        }
                    }
                }

                // 화면 그리기
                Canvas canvas = holder.lockCanvas();                            // 캔버스를 잠그고
                if (canvas != null) {                                           // 배경, 공, 패들, 점수 텍스트 그림
                    background.draw(canvas);
                    ball.draw(canvas);
                    paddle.draw(canvas);

                    Paint scorePaint = new Paint();                             // 점수 텍스트 설정
                    scorePaint.setColor(0xFFFFFFFF);
                    scorePaint.setTextSize(80);
                    scorePaint.setTextAlign(Paint.Align.LEFT);
                    canvas.drawText("Player : " + nickname, 100, 150, scorePaint);
                    canvas.drawText("Score : " + score, 100, 250, scorePaint);
                    for (Item item : items) {
                        item.draw(canvas);
                    }

                    int heartSize = 64;                                         // 목숨 크기 (픽셀)
                    int spacing = 20;                                           // 목숨 간격
                    int startX = 100;                                           // 목숨 시작 x좌표
                    int startY = 300;                                           // 목숨 y좌표

                    for (int i = 0; i < life; i++) {                            // 남은 목숨 옆으로 나란히 그리기
                        int left = startX + i * (heartSize + spacing);
                        int top = startY;
                        int right = left + heartSize;
                        int bottom = top + heartSize;

                        heartDrawable.setBounds(left, top, right, bottom);
                        heartDrawable.draw(canvas);
                    }

                    holder.unlockCanvasAndPost(canvas);                         // 다 그리면 캔버스를 해제하고 화면에 표시
                }
                try {
                    Thread.sleep(1);                                      // 약간의 딜레이를 줘서 속도를 조절
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        renderer.start();
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override                                                                   // 화면 변경 시
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (!gameOver) {                                                        // 게임 종료 상태가 아니면 화면 크기, 형식이 변경시 한번 그려줌
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {                                               // 캔버스를 잠그고 null이 아닌지 확인, 캔버스가 없다면 그릴수 없음
                Drawable background = getResources().getDrawable(R.drawable.background, null);
                background.setBounds(holder.getSurfaceFrame());
                background.draw(canvas);                                        // 배경을 가져와 현재 화면 크기에 맞게 그림
                ball.draw(canvas);                                              // 공과 패들도 그림
                paddle.draw(canvas);

                Paint scorePaint = new Paint();                                 // 점수 텍스트 그림
                scorePaint.setColor(0xFFFFFFFF);
                scorePaint.setTextSize(80);
                scorePaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Player : " + nickname, 100, 150, scorePaint);
                canvas.drawText("Score : " + score, 100, 250, scorePaint);
                for (Item item : items) {
                    item.draw(canvas);
                }

                int heartSize = 64;                                             // 목숨 크기 (픽셀)
                int spacing = 20;                                               // 목숨 간격
                int startX = 100;                                               // 목숨 시작 x좌표
                int startY = 300;                                               // 목숨 y좌표

                for (int i = 0; i < life; i++) {
                    int left = startX + i * (heartSize + spacing);
                    int top = startY;
                    int right = left + heartSize;
                    int bottom = top + heartSize;

                    heartDrawable.setBounds(left, top, right, bottom);
                    heartDrawable.draw(canvas);
                }

                holder.unlockCanvasAndPost(canvas);                             // 캔버스 잠금을 해제하고 화면에 그린 내용 표시
            }
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        running = false;                                                        // surface가 파괴될 때 게임 루프 종료를 알리고 완전히 끝날 때까지 대기
        if (renderer != null) {
            try {                                                               // renderer 스레드가 존재하면 종료될 때 까지 대기
                renderer.join();
            } catch (InterruptedException e) {                                  // 기다리다 인터럽트가 발생하면 예외 출력
                e.printStackTrace();
            }
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (soundPool != null) {                                                // 게임 종료할때 soundPool해제
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {                            // 터치 입력 시
        if (gameOver) return false;                                             // 게임이 종료된 상태면 터치를 무시하고 false반환

        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            // 터치 액션이 MOVE or DOWN일때 처리
            // MOVE : 터치한 상태로 움직임
            // DOWN : 화면을 처음 터치
            int newX = (int) event.getX() - paddle.getSize().x / 2;             // 터치한 x좌표에서 패들 너비의 절반을 빼, 패들의 중앙이 터치 위치
            int maxX = holder.getSurfaceFrame().right - paddle.getSize().x;     // 패들이 화면 오른쪽 끝을 넘지 않도록 최대 x좌표 계산
            if (newX < 0) newX = 0;                                             // 왼쪽 끝 제한, 패들이 화면 왼쪽보다 작아지면 0으로 고정
            if (newX > maxX) newX = maxX;                                       // 오른쪽 끝 제한, 패들이 최대값보다 커지면 maxX로 고정
            paddle.setPoint(new Point(newX, paddle.getPoint().y));              // 터치 위치를 기준으로 패들 위치 이동

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {                                               // 배경, 공, 패들, 점수 텍스트 다시 그리기
                Drawable background = getResources().getDrawable(R.drawable.background, null);
                background.setBounds(holder.getSurfaceFrame());
                background.draw(canvas);
                ball.draw(canvas);
                paddle.draw(canvas);

                Paint scorePaint = new Paint();
                scorePaint.setColor(0xFFFFFFFF);
                scorePaint.setTextSize(80);
                scorePaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Player : " + nickname, 100, 150, scorePaint);
                canvas.drawText("Score : " + score, 100, 250, scorePaint);
                for (Item item : items) {
                    item.draw(canvas);
                }

                int heartSize = 64;                                             // 목숨 크기 (픽셀)
                int spacing = 20;                                               // 목숨 간격
                int startX = 100;                                               // 목숨 시작 x좌표
                int startY = 300;                                               // 목숨 y좌표

                for (int i = 0; i < life; i++) {
                    int left = startX + i * (heartSize + spacing);
                    int top = startY;
                    int right = left + heartSize;
                    int bottom = top + heartSize;

                    heartDrawable.setBounds(left, top, right, bottom);
                    heartDrawable.draw(canvas);
                }

                holder.unlockCanvasAndPost(canvas);                             // 패들 위치가 바껴 화면을 즉시 다시 그림
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
