import google.generativeai as genai
from ..config import settings

# Configure the generative AI with the API key from settings
genai.configure(api_key=settings.genai_api_key)

# Initialize the generative model
model = genai.GenerativeModel('gemini-1.5-flash')


# Function to generate a summary for a given text
def generate_summary(text: str) -> str:
    # Define the prompt for the generative model
    prompt = (
        "Summarize the following text which is scraped from an url:\n"
        f"{text}"
    )

    # Generate the content using the model
    response = model.generate_content(prompt)

    # Extract the summary from the response
    summary = response.text

    # Return the summary
    return summary
