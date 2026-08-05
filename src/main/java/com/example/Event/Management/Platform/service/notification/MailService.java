package com.example.Event.Management.Platform.service.notification;

import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final MailTemplateService mailTemplateService;

    public void sendWelcomeEmail(String to, String username) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Welcome to EventHub");
        message.setText(mailTemplateService.getUserWelcomeMessage(username));

        mailSender.send(message);
    }

    public void sendRoleChangeEmail(String to, String username, @NotNull Role role){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Role change");

        String text = switch (role){
            case ROLE_ORGANIZER -> mailTemplateService.getOrganizerPromotionMessage(username);
            case ROLE_ADMIN -> mailTemplateService.getAdminPromotionMessage(username);
            default -> throw new IllegalArgumentException(
                    "Role cannot receive promotion email: " + role
            );
        };

        message.setText(text);

        mailSender.send(message);
    }

    public void sendEventCreatedMail(@NotNull User user, Event event){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(user.getEmail());
        message.setSubject("New event created");
        message.setText(mailTemplateService.getEventCreatedMessage(user, event));

        mailSender.send(message);
    }

    public void sendBookingCreatedMail(@NotNull Booking booking){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(booking.getUser().getEmail());
        message.setSubject("Booking Created Successfully");

        message.setText(mailTemplateService.getBookingCreatedMessage(booking));

        mailSender.send(message);
    }

    public void sendBookingConfirmedMail(@NotNull Booking booking){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(booking.getUser().getEmail());
        message.setSubject("Booking Confirmed");

        message.setText(mailTemplateService.getBookingConfirmedMessage(booking));

        mailSender.send(message);

    }

    public void sendBookingCancelledByUserMail(@NotNull Booking booking) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(booking.getUser().getEmail());
        message.setSubject("Booking Cancelled");

        message.setText(
                mailTemplateService.getBookingCancelledByUserMessage(booking)
        );

        mailSender.send(message);
    }

    public void sendBookingAutoCancelledMail(@NotNull Booking booking){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(booking.getUser().getEmail());
        message.setSubject("Booking Cancelled");

        message.setText(
                mailTemplateService.getBookingAutoCancelledMessage(booking)
        );

        mailSender.send(message);
    }
}
