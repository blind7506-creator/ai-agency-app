from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy import create_engine, Column, Integer, String, Float, ForeignKey, DateTime, Text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from pydantic import BaseModel
from typing import List
import datetime

# Database Setup
SQLALCHEMY_DATABASE_URL = "sqlite:///./ai_agency.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True)
    email = Column(String, unique=True)

class Agent(Base):
    __tablename__ = "agents"
    id = Column(Integer, primary_key=True)
    name = Column(String)
    role = Column(String)
    model = Column(String)
    status = Column(String, default="idle")

class Business(Base):
    __tablename__ = "businesses"
    id = Column(Integer, primary_key=True)
    name = Column(String)
    total_profit = Column(Float, default=0.0)
    status = Column(String)

Base.metadata.create_all(bind=engine)
app = FastAPI()

def get_db():
    db = SessionLocal()
    try: yield db
    finally: db.close()

class AgentCreate(BaseModel):
    name: str; role: str; model: str

@app.get("/stats")
def get_stats(db: Session = Depends(get_db)):
    return {
        "total_agents": db.query(Agent).count(),
        "total_profit": sum([b.total_profit for b in db.query(Business).all()])
    }

@app.post("/agents")
def create_agent(agent: AgentCreate, db: Session = Depends(get_db)):
    db_agent = Agent(name=agent.name, role=agent.role, model=agent.model)
    db.add(db_agent); db.commit(); db.refresh(db_agent)
    return db_agent
