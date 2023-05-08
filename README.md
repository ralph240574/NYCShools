# 20220505-RalphMueller-NYCSchools

## Build Setup
This project was build with Android Studio Flamingo and the latest gradle plugin (8.0)

## Notes
This app tries to follow a basic clean architecture using MVI

### Data Layer
The SchoolRepo class manages all data access. It has a local data source and remote data source.
The local data source is using room to store the data. The remote data source is using
Retrofit/Okhhtp/Moshi

### Viewmodels
The viewmodels are using coroutine flows to get data from the repo. Each viewmodel updates a uiState
StateFlow which represents the entire UI state and
which is observerd by the relevant Composable

### UI
The UI is using Jetpack Compose. The screen orientation is locked to portrait for simplicity
strings are hardcoded for simplicity..
not all available data is displayed, only a subset which seems most important

### Dependency Injection
Hilt dependency injection is used

### Testing
- unit tests which cover datalayer and ViewModels
- instrumented tests, only happy path

### App Features
- after initial startup the data is dowbloaded and stored on the device
- pull to refresh will force a refresh via network, otherwise app is in offline mode
- sorting, note after sorting by sat score all school without sat scores are filtered out.
- sharing from detail screen
- school links open external browser

### Possible Enhancements
- network monitor, show message if no internet connection
- search functionality: search for any string in the data
- filter functionality: filter out schools based on sat scores
- sort by shortest distance to user
- get direction to school
- add more data fields 
- more UI tests..

