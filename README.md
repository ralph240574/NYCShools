# 20220505-RalphMueller-NYCSchools

## Build Setup
This project was build with Android Studio Flamingo and the latetes gradle plugin


## Notes

TODO layouts, more data color

screen orientation is locked to portrait for simplicity

This app demostrates  Clean architecture


Retrofit/Okhhtp/Moshi
Jet pack compose

unit tests..

instrumented tests

all data is accessed through a repo using corouting flows repo

localdata source + remore data source

localdata source used room to store the data

after initial startup the data is dowbloaded and stored on the device
mvvm


features
- pull to refresh will force a refresh via  network
otherwise in offline mode

-sorting
- sharing from detail scrreen
- school links open extrenal browser


Possible Enhancements
network montor
smaretr cahing, unfortunately server does not set proper chaging headers
could use Etag which are in the server responses (started working on that, but it would have taken too much time to properly implelmt it)
search funtionality 
sort by shortest distance to user

find direction 

