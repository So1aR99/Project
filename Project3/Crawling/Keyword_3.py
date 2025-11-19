# 홈트
from bs4 import BeautifulSoup
import urllib.request

keywords_url = "https://www.skyedaily.com/news/news_view.html?ID=120281"
html = urllib.request.urlopen(url=keywords_url)
soup = BeautifulSoup(markup=html, features='html.parser')
print(keywords_url)

# 제목 출력 (meta 태그에서 가져오기)
meta_title = soup.find('meta', attrs={'name': 'title'})
title_text = meta_title['content'] if meta_title else "제목 없음"
print('제목:', title_text)
print()

# 본문 찾기: class="바탕글"인 <div>들을 모두 추출
content_divs = soup.find_all('div', class_='바탕글')
paragraphs = [div.get_text(strip=True) for div in content_divs if div.get_text(strip=True) != '']
first_five_paragraphs = paragraphs[:5]

# 본문 출력
for para in first_five_paragraphs:
    print(para)
print()

# 필요한 기구 키워드 리스트 (홈트 관련 장비)
equipment_keywords = ["요가매트", "덤벨", "실내자전거", "짐볼", "폼롤러", "아령", "워킹머신", "모자", "벨트", "런닝화"]

# 본문 전체 텍스트를 하나로 합치기
full_text = ' '.join(paragraphs)

# 본문에서 키워드 포함 여부 확인
found_equipments = [kw for kw in equipment_keywords if kw in full_text]

# 결과 출력
print("필요한 기구:", end=' ')
for item in found_equipments:
    print(item, end=' ')
