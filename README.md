# EventHub 🎟️

A REST API for event management built with Spring Boot.
Users can search and book tickets, while admins and
organizers manage events through role-based access control.

> 🚧 **Status: Active Development**

---

## Features

### Authentication & Security
- JWT-based authentication
- Role-based access control (ROLE_ADMIN, ROLE_USER, ROLE_ORGANIZER)
- Secured endpoints with @PreAuthorize annotations
- Global exception handling with custom error responses

### Users
- Register and manage user accounts
- Search and filter events by name, city, category and date
- Book tickets for events
- Cancel bookings

### Organizers
- Create and manage events (via ROLE_ORGANIZER)
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
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Lombok
- Swagger UI

---

## Main Entities

- **User** → base entity with assigned roles (ADMIN, USER, ORGANIZER)
- **Event** → created by users with ROLE_ORGANIZER, linked to a Location
- **Ticket** → ticket types per event with availability tracking
- **Booking** → user purchases a ticket for an event
- **Location** → reused across events

---

## Roadmap

- [x] Spring Security + JWT
- [ ] Email notifications
- [ ] QR code generation for bookings
- [ ] Location proximity search

---

## Author

Built by **cristianpop4** as a learning project.