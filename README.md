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

## Running locally

The crossword puzzle creation directly from an image relies on Tesseract and AWS Bedrock, and optionally can use
a postgres database.

### Installing tesseract

To install tesseract (on mac) you can run

```bash
brew install tesseract
```

### Using AWS Bedrock

The application uses the standard AWS SDK for connection and requires the ability to access
and AWS account with permissions to call AWS Bedrock and use it to call an AI model.

A different model can be configured for each of the following features:

* Answer Finder -> Used to look up candidate answers for clues
* Clue Extractor -> Used to perform OCR on crossword images to extract clue information
* Clue Type Policy -> Used to determine the type of each clue (either standard or cryptic)

The default model for answer finder and clue extractor is Claude Opus 4.6 (model ID 
`eu.anthropic.claude-opus-4-6-v1`). The default model for clue type policy is
Claude Sonnet 4.6 (model ID `eu.anthropic.claude-sonnet-4-6`)

You may need to log into your AWS console and use the model with the Chat / Text
playground first, or apply some other adjustments to make the model (or any model
of your choosing) available for use.

### Using Postgres

The application can use in memory stubbed repositories for data persistence, but you can also use
a postgres database. If you have docker compose available you can spin up a postgres instance for the
application to use by running:

```bash
docker compose up -d
```

### Running from gradle

Using gradle is the simplest way to run the service locally. Once tesseract, AWS Bedrock and model ID are
configured, and you have a postgres database available you can run the service with:

```gradle
./gradlew bootRun
```

If you would rather run with stubbed in memory repositories to save
spinning up a postgres instance you can run:

```gradle
./gradlew bootRun --args='--repository.type=stub'
```

### Running from docker

You can also build the application into a docker image and run that rather than running the application
directly on your local machine if you wish, as this saves the need to install tesseract.

To do this, you need to build the docker image by running:

```gradle
./gradlew buildImage
```

Then you can run the application container and database by running:

```bash
docker compose --profile docker-api up -d
```

Note - this will look for two environment variables named `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`
and will set them up inside the docker container to enable access to AWS Bedrock, without these set up on
the host the connection will error when running inside the docker container. If you prefer, you can edit
the values in the `docker-compose.yml` file directly, but be careful not to try and commit that change!

## TODO

* Clean up tesseract / grid extractor by splitting into its own module
* Fix sonar issues