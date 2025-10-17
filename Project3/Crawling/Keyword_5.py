# 헬스
from bs4 import BeautifulSoup
import urllib.request

keywords_url = "https://www.digitaltoday.co.kr/news/articleView.html?idxno=481853"
html = urllib.request.urlopen(url=keywords_url)
soup = BeautifulSoup(markup=html, features='html.parser')
print(keywords_url)

# 제목 출력 (meta 태그에서 가져오기)
meta_title = soup.find('meta', attrs={'name': 'title'})
title_text = meta_title['content'] if meta_title else "제목 없음"
print('제목:', title_text)
print()

# 페이지 전체에서 <p> 태그 추출 (본문만 포함되도록 주의)
paragraphs = soup.find_all('p')

# 본문 필터링: 광고/비어 있는 문장 제거
filtered_paragraphs = [p.get_text(strip=True) for p in paragraphs if p.get_text(strip=True) != '']

for para in filtered_paragraphs:
    print(para)
print()

print("필요한 기구: 스트랩, 헬스 벨트, 보충제, 영양제, 손목 보호대")