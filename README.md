# Carsharing cross-browser UI and API tests

Automated UI and API tests for a carsharing web app.
The UI tests run in **Chrome, Firefox and Edge**, in parallel, either locally or on a
remote **Selenium Grid** in Docker. The API tests call the site's REST backend directly,
no browser needed. Results are shown in an **Allure** report.

## Tech stack

| Area           | Tools                                                    |
|----------------|----------------------------------------------------------|
| Language       | Java 21                                                  |
| Build          | Gradle 8.14 (wrapper included)                           |
| Browser driver | Selenium 4.49 (WebDriver, RemoteWebDriver)               |
| API client     | REST Assured 5.5 + Jackson (requests/responses in Allure) |
| API contract   | JSON Schema (networknt json-schema-validator), schemas from the backend Swagger |
| Test runner    | TestNG 7.12 (suites, groups, data providers, parallel)   |
| Grid           | Selenium Grid 4 in Docker (hub + Chrome/Firefox/Edge)    |
| Report         | Allure 2                                                 |
| Logging        | SLF4J + Logback (a separate log file for every browser)  |
| Test data      | Datafaker, Lombok                                        |

## Project structure

The project follows the **Page Object Model (POM)** pattern: every page of the site is a class
in `pages/` that holds its locators and actions. Tests call page methods
and don't work with locators directly, so a change on the site is fixed in one place.

```
src/main/java
├── manager/
│   ├── AppManager.java      # opens/closes the browser for every test, one driver per thread
│   └── DriverFactory.java   # local driver or RemoteWebDriver on the Grid
├── pages/                   # Page Objects (HomePage, LoginPage, RegistrationPage, ...)
├── api/                     # REST layer: BaseApi (config, base spec), Endpoints (all paths)
│                            # + controllers as in Swagger (AuthenticationController, CarController)
├── dto/                     # data models named and built as the backend Swagger schemas
│                            # (RegistrationBodyDto, AuthenticationBodyDto, CarDto, SearchDto, ...);
│                            # the same models fill the UI forms and are sent/received by the API tests
├── enums/                   # Fuel, Months
└── utils/                   # ConfigReader, UserFactory, CarFactory, SearchFactory,
                             # SchemaValidator, listeners, screenshots
src/main/resources
└── config.properties        # default settings (not in git, see below)
src/test/java
├── ui_tests/                # UI test classes
└── api_tests/               # API test classes (no browser)
src/test/resources
├── smoketests.xml               # smoke, one browser
├── regressiontests.xml          # smoke + regression, one browser
├── crossbrowser.xml             # smoke, 3 browsers in parallel
├── crossbrowser_regression.xml  # smoke + regression, 3 browsers in parallel
├── apitests.xml                 # API tests, no browser
├── schemas/                     # JSON schemas of the API answers, one file per Swagger schema
├── logback.xml
└── allure.properties
docker-compose.yml           # Selenium Grid: hub + 3 browser nodes
```

## Test coverage

| Class               | What is tested                                                        |
|---------------------|-----------------------------------------------------------------------|
| `RegistrationTests` | positive registration, empty fields, existing user, checkbox, invalid emails and passwords |
| `LoginTests`        | login, logout, wrong credentials, invalid login form                  |
| `AddCarTests`       | adding a car: positive case and validation of every field             |
| `FindYourCarTests`  | car search: positive, calendar, invalid search data                   |
| `NavigationTests`   | header menu, "Let the car work", social network links                 |

