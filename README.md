# TicketingService

**Bulid Instructions**
- JDK 1.8 is required to build
- mvn install package
- java -jar .\target\TicketingService-0.0.1-SNAPSHOT.jar
- Open browser to http://localhost:8080

**Points to Note**
- There is no concept of seat nos , only tickets are booked for the general seat level
- all rest calls support both GET/POST currently.
- Using H2 in memory sql database
- There is no DTO layer created for keeping the code simple as REST API is not fully functional
- Uses Spring boot for prototyping and embedded tomcat

**Check Seats Availabilty**
- http://localhost:8080/seats/available
- http://localhost:8080/seats/available/1 ( by level )

**Book Seat**
- http://localhost:8080/bookseats?email=vin@test.com&numSeats=1600&minLevel=1&maxLevel=4
- http://localhost:8080/bookseats?email=vin@test.com&numSeats=60
- http://localhost:8080/bookseats?email=vin@test.com&numSeats=1600&minLevel=2
- etc..

**Reserve Seats**
- http://localhost:8080/reserve/<seatHoldId>?email=<customerEmail>
- http://localhost:8080/reserve/1?email=vin@test.com

