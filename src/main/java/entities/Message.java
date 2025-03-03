package entities;

import java.time.LocalDateTime;

public class Message {
    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String body;
    private LocalDateTime timestamp;
    private String attachmentPath; // Optional

    // Constructor
    public Message(String senderEmail, String recipientEmail, String subject, String body, String attachmentPath) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.timestamp = LocalDateTime.now(); // Auto-generate timestamp
        this.attachmentPath = attachmentPath;
    }

    // Getters and Setters
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

    // ToString method for debugging
    @Override
    public String toString() {
        return "Message{" +
                "senderEmail='" + senderEmail + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", timestamp=" + timestamp +
                ", attachmentPath='" + attachmentPath + '\'' +
                '}';
    }
}
