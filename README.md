## Backlog

## Note
 The decision to Dockerize only the backend and database was made because creating a Docker image for the Swing-based desktop UI would require extensive host machine configuration, which is highly dependent on the operating system. Instead, the UI desktop project can be run directly on the host machine, while the backend and database are containerized for consistency and ease of deployment.

## Features
- [x] Implement CRUD operations for HR Personnel
- [x] Implement search and filtering functionality
- [x] Generate basic reports
- [x] Implement JWT authentication and authorization
  - [x] User authentication with JWT
  - [x] Role-based authorization (HR, Manager, Admin)
  - [x] Token management (generation, validation, expiration)
- [x] Audit Trail
  - [x] Log changes to employee records
  - [x] Track who made changes and when
- [x] Validation Rules
  - [x] Ensure valid email format
  - [x] Ensure unique Employee ID
-create docker image for testing app locally
  

## Bugs
- [ ]  work with Mysql instead Oracle Database
- [ ]  Unit tests failed to run due to dependecies conflict
      

## Tests
- 
  - [x] Test CRUD operations
  - [x] Test search and filtering
  - [x] Test reporting functionality
  - [x] Test JWT authentication and authorization
- [x] Create Postman Collection for API testing
  - [x] Test all endpoints
  - [x] Validate responses and error handling

## Documentation
- [x] configure Swagger UI to map archtecture of API's : */swagger-ui/index.html
- [x] Create README.md
- [x] create an Postman api collection here the lien :https://drive.google.com/drive/folders/1t_NmaMsOHstPvZgNwYGDodelWwFVKVTa?usp=sharing
  
