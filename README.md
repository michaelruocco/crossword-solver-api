# Crossword solver API

Crossword solver API

## Useful Commands

```gradle
// cleans build directories
// checks dependency versions
// checks for gradle issues
// formats code
// builds code
// runs tests
// checks dependencies for vulnerabilities
./gradlew clean dependencyUpdates lintGradle spotlessApply build


## TODO

* Add cryptic puzzle detection and solving
* Try to figure out how to ensure tricky clues get answered correctly:
    * Ram (3) -> TAP/TOP -> TUP
    * Sharp bark (3) -> YIP -> YAP
* Add endpoint to accept direct file upload
* Add file size validation puzzle creation