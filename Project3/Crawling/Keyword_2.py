# 크로스핏
from bs4 import BeautifulSoup
import urllib.request

keywords_url = "https://fashionbiz.co.kr/article/154034"
html = urllib.request.urlopen(url=keywords_url)
soup = BeautifulSoup(markup=html, features='html.parser')
print(keywords_url)

# 제목 출력
tag_title = soup.find('h1', class_='sc-9f018dc8-0 TnIrk')
print('제목:', tag_title.text)
print()

# 본문 div 찾기
article_div = soup.find('div', id='react-quill', class_='sc-9f018dc8-13 kGIqsI')

# 본문 문단 나누기
paragraphs = [p for p in article_div.get_text(separator="\n", strip=True).split('\n') if p.strip() != '']
first_five_paragraphs = paragraphs[:5]

for para in first_five_paragraphs:
    print(para)
print()

# 필요한 기구 키워드 리스트 (원하는 대로 추가 가능)
equipment_keywords = ["역도", "풀업", "줄넘기", "로잉머신", "월볼", "케틀벨", "바벨"]

# 본문 전체 텍스트 (공백으로 연결)
full_text = article_div.get_text(separator=" ", strip=True)

# 본문에서 키워드 포함 여부 확인해서 리스트 추출
found_equipments = [kw for kw in equipment_keywords if kw in full_text]

print("필요한 기구:", end=' ')
for item in found_equipments:
    print(item, end=' ')
