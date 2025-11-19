from pytrends.request import TrendReq
import pandas as pd
import time

def new_pytrends():
    return TrendReq(hl='ko', tz=540, timeout=(10, 30)) # 구글이 일정 횟수 이상 요청 시 세션 차단

keywords = ['필라테스', '크로스핏', '홈트', '러닝', '헬스']
years = [2025] # 조사할 연도리스트 (한꺼번에 많은 연도를 요청하면 세션 차단되므로 하나씩 넣어 데이터를 추가함)
results = [] # 결과 저장 빈리스트

for year in years: # 리스트안에 연도를 여러개 입력 시 연도별로 반복 작업
    print(f"\n[{year}] 연도별 키워드 인기 조사 중...")

    pytrends = new_pytrends()
    timeframe = f'{year}-01-01 {year}-12-31' # 해당 연도의 1월 1일부터 12월 31일까지 문자열로 저장

    for i in range(0, len(keywords), 5): # 키워드를 5개로 묶어 처리, 키워드가 늘어났을 때 방지
        kw_chunk = keywords[i:i + 5]

        for attempt in range(5):  # 재시도 5회로 늘림
            try:
                pytrends.build_payload(kw_chunk, cat=0, timeframe=timeframe, geo='KR', gprop='')
                interest_over_time_df = pytrends.interest_over_time() # 데이터를 잘 가져오면 루프 종료
                break
            except Exception as e: # 에러가 발생하면 에러 메시지 출력
                print(f"  요청 실패: {e}, 재시도 {attempt + 1}/5 - 30초 대기") # 재시도 메시지 출력
                time.sleep(30)  # 대기시간 30초로 증가
                pytrends = new_pytrends() # 새로운 객체생성
        else:
            print("  5회 재시도 실패, 다음으로 넘어갑니다.") # 데이터들 계속 가져오지 못하면 출력
            continue

        if interest_over_time_df.empty: # 가져온 데이터가 비어있는지 확인
            print("  데이터가 없습니다.")
            continue

        for kw in kw_chunk: # 각 키워드 별로 평균 검색량 계산
            avg_interest = interest_over_time_df[kw].mean()
            results.append({'연도': year, '키워드': kw, '평균 검색량(상대적)': avg_interest})

        time.sleep(20)  # 요청 간 대기 20초

df = pd.DataFrame(results) # results리스트 데이터프레임으로 변환
df = df.sort_values(['연도', '평균 검색량(상대적)'], ascending=[True, False]) # 연도는 오름차순 평균 검색량은 내림차순

print("\n===== 연도별 운동 키워드 인기 순위 =====")
for year in years: # 각 연도별로 데이터 필터링
    print(f"\n[{year}년]")
    temp = df[df['연도'] == year]
    for i, (_, row) in enumerate(temp.iterrows(), 1):
        print(f"{i}. {row['키워드']}: 평균 검색량(상대적) {row['평균 검색량(상대적)']:.2f}")

df.to_csv('yearly_sports_trends.csv', encoding='utf-8', mode='a', index=False)
df.to_csv("yearly_sports_trends_EXCEL.csv", encoding='cp949', mode='a', index=False)  # 엑셀용
# 파일 저장할때 처음엔 새로 쓰기 "w"로 저장하고 그 이후에 추가로 저장할때는 추가모드"a"로 저장
