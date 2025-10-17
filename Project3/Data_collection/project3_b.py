# 헬스 관련 이야기를 하는 사이트에서 글들의 제목에서 키워드만 뽑음
import requests
from bs4 import BeautifulSoup
import time
from collections import Counter
import pandas as pd

keywords = ["필라테스", "크로스핏", "홈트", "러닝", "런닝", "헬스"] # 운동 관련 키워드 (러닝과 런닝은 추후 데이터에서 합쳐서 계산)
base_url = "https://gall.dcinside.com/board/lists/?id=extra_new1&page=" # 헬스 관련 커뮤니티 기본 URL (뒤에 페이지 번호만 바꿈)
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                  "AppleWebKit/537.36 (KHTML, like Gecko) "
                  "Chrome/127.0.0.0 Safari/537.36"
}
page_limit = 200  # 가져올 페이지 수(너무 많으면 제한)

# 웹크롤링
titles = [] # 수집한 게시글 제목 저장
for page in range(1, page_limit + 1): # 1페이지부터 200페이지까지 반복(가져올 페이지수+1)
    try:
        res = requests.get(f"{base_url}{page}", headers=headers, timeout=10) # 10초 이내에 응답 없으면 에러발생
        if res.status_code != 200:
            continue

        soup = BeautifulSoup(res.text, "html.parser") # 가져온 텍스트를 변환
        title_tags = soup.select("td.gall_tit a")

        for tag in title_tags: # 각 제목 태그 반복
            title = tag.get_text(strip=True) # 텍스트만 추출하고 앞뒤공백 제거
            if title and not title.startswith("공지"):  # 제목이 비어있는지 확인
                titles.append(title) #titles 리스트에 저장
        time.sleep(0.2)  # 서버 부하 방지
    except:
        continue

# 키워드별 개수 세기
counts = Counter()
for title in titles: # 모든 제목 반복
    for keyword in keywords: # 각 제목에 대해 모든 키워드 확인
        if keyword in title: # 키워드가 제목에 포함되면
            counts[keyword] += 1 # 해당키워드 +1

# 결과 출력
print("\n키워드별 게시글 수")
print("-" * 20)
for keyword, count in counts.items(): # 키워드별 게시글 수 출력
    print(f"{keyword}: {count}건")

print(f"\n✅ 총 {len(titles)}개 게시글 제목 크롤링 완료!") # 크롤링한 총 게시글 개수 출력

# CSV로 저장
df = pd.DataFrame(list(counts.items()), columns=["키워드", "검색량"])

df.to_csv("recently_sports.csv", encoding="utf-8", mode="w", index=False)
df.to_csv("recently_sports_EXCEL.csv", encoding="cp949", mode="w", index=False)
