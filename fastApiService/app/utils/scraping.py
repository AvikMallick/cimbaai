from bs4 import BeautifulSoup


# Function to extract text from HTML content
def extract_text_from_url(html_content: str) -> str:
    # Create a BeautifulSoup object
    soup = BeautifulSoup(html_content, 'html.parser')

    # Find all paragraph and header tags
    tags = ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'div', 'span', 'li', 'th', 'td',
            'blockquote', 'pre', 'code', 'caption', 'figcaption', 'label', ]

    text = []

    for tag in tags:
        # Find all elements of the current tag type in the soup object
        elements = soup.find_all(tag)

        # Iterate over each element found
        for element in elements:
            # Extract the text from the element and add it to the list
            text.append(element.text)

    # Join all the text elements into a single string
    text_content = ' '.join(text)

    # Return the text content
    return text_content
