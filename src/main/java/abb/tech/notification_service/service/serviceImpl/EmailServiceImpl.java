package abb.tech.notification_service.service.serviceImpl;

import abb.tech.notification_service.dto.TicketCreatedEvent;
import abb.tech.notification_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendTicketWithAttachment(TicketCreatedEvent event) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(event.getUserEmail());
        helper.setSubject("Sifarişiniz təsdiqləndi! Sifariş #" + event.getOrderId());

        // Mobil tətbiq qeydi çıxarılmış, biletlərin e-maildə olduğunu deyən mətn
        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                "<h2>Bizdən bilet əldə etdiyiniz üçün təşəkkür edirik!</h2>" +
                "<p>Sifarişiniz uğurla tamamlandı. Seçdiyiniz tədbirdə iştirak etməyə bir addım daha yaxınsınız.</p>" +

                "<div style='background-color: #f9f9f9; padding: 15px; border-left: 5px solid #28a745;'>" +
                "<p><b>Biletləriniz bu e-mailə əlavə edilmişdir.</b><br>" +
                "</div>" +

                "<p style='color: #d9534f;'><b>VACİB QEYD:</b> Hər bir bilet üzərindəki QR kod unikaldır və yalnız bir dəfə giriş üçün keçərlidir. Biletlərinizi kənar şəxslərə göndərməməyiniz xahiş olunur.</p>" +

                "<br>" +
                "<p>Xoş istirahətlər arzulayırıq!<br><b>Hörmətlə, ETicket Komandası</b></p>" +
                "</body>" +
                "</html>";

        helper.setText(htmlContent, true);

        if (event.getTickets() != null) {
            for (TicketCreatedEvent.TicketDetails ticket : event.getTickets()) {
                try {
                    byte[] pdfBytes = Base64.getDecoder().decode(ticket.getPdfBase64());
                    String fileName = "Bilet_" + ticket.getTicketNumber() + ".pdf";
                    helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));
                } catch (Exception e) {
                    System.err.println("Bilet əlavə edilərkən xəta: " + ticket.getTicketNumber());
                }
            }
        }

        mailSender.send(message);
    }
}