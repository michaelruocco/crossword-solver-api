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

* Add proper grid space detection to enable removing S from HORSDOEUVRES to give HORSDOEUVRE
* Add cryptic puzzle detection
* Add endpoint to accept direct file upload
* Add file size validation on direct file upload
* Try to figure out how to ensure RAM / TAP / TUP clue gets answered correctly