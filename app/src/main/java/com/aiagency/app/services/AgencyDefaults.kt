package com.aiagency.app.services

import com.aiagency.app.domain.Agent

object AgencyDefaults {
    val initialAgents = listOf(
        Agent(
            name = "Scout",
            role = "Market Researcher",
            description = "Scans freelance platforms for high-quality opportunities.",
            capabilities = listOf("opportunity_scouting", "market_analysis")
        ),
        Agent(
            name = "Analyzer",
            role = "Opportunity Analyzer",
            description = "Evaluates the feasibility and profitability of scouted leads.",
            capabilities = listOf("feasibility_study", "profitability_analysis")
        ),
        Agent(
            name = "Writer",
            role = "Proposal Writer",
            description = "Drafts high-conversion proposals tailored to client needs.",
            capabilities = listOf("proposal_drafting", "copywriting")
        ),
        Agent(
            name = "Manager",
            role = "Project Manager",
            description = "Coordinates tasks between agents and ensures deadlines are met.",
            capabilities = listOf("orchestration", "scheduling")
        ),
        Agent(
            name = "Executor",
            role = "Execution Specialist",
            description = "Performs the actual technical work of the project.",
            capabilities = listOf("coding", "implementation")
        ),
        Agent(
            name = "QA",
            role = "Quality Assurance",
            description = "Reviews deliverables to ensure they meet agency standards.",
            capabilities = listOf("quality_check", "bug_hunting")
        ),
        Agent(
            name = "Analyst",
            role = "Business Analyst",
            description = "Analyzes business growth and suggests improvements.",
            capabilities = listOf("business_strategy", "growth_analysis")
        )
    )
}
