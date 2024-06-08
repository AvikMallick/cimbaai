from pydantic_settings import BaseSettings


# Define application settings
class Settings(BaseSettings):
    genai_api_key: str  # API key for generative AI

    class Config:
        env_file = ".env"  # Load environment variables from .env file


settings = Settings()  # Create settings instance
