package com.example.Event.Management.Platform.service.notification;

import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class MailTemplateService {

    public String getUserWelcomeMessage(String username) {
        return """
            Welcome to EventHub!

            Hello %s,

            Your account has been successfully created.

            As a User, you can:
            • Browse upcoming events
            • Register for events
            • Keep track of your upcoming activities

            Start exploring and discover events that match your interests.

            Thank you for joining EventHub!

            Best regards,
            The EventHub Team
            """
                .formatted(username);
    }

    public String getOrganizerPromotionMessage(String username) {
        return """
            Organizer Access Granted

            Hello %s,

            Congratulations!

            Your EventHub account has been upgraded to Organizer status.

            You can now:
            • Create and publish events
            • Manage your events
            • Monitor registrations
            • Update event information

            We look forward to seeing the experiences and events you create for the community.

            Best regards,
            The EventHub Team
            """
                .formatted(username);
    }

    public String getAdminPromotionMessage(String username) {
        return """
            Administrator Access Granted

            Hello %s,

            Your EventHub account has been granted Administrator privileges.

            As an Administrator, you now have access to:
            • User management tools
            • Organizer management features
            • Event moderation capabilities
            • Administrative platform controls

            Please use these privileges responsibly to help maintain a safe and enjoyable environment for all users.

            Thank you for supporting the EventHub platform.

            Best regards,
            The EventHub Team
            """
                .formatted(username);
    }

    public String getEventCreatedMessage(User user, Event event) {
        return """
            Event Successfully Created

            Hello %s,

            Your event has been successfully created and is now available on EventHub.

            Event Details:
            ----------------------------------------
            Name: %s
            Description: %s
            Category: %s
            Date: %s
            Maximum Participants: %d
            ----------------------------------------

            Location:
            %s %d, %s, %s

            What’s next?
            • You can update event details anytime
            • Track participant registrations
            • Manage event capacity and visibility

            Best regards,
            The EventHub Team
            """
                .formatted(
                        user.getName(),
                        event.getName(),
                        event.getDescription(),
                        event.getEventCategory(),
                        event.getDate(),
                        event.getMaxParticipants(),
                        event.getLocation().getStreetName(),
                        event.getLocation().getNumber(),
                        event.getLocation().getCity(),
                        event.getLocation().getZipCode()
                );
    }

    public String getBookingCreatedMessage(Booking booking) {
        return """
            Booking Successfully Created

            Hello %s,

            Your booking request has been successfully created and is now being processed.

            Booking Details:
            ----------------------------------------
            Event: %s
            Date: %s
            Location: %s
            Ticket Type: %s
            Price: %s
            Status: %s
            ----------------------------------------

            What happens next?
            • Your booking is currently under review (ON_HOLD)
            • You will receive a confirmation email once it is approved
            • If no action is taken, the booking may expire after a limited time

            Please make sure to check your email for updates regarding your booking status.

            Best regards,
            The EventHub Team
            """
                .formatted(
                        booking.getUser().getName(),
                        booking.getEvent().getName(),
                        booking.getEvent().getDate().toString(),
                        booking.getEvent().getLocation().getCity() + ", " +
                                booking.getEvent().getLocation().getStreetName() + " " +
                                booking.getEvent().getLocation().getNumber(),
                        booking.getTicket().getType().toString(),
                        booking.getTicket().getPrice().toString(),
                        booking.getStatus().toString()
                );
    }

    public String getBookingConfirmedMessage(Booking booking) {
        return """
            Booking Confirmed

            Hello %s,

            Great news! Your booking has been confirmed successfully.

            Booking Details:
            ----------------------------------------
            Event: %s
            Date: %s
            Location: %s
            Ticket Type: %s
            Price: %s
            Status: %s
            ----------------------------------------

            What this means:
            • Your spot at the event is now guaranteed
            • Please arrive on time at the event location
            • Keep this email for your records if needed

            We are excited to welcome you to the event!

            Best regards,
            The EventHub Team
            """
                .formatted(
                        booking.getUser().getName(),
                        booking.getEvent().getName(),
                        booking.getEvent().getDate().toString(),
                        booking.getEvent().getLocation().getCity() + ", " +
                                booking.getEvent().getLocation().getStreetName() + " " +
                                booking.getEvent().getLocation().getNumber(),
                        booking.getTicket().getType().toString(),
                        booking.getTicket().getPrice().toString(),
                        booking.getStatus().toString()
                );
    }

    public String getBookingCancelledByUserMessage(Booking booking) {
        return """
            Booking Cancelled

            Hello %s,

            Your booking has been successfully cancelled as requested.

            Booking Details:
            ----------------------------------------
            Event: %s
            Date: %s
            Ticket Type: %s
            ----------------------------------------

            You can always create a new booking if spots are still available.

            We hope to see you at future events!

            Best regards,
            The EventHub Team
            """
                .formatted(booking.getUser().getName(),
                        booking.getEvent().getName(),
                        booking.getEvent().getDate(),
                        booking.getTicket().getType());
    }

    public String getBookingAutoCancelledMessage(Booking booking) {
        return """
            Booking Expired & Cancelled

            Hello %s,

            Your booking for the event below has been automatically cancelled because it was not confirmed within 24 hours.

            Booking Details:
            ----------------------------------------
            Event: %s
            Date: %s
            Ticket Type: %s
            ----------------------------------------

            Reason:
            • Booking was not confirmed in time (24-hour limit exceeded)

            You can create a new booking if the event is still available.

            Best regards,
            The EventHub Team
            """
                .formatted(booking.getUser().getName(),
                        booking.getEvent().getName(),
                        booking.getEvent().getDate(),
                        booking.getTicket().getType());
    }
}
