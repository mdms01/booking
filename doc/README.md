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
* Add configuration parameters to allow deployment
## Components
## Layers
* Controller
* Service
* Integration
* Persistence
### Rest
### Service
### Persistence

#### Principles
#### Constraints  

#### Assumptions 
* There is no intersection between time slots
* There is no appointment which start at day X and finishes at day X+1

#### Concerns
* Multi-datacenter in high latency
* High-availability
* Observability
* Security
* Test
* Time zone
* Service Discovery ?
* Concurrency in the booking
* Caching
* change time slots - must be a separated process
* Request traceability


#### Decisions
* Store at GMT +0
* As the concurrency in the booking is hard, a trade off must be put in place, compromisse the schedule flexibility with guaranteed no double bookings
* Caching for Services and clinic timeslots are mandatory to prevent complications on booking
* Use relational database due query capabilities even the book looks like a key value pair
* no impose restrictions when the user can schedule 

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

