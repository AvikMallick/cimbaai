from fastapi import FastAPI
from app.routes.summary import router as summary_router

app = FastAPI()  # Create FastAPI application

app.include_router(summary_router)  # Include summary router

# Run application if this file is the entry point
if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)  # Run application with uvicorn
