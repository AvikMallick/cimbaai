from fastapi import APIRouter, HTTPException
from httpx import AsyncClient, HTTPStatusError
from pydantic import HttpUrl
from ..utils.scraping import extract_text_from_url
from ..utils.generative_ai import generate_summary

router = APIRouter()  # Create API router


# Define /summary route
@router.get("/summary")
async def summarize_url(url: HttpUrl):  # Take URL as query parameter
    try:
        async with AsyncClient() as client:  # Create HTTP client
            response = await client.get(str(url))  # Get URL content
            response.raise_for_status()  # Raise exception if status is not 200
    except HTTPStatusError as e:
        raise HTTPException(status_code=e.response.status_code,
                            detail="Failed to fetch the webpage")  # Handle HTTP errors

    text_content = extract_text_from_url(response.text)  # Extract text from URL content

    if not text_content:
        raise HTTPException(status_code=500,
                            detail="Failed to extract text content from the webpage")  # Handle extraction errors

    try:
        summary = await generate_summary(text_content)  # Generate summary from text content
    except Exception as e:
        raise HTTPException(status_code=500,
                            detail="Failed to summarize the text")  # Handle summarization errors

    return {"summary": summary}  # Return summary
