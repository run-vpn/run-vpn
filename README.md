# Brand new VPN application

## Tech Stack
**Shared Tech Stack:** 
- Decompose (presentation layer) 
- SQLDelight (database orm) 
- Coroutines (Multithreading) 
- Ktor & Ktorfit (Network, Retrofit analog) 
- Koin (Dependency Injection) 
- Kermit (Log util) 
- Okio (file storage, ?) 
- MOKO Resources

## Module structure
- androidApp - Android Application Module
- iosApp - XCode Swift project for iOS
- :shared - main point for build shared lib
- :core - core modules (network, tea, ui, etc...)
- :data - data modules
- :feature - feature modules (depens on :data modules)

**Tools:** 
- Geminio (generating templates)
