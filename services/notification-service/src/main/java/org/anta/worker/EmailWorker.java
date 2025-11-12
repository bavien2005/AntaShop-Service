package org.anta.worker;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.anta.dto.request.NotificationEmailRequest;
import org.anta.entity.NotificationRequestEntity;
import org.anta.repository.NotificationRequestRepository;
import org.anta.service.TemplateRenderer;
import org.anta.util.JsonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailWorker {

    private final NotificationRequestRepository repo;
    private final JavaMailSender mailSender;
    private final TemplateRenderer templateRenderer;

    @RabbitListener(queues = "notifications.email.queue")
    public void handle(String requestId) {
        Optional<NotificationRequestEntity> opt = repo.findById(requestId);
        if (opt.isEmpty()) return;
        NotificationRequestEntity entity = opt.get();

        if ("SENT".equals(entity.getStatus())) return;

        try {
            entity.setStatus("PROCESSING");
            repo.save(entity);

            NotificationEmailRequest req = JsonUtil.fromJson(entity.getPayload(), new TypeReference<NotificationEmailRequest>(){});

            String html;

            if (req.getRawHtml() != null && !req.getRawHtml().isBlank()) {
                html = req.getRawHtml();
            }
            else if (req.getBody() != null && !req.getBody().isBlank()) {
                html = req.getBody().replace("\n", "<br>");
            }
            else {
                html = templateRenderer.render(req.getTemplateId(), req.getTemplateData());
            }


            sendHtmlEmail(req.getTo(), req.getSubject(), html);

            entity.setStatus("SENT");
            entity.setAttempts(entity.getAttempts() + 1);
            entity.setUpdatedAt(LocalDateTime.now());
            repo.save(entity);

        } catch (Exception ex) {
            int attempts = entity.getAttempts() == null ? 1 : entity.getAttempts() + 1;
            entity.setAttempts(attempts);
            entity.setStatus(attempts >= 3 ? "FAILED" : "PENDING");
            entity.setUpdatedAt(LocalDateTime.now());
            repo.save(entity);
        }
    }

    private void sendHtmlEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}

