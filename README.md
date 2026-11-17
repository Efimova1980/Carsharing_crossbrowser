# Carsharing cross-browser UI and API tests

Automated UI and API tests for a carsharing web app.
The UI tests run in **Chrome, Firefox and Edge**, in parallel, on a **Selenium Grid** in Docker
started by GitHub Actions. The API tests call the site's REST backend directly,
no browser needed. Results are shown in an **Allure** report.

## Tech stack

| Area           | Tools                                                    |
|----------------|----------------------------------------------------------|
| Language       | Java 21                                                  |
| Build          | Gradle 8.14 (wrapper included)                           |
| Browser driver | Selenium 4.49 (WebDriver, RemoteWebDriver)               |
| API client     | REST Assured 5.5 + Jackson (requests/responses in the API log) |
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
└── utils/                   # ConfigReader, UserFactory, CarFactory, SearchFactory,
                             # SchemaValidator, listeners, screenshots;
                             # enums/: Fuel, HeaderMenuItem, FooterMenuItem
src/main/resources
└── config.properties        # settings for local runs (not in git; in CI they come from secrets)
src/test/java
├── ui_tests/                # UI test classes
└── api_tests/               # API test classes (no browser)
src/test/resources
├── smoketests.xml               # smoke, one browser
├── regressiontests.xml          # smoke + regression, one browser
├── crossbrowser_smoke.xml       # smoke, 3 browsers in parallel
├── crossbrowser_regression_parallel.xml    # smoke + regression, 3 browsers in parallel
├── crossbrowser_regression_sequential.xml  # the same, browsers one after another
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

| Class                  | What is tested                                             |
|------------------------|------------------------------------------------------------|
| `RegistrationApiTests` | registration: positive, already registered user, blank email, blank password, invalid emails and passwords (data providers) |
| `LoginApiTests`        | login: token in the answer, wrong credentials              |
| `SearchApiTests`       | search: positive, blank city, empty request, end date before start date, the same day, dates in the past |
| `AddCarApiTests`       | adding a car: positive, missing field, no/invalid token     |
| `DeleteCarApiTests`    | deleting a car: positive (checked in the user's car list), no token, not existing car |

How the API tests check an answer:

1. **status code** first, as a hard assert: if it is wrong, the body has another format;
2. **JSON schema** of the body (`utils.SchemaValidator`), once per endpoint + status code;
3. **values** (error text, token, found cars).

Tests create as little data as possible: checks that need a user use one existing test user,
and a car added by a test is deleted after it.

Groups: `smoke`, `regression`, `external` (links to other sites), `api`, `api_smoke`.

> Some tests are red because they catch existing bugs of the site.

## Running on GitHub Actions

The tests run in GitHub Actions: **Actions → Tests → Run workflow**, then choose what to run.
The run is started by hand only (`workflow_dispatch`).

| Option                               | What runs                                                   |
|--------------------------------------|-------------------------------------------------------------|
| `crossbrowser_regression_parallel`   | smoke + regression in Chrome, Firefox and Edge in parallel  |
| `crossbrowser_regression_sequential` | the same, browsers one after another (to compare the time)  |
| `crossbrowser_smoke`                 | smoke in Chrome, Firefox and Edge in parallel               |
| `apitests`                           | API tests, no browser                                       |
| `all`                                | `crossbrowser_regression_parallel` + `apitests`             |

What the workflow does:

1. starts the Selenium Grid from `docker-compose.yml` on the GitHub runner (hub + Chrome, Firefox, Edge);
2. runs the chosen tests; failed tests do not stop the others;
3. builds the Allure report and publishes it to GitHub Pages, even if tests failed.

The site address and the test user are passed from repository secrets
(`BASE_URL`, `API_URL`, `LOGIN`, `PASSWORD`), so they are not in the code, the logs or the report.
Logs and screenshots stay on the runner and are not published.

## Report

**Latest report:** [Allure report of the last GitHub Actions run](https://efimova1980.github.io/Carsharing_crossbrowser/)
(published by the workflow; screenshots and logs are not published).

In Allure, tests are grouped by browser and test class (Suites);
failed tests are split into **Product defects** (assertion failed) and **Test defects** (errors in the test or environment).
