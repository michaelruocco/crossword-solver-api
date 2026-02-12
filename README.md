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
```

## Run locally

The crossword puzzle creation directly from an image relies on tesseract. To install tesseract
(on mac) you can run

```bash
brew install tesseract
```

Once tesseract is available you can run the service with

```gradle
./gradlew bootRun
```

This will require a postgres database to be available, you can start one
using docker compose if you run

```bash
docker compose up -d
```

If you would rather run with stubbed in memory repositories to save
spinning up a postgres instance you can run

```gradle
./gradlew bootRun --args='--repository.type=stub'
``

## TODO

* Add cryptic puzzle detection and solving
* Try to figure out how to ensure tricky clues get answered correctly:
    * Ram (3) -> TAP/TOP -> TUP
    * Sharp bark (3) -> YIP -> YAP