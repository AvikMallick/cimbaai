from pydantic import BaseModel, HttpUrl


# Define URLRequest model
class URLRequest(BaseModel):
    url: HttpUrl  # URL field
