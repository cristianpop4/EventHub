package com.example.Event.Management.Platform.unitTests;

import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.service.notification.MailService;
import com.example.Event.Management.Platform.service.notification.MailTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MailServiceTests {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailTemplateService mailTemplateService;

    @InjectMocks
    private MailService mailService;

    private User user;
    private Event event;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "Cristian",
                "cristian@example.com",
                "encodedPassword",
                Role.ROLE_USER
        );

        event = new Event(
                1L,
                "Music Festival",
                "A great festival",
                null,
                null,
                null,
                200,
                user,
                null,
                null
        );

        booking = new Booking(
                1L,
                user,
                event,
                null,
                null,
                null
        );
    }

    @Test
    void sendWelcomeEmail_ShouldSendCorrectMessage() {
        when(mailTemplateService.getUserWelcomeMessage("Cristian"))
                .thenReturn("Welcome Cristian!");

        mailService.sendWelcomeEmail("cristian@example.com", "Cristian");

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals("cristian@example.com") && "Welcome to EventHub".equals(msg.getSubject()) && "Welcome Cristian!".equals(msg.getText())
        ));
    }

    @Test
    void sendRoleChangeEmail_ShouldSendOrganizerMessage_WhenRoleIsOrganizer() {
        when(mailTemplateService.getOrganizerPromotionMessage("Cristian"))
                .thenReturn("You're now an organizer!");

        mailService.sendRoleChangeEmail("cristian@example.com", "Cristian", Role.ROLE_ORGANIZER);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                "You're now an organizer!".equals(msg.getText()) &&
                        "Role change".equals(msg.getSubject())
        ));
        verify(mailTemplateService, never()).getAdminPromotionMessage(any());
    }

    @Test
    void sendRoleChangeEmail_ShouldSendAdminMessage_WhenRoleIsAdmin() {
        when(mailTemplateService.getAdminPromotionMessage("Cristian"))
                .thenReturn("You're now an admin!");

        mailService.sendRoleChangeEmail("cristian@example.com", "Cristian", Role.ROLE_ADMIN);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                "You're now an admin!".equals(msg.getText())
        ));
        verify(mailTemplateService, never()).getOrganizerPromotionMessage(any());
    }

    @Test
    void sendRoleChangeEmail_ShouldThrow_WhenRoleIsNotPromotable() {
        assertThrows(IllegalArgumentException.class,
                () -> mailService.sendRoleChangeEmail("cristian@example.com", "Cristian", Role.ROLE_USER));

        verifyNoInteractions(mailSender);
    }

    @Test
    void sendEventCreatedMail_ShouldSendCorrectMessage() {
        when(mailTemplateService.getEventCreatedMessage(user, event))
                .thenReturn("Your event was created!");

        mailService.sendEventCreatedMail(user, event);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals(user.getEmail()) &&
                        "New event created".equals(msg.getSubject()) &&
                        "Your event was created!".equals(msg.getText())
        ));
    }

    @Test
    void sendBookingCreatedMail_ShouldSendCorrectMessage() {
        when(mailTemplateService.getBookingCreatedMessage(booking))
                .thenReturn("Booking created!");

        mailService.sendBookingCreatedMail(booking);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals(user.getEmail()) &&
                        "Booking Created Successfully".equals(msg.getSubject()) &&
                        "Booking created!".equals(msg.getText())
        ));
    }

    @Test
    void sendBookingConfirmedMail_ShouldSendCorrectMessage() {
        when(mailTemplateService.getBookingConfirmedMessage(booking))
                .thenReturn("Booking confirmed!");

        mailService.sendBookingConfirmedMail(booking);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals(user.getEmail()) &&
                        "Booking Confirmed".equals(msg.getSubject()) &&
                        "Booking confirmed!".equals(msg.getText())
        ));
    }

    @Test
    void sendBookingCancelledByUserMail_ShouldSendCorrectMessage() {
        when(mailTemplateService.getBookingCancelledByUserMessage(booking))
                .thenReturn("You cancelled your booking.");

        mailService.sendBookingCancelledByUserMail(booking);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals(user.getEmail()) &&
                        "Booking Cancelled".equals(msg.getSubject()) &&
                        "You cancelled your booking.".equals(msg.getText())
        ));
    }

    @Test
    void sendBookingAutoCancelledMail_ShouldSendCorrectMessage() {
        when(mailTemplateService.getBookingAutoCancelledMessage(booking))
                .thenReturn("Your booking was auto-cancelled.");

        mailService.sendBookingAutoCancelledMail(booking);

        verify(mailSender).send(argThat((SimpleMailMessage msg) ->
                msg.getTo()[0].equals(user.getEmail()) &&
                        "Booking Cancelled".equals(msg.getSubject()) &&
                        "Your booking was auto-cancelled.".equals(msg.getText())
        ));
    }
}
