# Architecture
## Overall
![Overall](doc/images/overallBooking.png "Overall")
* **Booking** - Responsible for process the client booking requests in a clinic for a service
* **Customer** - Responsible for manage the customers information 
* **Services** - Responsible for manage the services available
* **Clinics** - Responsible for manage the clinics information and time slots for the services 
* **Notification** - Responsible for inform the customers about changes and events 

Booking system uses Clinics and customers system to validate customers bookings, 

## Deployment
## Components
## Layers
### Rest
### Service
### Persistence


 
#### Concerns
* Multi-datacenter in high latency
* High-availability
* Observability
* Security
* Test
#REST API 
#### Concerns
* API Definition/OpenApi spec
* CORS
* Error Handling
* Localisation

## Post /bookings
To book a service in a specific time in a clinic
#### Concerns
* Concurrency
* Idempotency
## Get clinic/{id}/bookings
#### Concerns
* Too many elements
* Caching Policy
## probably Get clinic/{id}/bookings/{id}
#### Concerns

## probably Get clinic/{id}/freeslots/yyyy-mm-dd
#### Concerns


#REST API
* mock api

