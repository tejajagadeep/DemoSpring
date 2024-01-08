from bs4 import BeautifulSoup

html_content = '''
<div class="row fontawesome-icon-list">
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-book"><i class="fa fa-address-book" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-book</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-book-o"><i class="fa fa-address-book-o" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-book-o</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-card"><i class="fa fa-address-card" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-card</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/address-card-o"><i class="fa fa-address-card-o" aria-hidden="true"></i> <span class="sr-only">Example of </span>address-card-o</a></div>
    <div class="fa-hover col-md-3 col-sm-4"><a href="../icon/adjust"><i class="fa fa-adjust" aria-hidden="true"></i> <span class="sr-only">Example of </span>adjust</a></div>
</div>
'''

# Parse the HTML content
soup = BeautifulSoup(html_content, 'html.parser')

# You can now manipulate the parsed HTML as needed
# For example, you can add more icons dynamically

# Adding a new icon dynamically
new_icon = soup.new_tag('div', attrs={'class': 'fa-hover col-md-3 col-sm-4'})
new_icon.append(soup.new_tag('a', href='../icon/new-icon'))
new_icon.a.append(soup.new_tag('i', class_='fa fa-new-icon', aria_hidden='true'))
new_icon.a.append(' <span class="sr-only">Example of </span>new-icon')

# Append the new icon to the existing structure
soup.find('div', class_='fontawesome-icon-list').append(new_icon)

# Print the modified HTML
print(soup.prettify())

