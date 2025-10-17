# 필라테스
from bs4 import BeautifulSoup
import urllib.request

keywords_url = "https://www.job-post.co.kr/news/articleView.html?idxno=19186"
html = urllib.request.urlopen(url=keywords_url)
soup = BeautifulSoup(markup=html, features='html.parser')
print(keywords_url)

# 제목 출력
tag_title = soup.find('title')
print('제목:', tag_title.text)
print()

# 본문 div 찾기
article_div = soup.find('div', id='article-view-content-div')

if article_div:
    # 모든 <p> 태그 리스트로 가져오기
    paragraphs = article_div.find_all('p')
    # 앞 두 문단 텍스트만 추출
    first_two_paragraphs = [p.get_text(strip=True) for p in paragraphs[:2]]

    for para in first_two_paragraphs:
        print(para)

    # 본문 전체 텍스트 합치기
    full_text = ' '.join([p.get_text(strip=True) for p in paragraphs])

    # "필라테스 기구" 키워드 포함 여부 확인
    if "필라테스 기구" in full_text:
        print("\n필요한 기구: 필라테스 기구" )
    else:
        print("\n필요한 기구: 해당 키워드가 본문에 없습니다.")
else:
    print("기사 본문을 찾을 수 없습니다.")
