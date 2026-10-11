# AI Agency App

Android AI Agency application with multi-agent orchestration.

## Project Setup & Architecture
- **Architecture:** MVVM with Clean Architecture.
- **UI Framework:** Jetpack Compose with Material 3.
- **Theme:** Dark theme with orange accents.
- **Language:** Kotlin 1.9.20.
- **JDK:** 17.
- **Min SDK:** 26 | **Target SDK:** 34.

### Project Structure
- `app/src/main/java/com/aiagency/app/ui`: UI components, themes, and screens.
- `app/src/main/java/com/aiagency/app/domain`: Business logic and domain models.
- `app/src/main/java/com/aiagency/app/data`: Data sources and repository implementations.
- `app/src/main/java/com/aiagency/app/services`: External API integrations and orchestrators.

## Build Requirements
As the project is developed in a Termux environment, local compilation is not feasible.
- **Remote Build Required:** A machine with Android Studio / Android SDK / Gradle is required to build the APK.
- **Configuration:** Use the provided `.gradle.kts` and `build.gradle.kts` files.

## Known Limitations
- **Local Build:** Cannot be run locally in Termux.
- **Verification:** Foundation is verified via structural and syntax analysis only.
- **Agent Capabilities:** Agents currently use a `MockLLMProvider`. They can route tasks and simulate responses but cannot perform real external platform actions (e.g., scraping, sending emails) until specific tool integrations are implemented.

## Roadmap
- [x] Project Foundation (gradle, theme, navigation placeholders)
- [x] Core Multi-Agent Engine (Domain models, Registry, Orchestrator, Basic Specialists)
- [ ] Data Persistence (Room)
- [ ] LLM Integration (Actual API Provider)
- [ ] UI Implementation for the 10 core areas
