from bs4 import BeautifulSoup


# Function to extract text from HTML content
def extract_text_from_url(html_content: str) -> str:
    # Create a BeautifulSoup object
    soup = BeautifulSoup(html_content, 'html.parser')

    text_content = soup.get_text()

    # Return the text content
    return text_content
