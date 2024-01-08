soup = BeautifulSoup(html, 'html.parser')

for div in soup.find_all('div', class_='fa-hover'):
    span = div.find('span', class_='sr-only')
    if span:
        text_after_span = span.find_next_sibling(text=True)
        print(text_after_span.strip())
