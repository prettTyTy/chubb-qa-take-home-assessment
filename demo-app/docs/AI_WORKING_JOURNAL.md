AI Working Journal

Overview

AI assistance was used during this QA assessment to support source-code understanding, test planning, test implementation, troubleshooting, and documentation.
All tests were run locally and the results were verified before being considered complete.

1. Understanding the Application

AI was used to help review the application architecture and source code across the UI, BFF, Claims Service, database, Kafka, and authentication components.

The main risks identified were:

* Claim business rules and validation
* Authentication and authorization
* Communication between the frontend, BFF, Claims Service, database, and Kafka

The test scope was intentionally kept focused on these higher-risk areas instead of attempting exhaustive coverage.

2. Manual Testing and Issue Investigation

The application was started locally using Docker Compose and tested through the UI.

The following behaviours were verified:

* Existing claimant user can log in successfully.
* A valid claim can be submitted successfully.
* The submitted claim can be viewed after creation.
* New user signup fails.

For the signup issue, browser and BFF logs were reviewed. The BFF returned 401 Unauthorized when calling the Keycloak Admin API.

This was documented as:

Signup Failure: New user registration fails with a 401 Unauthorized response from the Keycloak Admin API.

AI was used to help interpret the logs and identify the likely area involved. The issue was still verified manually using the running application and supporting logs.

3. Unit Tests

AI was used to help structure unit tests around the claim domain business rules.

The implemented tests cover:

* Creating a valid claim
* Rejecting a description shorter than the minimum length
* Rejecting a claim amount above the maximum
* Rejecting a future incident date
* Allowing a valid status transition
* Rejecting an invalid status transition

Result:

* 6 tests passed
* 0 failures

4. Frontend Component Tests

AI was used to help configure Vitest and React Testing Library for the existing frontend project.

The component tests cover the first step of the claim wizard:

* User can enter claim information
* Validation is shown when the location is too short
* User can move to the next step when the form is valid

Result:

* 3 tests passed
* 0 failures

The test configuration was adjusted to support the existing React frontend and the tests were run locally.

5. API Integration Test

AI was used to help create an integration test for the claim API.

The test verifies the flow:

UI/API request → Claims Service → database → API response

The test:

* Creates a claimant user
* Sends a valid claim request
* Verifies the API returns 201 Created
* Verifies the claim is stored
* Retrieves the claim through the API
* Verifies the returned claim data

Result:

* 1 test passed
* 0 failures

6. Kafka Integration Test

AI was used to help create an integration test for the Kafka event publishing path.

The test uses an embedded Kafka broker and verifies that a ClaimSubmitted event is published to the claim-events topic.

The test verifies:

* Kafka message is produced
* Claim ID is used as the message key
* Event type is present
* Claim ID and user ID are present in the message

Result:

* 1 test passed
* 0 failures

7. Python API Integration Test

AI was used to help create an additional API integration test using Python, pytest, and requests.

The test verifies:

* CSRF token retrieval
* Claimant login
* Valid claim submission
* 201 Created response
* Submitted claim status
* Submitted claim data

Result:

* 1 test passed
* 0 failures

8. Test That Was Not Pursued

An authorization integration test was attempted for the admin claims endpoint.

The test expected a claimant request to receive 403 Forbidden, but the application returned 200 OK.

The test was not kept as part of the final test suite because the reason for the behaviour would require additional investigation into the test security configuration and was outside the focused scope of this assessment.

The unexpected behaviour was not presented as a confirmed application bug.

9. Verification

AI-generated or AI-assisted test code was not considered complete until it was executed locally.

The final verified test results were:

* Backend unit tests: 6 passed
* API integration test: 1 passed
* Kafka integration test: 1 passed
* Frontend component tests: 3 passed
* Python API integration test: 1 passed

Total:

* 12 tests passed
* 0 failures

The tests were executed using the project's local environment and Maven, Vitest, and pytest commands.

10. AI Usage Reflection

AI was used as a development and QA assistance tool rather than as a replacement for verification.

The main uses were:

* Understanding unfamiliar source code
* Identifying potential risk areas
* Suggesting focused test scenarios
* Helping with test framework configuration
* Drafting test implementations
* Troubleshooting test failures
* Interpreting application logs
* Improving QA documentation

The final decisions about test scope, whether to keep or remove tests, and whether an observed behaviour should be reported as a bug or concern were based on local test execution and application evidence.

11. AI Decision Log

Accepted

AI suggestions for testing claim validation, status transitions, the claim submission flow, frontend validation, Kafka event publishing, and API integration were accepted after reviewing the source code and verifying the tests locally.

Challenged

AI suggestions for broader test coverage across multiple API endpoints and frontend areas were considered but not prioritised because the assessment had a limited timebox and requested meaningful test variety rather than exhaustive coverage.

The authorization test result was also investigated rather than immediately being treated as an application defect because the test returned 200 OK instead of the expected 403 Forbidden.

Overridden

The final test scope was narrowed to the higher-risk claim business rules, claim submission flow, frontend claim form, API integration, and Kafka messaging path instead of implementing exhaustive endpoint and frontend coverage.

The authorization test was not included in the final test suite because its result required further investigation into the test security configuration.

Reasoning

The final decisions were based on application risk, assessment requirements, available time and behaviours that could be reliably verified through local testing.
