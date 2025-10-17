# 러닝
from bs4 import BeautifulSoup
import urllib.request

keywords_url = "https://www.sedaily.com/NewsView/2GVIXWB31N"
html = urllib.request.urlopen(url=keywords_url)
soup = BeautifulSoup(markup=html, features='html.parser')
print(keywords_url)

# 제목 출력 (meta 태그에서 가져오기)
meta_title = soup.find('meta', attrs={'name': 'title'})
title_text = meta_title['content'] if meta_title else "제목 없음"
print('제목:', title_text)
print()

# 본문 찾기: class="article_view"인 <div> 추출
article_body = soup.find('div', class_='article_view')
if article_body:
    # BR 태그를 기준으로 단락 나누기
    paragraphs = []
    for content in article_body.stripped_strings:
        if len(content) > 20:  # 너무 짧은 텍스트 제외
            paragraphs.append(content)
    # 6단락 출력
    first_five_paragraphs = paragraphs[:6]
else:
    first_five_paragraphs = []

# 본문 출력
for para in first_five_paragraphs:
    print(para)
print()

# 필요한 기구 키워드 리스트 (러닝 관련 장비)
equipment_keywords = ["러닝화", "운동화", "러닝복", "워치", "스마트워치", "이어폰", "암밴드",
                      "물통", "러닝벨트", "압박복", "레깅스", "선글라스", "모자", "양말",
                      "에너지젤", "비타민", "보조제"]

# 본문 전체 텍스트를 하나로 합치기
full_text = ' '.join(paragraphs) if paragraphs else ''

# 본문에서 키워드 포함 여부 확인
found_equipments = [kw for kw in equipment_keywords if kw in full_text]

# 결과 출력
print("필요한 기구:", end=' ')
for item in found_equipments:
    print(item, end=' ')
print('등')