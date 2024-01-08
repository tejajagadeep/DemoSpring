from bs4 import BeautifulSoup

html = """
<div class="row fontawesome-icon-list">
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-book"><i class="fa fa-address-book" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-book</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-book-o"><i class="fa fa-address-book-o" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-book-o</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-card"><i class="fa fa-address-card" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-card</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-card-o"><i class="fa fa-address-card-o" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-card-o</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/adjust"><i class="fa fa-adjust" aria-hidden="true"></i> <span class="sr-only">Example of </span>adjust</a></div>
</div>
"""

soup = BeautifulSoup(html, 'html.parser')

# Find all div elements with class 'fa-hover' and filter based on the href attribute
selected_icons = soup.find_all('div', class_='fa-hover', href=lambda value: value and value.endswith(('address-card', 'address-card-o', 'adjust')))

# Print the selected icons
for icon in selected_icons:
    print(icon.text.strip())
