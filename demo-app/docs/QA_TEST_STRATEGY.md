**Test Strategy - Chubb Claims Management Application**



Highest-Risk Areas



After reviewing the source code, I identified these areas as the main risks:



**Claim business rules** — claim validation and status transitions.

**Authentication and authorization** — different permissions for claimant and admin users.

**Service communication** — communication between the frontend, BFF, Claims Service, database and Kafka.



**Test Focus**



***Unit Tests***

I will test the main claim business rules:



* Creating a valid claim
* Claim validation
* Claim amount validation
* Incident date validation
* Valid claim status transition
* Invalid claim status transition



***Component Tests***

I will test the main behaviour of the claim form:



* User can enter claim information
* Validation is shown for invalid input
* User can submit a valid claim



***Integration Tests***

I will focus on the main claim flow and one messaging scenario:



* Submit a claim and verify it is saved successfully
* Retrieve the submitted claim
* Verify that a claim event is produced through Kafka



**Not Covered**

I will not aim for exhaustive coverage. The following will have limited or no coverage:



All API endpoints

All frontend components

Full browser/device compatibility

Extensive performance testing

All Kafka scenarios



**Issues Found**

1\. Signup Failure

New user signup failed. The BFF received 401 Unauthorized from the Keycloak Admin API and the UI displayed Signup failed.



2\. CDC / Debezium Configuration

The application has cdc-enabled: true, but the provided Docker Compose setup does not contain a Debezium/Kafka Connect service. No PostgreSQL publication was found during the initial check.



This may affect the claim event flow and should be investigated further.

