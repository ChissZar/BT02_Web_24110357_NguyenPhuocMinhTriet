package vn.iotstar.service;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class OtpMailer {
    private String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException("Thiếu cấu hình " + key);
        return value;
    }

    public void checkConfiguration() {
        required("SMTP_HOST"); required("SMTP_USER"); required("SMTP_PASSWORD"); required("SMTP_FROM");
    }

    public void send(String email, String otp, String purpose) {
        checkConfiguration();
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", required("SMTP_HOST"));
        properties.setProperty("mail.smtp.port", System.getenv().getOrDefault("SMTP_PORT", "587"));
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.ssl.checkserveridentity", "true");
        properties.setProperty("mail.smtp.connectiontimeout", "10000");
        properties.setProperty("mail.smtp.timeout", "10000");
        properties.setProperty("mail.smtp.writetimeout", "10000");
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(required("SMTP_USER"), required("SMTP_PASSWORD"));
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(required("SMTP_FROM")));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email, true));
            message.setSubject("ACTIVATE".equals(purpose) ? "Kích hoạt tài khoản" : "Xác nhận đặt lại mật khẩu", "UTF-8");
            message.setText("Mã OTP của bạn: " + otp + "\nMã có hiệu lực 10 phút và chỉ sử dụng một lần."
                    + "\nKhông chia sẻ mã này. Nếu không yêu cầu, hãy bỏ qua email.", "UTF-8");
            Transport.send(message);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Không gửi được email. Vui lòng thử gửi lại mã sau 60 giây.");
        }
    }
}
