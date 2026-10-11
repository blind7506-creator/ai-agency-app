from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy import create_engine, Column, Integer, String, Float, ForeignKey, DateTime, Text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from pydantic import BaseModel
from typing import List
import datetime
from fastapi.middleware.cors import CORSMiddleware

SQLALCHEMY_DATABASE_URL = "sqlite:///./ai_agency.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class Agent(Base):
    __tablename__ = "agents"
    id = Column(Integer, primary_key=True)
    name = Column(String)
    role = Column(String)
    model = Column(String)
    status = Column(String, default="idle")
    uptime_pct = Column(Float, default=0.0)

class Business(Base):
    __tablename__ = "businesses"
    id = Column(Integer, primary_key=True)
    name = Column(String)
    total_profit = Column(Float, default=0.0)
    status = Column(String)

Base.metadata.create_all(bind=engine)
app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

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
        "total_profit": sum([b.total_profit for b in db.query(Business).all()]) or 0.0,
        "active_agents": db.query(Agent).filter(Agent.status == "active").count(),
        "tasks_running": 12
    }

@app.post("/agents")
def create_agent(agent: AgentCreate, db: Session = Depends(get_db)):
    db_agent = Agent(name=agent.name, role=agent.role, model=agent.model)
    db.add(db_agent); db.commit(); db.refresh(db_agent)
    return db_agent

@app.get("/agents")
def read_agents(db: Session = Depends(get_db)):
    return db.query(Agent).all()