API tests (call the site's REST backend directly, endpoints and models as in the backend Swagger):

| Class                           | What is tested                                             |
|---------------------------------|------------------------------------------------------------|
| `AuthenticationControllerTests` | registration: positive, already registered user, blank email, blank password, invalid emails and passwords (data providers); login: token in the answer, wrong credentials |
| `CarControllerTests`            | search: positive, blank city, empty request, end date before start date, the same day, dates in the past; adding a car: positive, missing field, no/invalid token; deleting a car: positive (checked in the user's car list), no token, not existing car |

How the API tests check an answer:

1. **status code** first, as a hard assert: if it is wrong, the body has another format;
2. **JSON schema** of the body (`utils.SchemaValidator`), once per endpoint + status code;
3. **values** (error text, token, found cars).

Expected results come from Swagger or, where Swagger has no constraint, from the UI behaviour.
Such assumptions are marked `TO CLARIFY` in the test comments.
Tests create as little data as possible: checks that need a user use the existing user
from `config.properties`, and a car added by a test is deleted after it.

Groups: `smoke`, `regression`, `external` (links to other sites), `api`, `api_smoke`.

> Some tests fail because of **known bugs** of the site. The tests check the expected behaviour,
> so they stay red until the bugs are fixed and keep the bugs visible in the report.
>
> - UI: an email with a Cyrillic domain is accepted, login is case-sensitive to the email.
> - API: an email with a Cyrillic domain is accepted; search accepts a blank city, an empty request,
>   an end date before the start date, the same start and end day and dates in the past;
>   a blank password is answered in another error format;
>   a request without a token gets 403 instead of 401 (add and delete a car).

## Setup

1. Install **JDK 21** and the browsers you want to test (for local runs).
   Browser drivers are downloaded automatically by Selenium Manager.
2. Create `src/main/resources/config.properties` (it is ignored by git because it has credentials):

   ```properties
   baseUrl=https://<site-under-test>
   apiUrl=https://<rest-backend-of-the-site>
   browser=chrome
   # empty = run locally; http://localhost:4444 = run on Selenium Grid
   gridUrl=
   headless=false
   # an existing user of the site
   login=your_user@example.com
   password=YourPassword1!
   ```

## Configuration

Settings are read by `ConfigReader`. A value given on the command line with `-D`
wins over `config.properties`:

| Key        | Meaning                                         |
|------------|-------------------------------------------------|
| `browser`  | `chrome`, `firefox` or `edge`                   |
| `gridUrl`  | Selenium Grid address; empty = local browsers   |
| `baseUrl`  | site under test                                 |
| `headless` | `true` = run browsers without a window          |
| `login`, `password` | an existing user of the site           |
| `apiUrl`   | REST backend of the site, used by the API tests |

In the cross-browser suites the browser comes from the suite file (`<parameter name="browser">`).

## Running tests

On Windows use `.\gradlew`, on Linux/macOS `./gradlew`.

| Command                                  | What runs                                     |
|------------------------------------------|-----------------------------------------------|
| `.\gradlew test`                         | all UI tests in one browser (Chrome by default) and all API tests, test classes in parallel |
| `.\gradlew smoketests`                   | smoke, one browser                            |
| `.\gradlew regressiontests`              | smoke + regression, one browser               |
| `.\gradlew crossbrowser`                 | smoke in Chrome, Firefox and Edge in parallel |
| `.\gradlew crossbrowser_regression`      | everything in 3 browsers in parallel          |
| `.\gradlew apitests`                     | API tests (no browser and no Grid needed)     |

Examples:

```bash
.\gradlew clean smoketests -Dbrowser=firefox -Dheadless=true
```

```bash
.\gradlew clean crossbrowser_regression -DgridUrl=http://localhost:4444
```

## Selenium Grid

To run tests on the Grid you need a **server (or any computer) with Docker and Docker Compose**.
Our setup: a cloud VM with Ubuntu 24.04, 4 vCPU, 8 GB RAM; the server firewall allows only SSH (port 22).
Plan about 1 CPU core and 1–2 GB RAM per browser running at the same time.

`docker-compose.yml` starts a hub and one node per browser (Chrome, Firefox, Edge,
1 session each). Image versions match `selenium-java` in `build.gradle`.
Docker Compose creates the Docker network for these containers automatically: the nodes find
the hub by its service name `selenium-hub` and register through the event bus (ports 4442/4443
inside the network). No manual network setup is needed.

Copy `docker-compose.yml` to the server and start the Grid:

```bash
docker compose up -d
```

```bash
docker compose ps
```

Grid port 4444 is published on `127.0.0.1` only: the grid has no password and must not be
open to the internet. Connect from your computer through an SSH tunnel and keep it open
while tests run:

```bash
ssh -N -L 4444:localhost:4444 root@<server-ip>
```

Then:

- Grid UI: http://localhost:4444/ui/ (with the slash at the end)
- run tests with `-DgridUrl=http://localhost:4444`

Every Grid session is named after the test (`ClassName.testName`), so it is easy to find in the Grid UI.

## Reports and logs

**Example report:** [Allure report of a cross-browser regression run on Selenium Grid](https://efimova1980.github.io/Carsharing_crossbrowser/allure-report/)
(Chrome, Firefox, Edge; screenshots are not included).

| What         | Where                                                        |
|--------------|--------------------------------------------------------------|
| Allure report | `.\gradlew allureServe` (opens in the browser) or `.\gradlew allureReport` → `build/reports/allure-report/allureReport/index.html` |
| Allure results | `build/allure-results`                                     |
| Screenshots  | `build/screenshots` (failed tests only; also attached to Allure) |
| Logs         | `build/logs` (common log and one log per browser)            |
| Gradle report | `build/reports/tests`                                       |

In Allure, tests are grouped by browser (Suites) and by Epic / Feature / Story (Behaviors);
failed tests are split into **Product defects** (assertion failed) and **Test defects** (errors in the test or environment).
