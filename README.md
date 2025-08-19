## Medical Appointment AMS

A Spring Boot microservices system for managing medical appointments with user management, doctor profiles, appointment workflows, PDF billing, and email notifications. The architecture uses Spring Cloud Config, Eureka Service Discovery, and an API Gateway.

### Architecture

- **Config Service** (`config-service`): Centralized configuration server backed by the `config-repository` directory.
- **Discovery Service** (`discovery-service`): Eureka server for service registry and discovery.
- **Gateway Service** (`gateway-service`): Entry point, enforces JWT authentication and routes to downstream services.
- **User Service** (`user-service`): Authentication, JWT issuance, user CRUD and search (Doctors, Patients).
- **Doctor Service** (`doctor-service`): Doctor profile CRUD, doctor appointment views and actions (status, reports).
- **Patient Service** (`patient-service`): Patient-facing APIs: search doctors, manage own appointments, download billing PDF.
- **Appointment Service** (`appointment-service`): Appointment lifecycle, PDF generation (FreeMarker + iText), and email notifications.
- **Admin Service** (`admin-service`): Admin operations over users: search, retrieve, activate/validate, delete.

### Ports and Service Names

- Config Service: 9999 (`config-service`)
- Discovery Service (Eureka): 8761 (`DiscoveryService`)
- Gateway Service: 8888 (`gateway-service`)
- User Service: 8081 (`user-service`)
- Doctor Service: 8082 (`doctor-service`)
- Admin Service: 8083 (`admin-service`)
- Patient Service: 8084 (`patient-service`)
- Appointment Service: 8085 (`appointment-service`)


### Service Endpoints

- User Service (`:8081`, base `\u0060/api/user\u0060`)
  - `POST /create` Register and returns `{ dto, token , refresh_token}`
  - `POST /login` Authenticate and returns `{ dto, token , refresh_token}`
  - `GET /doctors` List/search doctors (pagination/sorting)
  - `GET /doctor/{id}` Get doctor details
  - `GET /patients` List/search patients (pagination/sorting)
  - `GET /patient/{id}` Get patient details
  - `PUT /update/{id}` Update user
  - `DELETE /delete/{id}` Delete user
  - `PUT /activate/{id}` Activate user

- Doctor Service (`:8082`, base `\u0060/api/doctors\u0060`)
  - `GET /{id}` Get doctor profile
  - `POST /profile/create` Create profile
  - `PUT /profile/update/{id}` Update profile
  - `DELETE /profile/delete/{id}` Delete profile
  - `PUT /profile/activate/{id}` Activate profile
  - `GET /appointments/{doctorId}` List appointments for doctor (paginated)
  - `GET /{doctorId}/appointment/{appointmentId}` Get one appointment
  - `PUT /{appointmentId}/appointment/{doctorId}/change_status` Change appointment status
  - `POST /appointment/{appointmentId}/report/create` Create report
  - `PUT /appointment/{reportId}/report/update` Update report

- Patient Service (`:8084`, base `\u0060/api/patient\u0060`)
  - `GET /doctors` Search doctors
  - `GET /doctor/{id}` Get doctor details
  - `PUT /update/{id}` Update own profile
  - `DELETE /delete/{id}` Delete own profile
  - `PUT /activate/{id}` Activate own profile
  - `GET /{patientId}/appointments` List patient appointments (paginated)
  - `GET /{patientId}/appointment/{appointmentId}` Get an appointment
  - `POST /appointment/create` Create an appointment
  - `DELETE /{patientId}/appointment/{appointmentId}` Cancel an appointment
  - `GET /{patientId}/appointment/{appointmentId}/billing_file` Download billing PDF

- Appointment Service (`:8085`, base `\u0060/api/appointments\u0060`)
  - `GET /patient/{patientId}` List patient appointments
  - `GET /patient/{patientId}/{appointmentId}` Get patient appointment
  - `GET /doctor/{doctorId}` List doctor appointments
  - `GET /doctor/{doctorId}/{appointmentId}` Get doctor appointment
  - `POST /create` Create appointment
  - `PUT /update/{appointmentId}` Update appointment
  - `DELETE /cancel/{appointmentId}/{patientId}` Cancel appointment
  - `GET /billing_file/{appointmentId}/{patientId}` Get billing PDF
  - `PUT /doctor/change_status/{appointmentId}/{doctorId}` Change appointment status
  - `POST /{appointmentId}/report/create` Create report
  - `PUT /{reportId}/report/update` Update report

- Admin Service (`:8083`, base `\u0060/api/admin\u0060`)
  - `GET /users/{role}` List users by role
  - `GET /users/search-doctors` Search doctors (filters + pagination)
  - `GET /users/doctor/{id}` Get doctor by id
  - `GET /users/search-patients` Search patients (filters + pagination)
  - `GET /users/patient/{id}` Get patient by id
  - `DELETE /users/delete/{id}` Delete user
  - `PUT /users/activate/{id}` Validate/activate doctor