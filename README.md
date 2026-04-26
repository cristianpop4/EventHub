# EventHub 🎟️
 
A REST API for event management built with Spring Boot. Organizers can create events and manage tickets, users can search and book tickets.
 
> 🚧 **Status: Active Development**
 
---
 
## Features
 
### Users
- Register and manage user accounts
- Search and filter events by name, city, category and date
- Book tickets for events
- Cancel bookings
### Organizers
- Create and manage events
- Define ticket types with pricing and availability
- View bookings per event
### Events
- Full CRUD operations
- Dynamic search with multiple optional filters
- Automatic location management (create or reuse existing locations)
- Event categories: Music, Tech, Business, Education, Sports, Art, Food, Social
### Tickets & Bookings
- Multiple ticket types per event (VIP, Standard, etc.)
- Real-time availability tracking
- Booking creation with automatic availability decrease
- Booking cancellation with availability restore
---
 
## Tech Stack
 
- Java 21 + Spring Boot 4.0.3
- Spring Data JPA + Hibernate
- PostgreSQL
- Lombok
- Swagger UI
---

## Main Entities
 
- **Person** → base class for `User` and `Organizer` (JOINED inheritance)
- **Event** → created by an Organizer, linked to a Location
- **Ticket** → ticket types per event with availability tracking
- **Booking** → user purchases a ticket for an event
- **Location** → reused across events
---
 
## Roadmap
 
- [ ] Spring Security + JWT
- [ ] Email notifications
- [ ] Location proximity search
---
 
## Author
 
Built by **cristianpop4** as a learning project.
