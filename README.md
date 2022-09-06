# Booking
[Achitecture](doc/README.md)
### Build
to build the service in the same directory as this file type
```
gradle clean build
```
### Run
after build, go to directory build/libs
#### Start the service
```
java -DREMOTE_SERVICE_API_KEY=<clinics api key> -DREMOTE_SERVICE_URL=<base url for the remote service> -jar booking-0.0.1-SNAPSHOT.jar
```
#### Use the services
Use the curl commands below or use [booking postman collection](doc/Booking.postman_collection.json)
##### Retrieve the bookings for a clinic
```
curl --location --request GET 'http://localhost:9000/api/clinics/clinic100/bookings' 
```
##### Retrieving Available Slots to book
```
curl --location --request GET 'http://localhost:9000/api/clinics/clinic100/services/ser100/bookings/available/2021-01-26'
```
##### Booking 
```
curl --location --request POST 'http://localhost:9000/api/bookings' \
--header 'Content-Type: application/json' \
--data-raw '{
"clinicId": "clinic100",
"customerId": "ab123",
"serviceId": "ser100",
"date": "2021-01-25",
"startTime": "T10:30:00"
}'
```


Another Chaging for twilio.
