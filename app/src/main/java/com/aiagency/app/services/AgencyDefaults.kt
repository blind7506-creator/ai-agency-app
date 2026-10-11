package com.aiagency.app.services

import com.aiagency.app.domain.Agent
import com.aiagency.app.domain.AgentOperationalStatus

object AgencyDefaults {
    val initialAgents = listOf(
        Agent(
            name = "Scout",
            role = "Market Researcher",
            description = "Scans freelance platforms for high-quality opportunities.",
            capabilities = listOf("opportunity_scouting", "market_analysis"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Scan platforms for keywords related to AI agency services. Filter by budget and rating."
        ),
        Agent(
            name = "Analyzer",
            role = "Opportunity Analyzer",
            description = "Evaluates the feasibility and profitability of scouted leads.",
            capabilities = listOf("feasibility_study", "profitability_analysis"),
            operationalStatus = AgentOperationalStatus.OPERATIONAL_LOCALLY,
            systemInstructions = "Analyze lead data against agency capacity and historical profit margins."
        ),
        Agent(
            name = "Writer",
            role = "Proposal Agent",
            description = "Drafts high-conversion proposals tailored to client needs.",
            capabilities = listOf("proposal_drafting", "copywriting"),
            operationalStatus = AgentOperationalStatus.OPERATIONAL_LOCALLY,
            systemInstructions = "Draft persuasive proposals emphasizing ROI and technical excellence."
        ),
        Agent(
            name = "Manager",
            role = "Project Manager",
            description = "Coordinates tasks between agents and ensures deadlines are met.",
            capabilities = listOf("orchestration", "scheduling"),
            operationalStatus = AgentOperationalStatus.OPERATIONAL_LOCALLY,
            systemInstructions = "Break down projects into sub-tasks and assign to specialists."
        ),
        Agent(
            name = "Executor",
            role = "Execution Agent",
            description = "Performs the actual technical work of the project.",
            capabilities = listOf("coding", "implementation"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Implement technical requirements based on the project brief."
        ),
        Agent(
            name = "QA",
            role = "Quality Assurance Agent",
            description = "Reviews deliverables to ensure they meet agency standards.",
            capabilities = listOf("quality_check", "bug_hunting"),
            operationalStatus = AgentOperationalStatus.OPERATIONAL_LOCALLY,
            systemInstructions = "Strictly verify deliverables against acceptance criteria."
        ),
        Agent(
            name = "Analyst",
            role = "Business Analyst",
            description = "Analyzes business growth and suggests improvements.",
            capabilities = listOf("business_strategy", "growth_analysis"),
            operationalStatus = AgentOperationalStatus.OPERATIONAL_LOCALLY,
            systemInstructions = "Evaluate agency KPIs and suggest growth pivots."
        ),
        Agent(
            name = "Social Marketer",
            role = "Social Media Marketing Agent",
            description = "Manages agency presence and lead generation via social channels.",
            capabilities = listOf("content_creation", "social_engagement"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Create content calendars and engage with target industry leaders."
        ),
        Agent(
            name = "Finance",
            role = "Finance Manager Agent",
            description = "Handles invoicing, expense tracking, and profit calculations.",
            capabilities = listOf("financial_reporting", "budgeting"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Calculate margins, track expenses, and generate financial health reports."
        ),
        Agent(
            name = "Monitor",
            role = "Monitoring & Reporting Agent",
            description = "Continuous tracking of project health and client satisfaction.",
            capabilities = listOf("health_monitoring", "client_reporting"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Track project milestones and flag delays before they happen."
        ),
        Agent(
            name = "CRM",
            role = "Customer Relations Agent",
            description = "Manages client communication and relationship nurturing.",
            capabilities = listOf("client_communication", "crm_management"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Maintain a professional tone and ensure timely client updates."
        ),
        Agent(
            name = "Ecommerce",
            role = "E-commerce Operations Agent",
            description = "Manages online store setup and optimization.",
            capabilities = listOf("store_optimization", "product_listing"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Optimize product listings for conversion and SEO."
        ),
        Agent(
            name = "Intelligence",
            role = "Research & Competitor Intelligence Agent",
            description = "Analyzes competitor moves and identifies market gaps.",
            capabilities = listOf("competitor_tracking", "gap_analysis"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Deep dive into competitor offerings to identify unique selling points."
        ),
        Agent(
            name = "Compliance",
            role = "Security & Compliance Agent",
            description = "Ensures all project deliverables meet legal and security standards.",
            capabilities = listOf("security_audit", "legal_compliance"),
            operationalStatus = AgentOperationalStatus.INTEGRATION_REQUIRED,
            systemInstructions = "Audit code and contracts for security vulnerabilities and legal risks."
        )
    )
}
