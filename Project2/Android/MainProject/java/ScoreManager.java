package com.iot.project1;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String PREF_NAME = "GameScores";                   // SharedPreferences 파일 이름
    private static final String KEY_SCORES = "scores";                      // 점수 저장 키
    private static final String KEY_NAMES = "names";                        // 닉네임 저장 키
    private static final int MAX_RANKS = 3;                                 // 상위 3개만 저장

    private SharedPreferences prefs;                                        // 데이터 저장소

    public ScoreManager(Context context) {                                  // ScoreManager 생성자
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // SharedPreferences 초기화 (앱 내부에서만 접근 가능)
    }

    // 점수 정보를 담는 내부 클래스
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        public String nickname;                                             // 플레이어 닉네임
        public int score;                                                   // 플레이어 점수

        public ScoreEntry(String nickname, int score) {                     // ScoreEntry 생성자
            this.nickname = nickname;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreEntry other) {                            // 정렬을 위한 비교 메서드
            return Integer.compare(other.score, this.score);                // 내림차순 정렬 (높은 점수가 앞으로)
        }
    }

    // 새로운 점수 추가 및 랭킹 업데이트
    public void addScore(String nickname, int score) {
        List<ScoreEntry> rankings = getRankings();                          // 현재 저장된 랭킹 목록 가져오기
        rankings.add(new ScoreEntry(nickname, score));                      // 새로운 점수를 목록에 추가

        Collections.sort(rankings);                                         // 점수 순으로 정렬 (높은 점수부터)

        if (rankings.size() > MAX_RANKS) {                                  // 랭킹이 3개를 초과하면
            rankings = rankings.subList(0, MAX_RANKS);                      // 상위 3개만 유지하고 나머지는 제거
        }

        saveRankings(rankings);                                             // 업데이트된 랭킹을 저장
    }

    // 현재 랭킹 가져오기
    public List<ScoreEntry> getRankings() {
        List<ScoreEntry> rankings = new ArrayList<>();                      // 랭킹을 담을 빈 리스트 생성

        String scoresStr = prefs.getString(KEY_SCORES, "");                 // 저장된 점수 문자열 가져오기
        String namesStr = prefs.getString(KEY_NAMES, "");                   // 저장된 닉네임 문자열 가져오기

        if (!scoresStr.isEmpty() && !namesStr.isEmpty()) {                  // 저장된 데이터가 있으면
            String[] scores = scoresStr.split(",");                         // 쉼표로 구분된 점수들을 배열로 분리
            String[] names = namesStr.split(",");                           // 쉼표로 구분된 닉네임들을 배열로 분리

            int length = Math.min(scores.length, names.length);             // 점수와 닉네임 배열 중 짧은 길이를 기준으로
            for (int i = 0; i < length; i++) {                              // 반복문 실행
                try {
                    int score = Integer.parseInt(scores[i]);                // 문자열을 정수로 변환
                    rankings.add(new ScoreEntry(names[i], score));          // ScoreEntry 객체를 생성해서 리스트에 추가
                } catch (NumberFormatException e) {                         // 숫자 변환 실패 시
                    e.printStackTrace();                                    // 에러 출력
                }
            }
        }

        return rankings;                                                    // 완성된 랭킹 리스트 반환
    }

    // 랭킹 저장
    private void saveRankings(List<ScoreEntry> rankings) {
        StringBuilder scoresBuilder = new StringBuilder();                  // 점수들을 하나의 문자열로 만들기 위한 빌더
        StringBuilder namesBuilder = new StringBuilder();                   // 닉네임들을 하나의 문자열로 만들기 위한 빌더

        for (int i = 0; i < rankings.size(); i++) {                         // 랭킹 리스트를 순회하면서
            if (i > 0) {                                                    // 첫 번째가 아니면
                scoresBuilder.append(",");                                  // 쉼표로 구분
                namesBuilder.append(",");
            }
            scoresBuilder.append(rankings.get(i).score);                    // 점수 추가
            namesBuilder.append(rankings.get(i).nickname);                  // 닉네임 추가
        }

        SharedPreferences.Editor editor = prefs.edit();                     // SharedPreferences 편집 모드
        editor.putString(KEY_SCORES, scoresBuilder.toString());             // 점수 문자열 저장
        editor.putString(KEY_NAMES, namesBuilder.toString());               // 닉네임 문자열 저장
        editor.apply();                                                     // 변경사항 저장 완료
    }

    // 해당 점수가 랭킹에 들어가는지 확인
    public boolean isTopScore(int score) {
        List<ScoreEntry> rankings = getRankings();                          // 현재 랭킹 가져오기

        if (rankings.size() < MAX_RANKS) {                                  // 랭킹이 3개 미만이면
            return true;                                                    // 무조건 TOP 3에 들어감
        }

        return score > rankings.get(MAX_RANKS - 1).score;                   // 3위 점수보다 높으면 랭킹에 들어감
    }
}
