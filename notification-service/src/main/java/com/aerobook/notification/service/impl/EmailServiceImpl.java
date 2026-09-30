package com.aerobook.notification.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aerobook.notification.service.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:notifications@aerobook.com}")
    private String fromEmail;

    @Value("${aerobook.mail.enabled:false}")
    private boolean mailEnabled;

    @Async
    @Override
    public void sendBookingConfirmationEmail(String toEmail, String pnr, String flightNumber,
                                             String source, String destination, String departureTime,
                                             String passengerName, String seatNumber, Double totalFare) {
        String subject = "✈️ Booking Confirmed: " + flightNumber + " (" + source + " → " + destination + ") [PNR: " + pnr + "]";
        String html = buildBookingEmailHtml(pnr, flightNumber, source, destination, departureTime, passengerName, seatNumber, totalFare);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    @Override
    public void sendCheckInConfirmationEmail(String toEmail, String pnr, String flightNumber,
                                             String passengerName, String seatNumber, String boardingPassNumber,
                                             String gate, String terminal) {
        String subject = "🎫 Your Digital Boarding Pass: Flight " + flightNumber + " [Seat " + seatNumber + "]";
        String html = buildCheckInEmailHtml(pnr, flightNumber, passengerName, seatNumber, boardingPassNumber, gate, terminal);
        sendHtmlEmail(toEmail, subject, html);
    }

    @Async
    @Override
    public void sendFlightAlertEmail(String toEmail, String flightNumber, String subject, String message) {
        String fullSubject = "⚠️ Flight Advisory: " + flightNumber + " - " + subject;
        String html = buildAlertEmailHtml(flightNumber, subject, message);
        sendHtmlEmail(toEmail, fullSubject, html);
    }

    @Async
    @Override
    public void sendRefundConfirmationEmail(String toEmail, String pnr, String refundId, Double amount) {
        String subject = "💸 Refund Processed: PNR " + pnr + " [₹" + String.format("%.2f", amount) + "]";
        String html = buildRefundEmailHtml(pnr, refundId, amount);
        sendHtmlEmail(toEmail, subject, html);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (to == null || to.isBlank()) {
            to = "passenger@aerobook.com";
        }

        log.info("Dispatching HTML email to: '{}' | Subject: '{}'", to, subject);

        if (!mailEnabled || mailSender == null) {
            log.info("Live SMTP is disabled or unconfigured (aerobook.mail.enabled=false). Email logged successfully.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "AeroBook Commercial Airways");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.warn("Failed to dispatch SMTP email to {}: {}. Logged for audit.", to, e.getMessage());
        }
    }

    private String buildBookingEmailHtml(String pnr, String flightNumber, String source, String destination,
                                         String departureTime, String passengerName, String seatNumber, Double totalFare) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <style>
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #0b0f19; margin: 0; padding: 24px; color: #ffffff; }
            .container { max-width: 600px; margin: 0 auto; background: #131826; border-radius: 20px; overflow: hidden; border: 1px solid #1f293d; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); }
            .header { background: linear-gradient(135deg, #635bff 0%, #4338ca 100%); padding: 32px 28px; text-align: left; }
            .brand { font-size: 20px; font-weight: 800; letter-spacing: -0.5px; color: #ffffff; }
            .title { font-size: 26px; font-weight: 800; margin-top: 10px; color: #ffffff; }
            .pnr-box { display: inline-block; background: rgba(255,255,255,0.15); backdrop-filter: blur(8px); padding: 6px 14px; border-radius: 8px; font-family: monospace; font-size: 15px; font-weight: 700; margin-top: 12px; }
            .route-card { padding: 28px; background: #182032; border-bottom: 2px dashed #26334d; }
            .route-flex { display: flex; justify-content: space-between; align-items: center; }
            .airport-code { font-size: 38px; font-weight: 900; color: #ffffff; margin: 0; }
            .airport-city { font-size: 13px; color: #94a3b8; margin-top: 2px; }
            .plane-icon { font-size: 26px; color: #635bff; padding: 0 16px; }
            .flight-info { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; padding: 24px 28px; border-bottom: 1px solid #1f293d; }
            .label { font-size: 11px; text-transform: uppercase; letter-spacing: 0.5px; color: #64748b; font-weight: 600; margin-bottom: 4px; }
            .val { font-size: 15px; font-weight: 700; color: #f1f5f9; }
            .val-highlight { color: #635bff; font-weight: 800; }
            .fare-breakdown { padding: 20px 28px; background: #0f1422; display: flex; justify-content: space-between; align-items: center; }
            .barcode { padding: 20px 28px; text-align: center; background: #131826; }
            .barcode-strip { letter-spacing: 4px; font-family: monospace; font-size: 20px; color: #475569; }
            .footer { padding: 20px 28px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #1f293d; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <div class="brand">✈️ AeroBook Airways</div>
              <div class="title">Booking Confirmed!</div>
              <div class="pnr-box">PNR: %s</div>
            </div>
            <div class="route-card">
              <table style="width: 100%%;">
                <tr>
                  <td style="text-align: left;">
                    <h2 class="airport-code">%s</h2>
                    <div class="airport-city">Origin Station</div>
                  </td>
                  <td style="text-align: center;">
                    <div class="plane-icon">✈️ ───────▶</div>
                    <div style="font-size: 12px; color: #94a3b8; font-weight: 600;">%s</div>
                  </td>
                  <td style="text-align: right;">
                    <h2 class="airport-code">%s</h2>
                    <div class="airport-city">Destination</div>
                  </td>
                </tr>
              </table>
            </div>
            <div class="flight-info">
              <div>
                <div class="label">Passenger</div>
                <div class="val">%s</div>
              </div>
              <div>
                <div class="label">Seat Number</div>
                <div class="val val-highlight">%s</div>
              </div>
              <div>
                <div class="label">Departure</div>
                <div class="val">%s</div>
              </div>
            </div>
            <div class="fare-breakdown">
              <div style="color: #94a3b8; font-size: 14px;">Total Fare Paid (All Taxes Inc.)</div>
              <div style="font-size: 22px; font-weight: 900; color: #10b981;">₹%s</div>
            </div>
            <div class="barcode">
              <div class="barcode-strip">||| | |||| | ||||| ||| || | |||| ||</div>
              <div style="font-size: 11px; color: #475569; margin-top: 6px;">ELECTRONIC TICKET CITATION • VALID PHOTO ID REQUIRED AT AIRPORT</div>
            </div>
            <div class="footer">
              Thank you for choosing AeroBook Commercial Airways. Need assistance? Contact support@aerobook.com
            </div>
          </div>
        </body>
        </html>
        """.formatted(
                pnr,
                source != null ? source : "BOM",
                flightNumber != null ? flightNumber : "AB-101",
                destination != null ? destination : "DEL",
                passengerName != null ? passengerName : "Valued Passenger",
                seatNumber != null ? seatNumber : "Assigned at Check-In",
                departureTime != null ? departureTime : "Scheduled",
                totalFare != null ? String.format("%.2f", totalFare) : "0.00"
        );
    }

    private String buildCheckInEmailHtml(String pnr, String flightNumber, String passengerName,
                                         String seatNumber, String boardingPassNumber, String gate, String terminal) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <style>
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #0b0f19; margin: 0; padding: 24px; color: #ffffff; }
            .container { max-width: 600px; margin: 0 auto; background: #131826; border-radius: 20px; overflow: hidden; border: 1px solid #1f293d; }
            .header { background: linear-gradient(135deg, #10b981 0%, #059669 100%); padding: 32px 28px; }
            .badge { display: inline-block; background: rgba(0,0,0,0.2); padding: 6px 14px; border-radius: 8px; font-family: monospace; font-size: 16px; font-weight: 800; color: #fff; }
            .grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; padding: 28px; background: #182032; }
            .stat-box { background: #0f1422; padding: 18px; border-radius: 12px; border: 1px solid #26334d; }
            .stat-label { font-size: 11px; text-transform: uppercase; color: #64748b; font-weight: 700; }
            .stat-val { font-size: 24px; font-weight: 900; color: #635bff; margin-top: 4px; }
            .barcode { padding: 24px; text-align: center; background: #131826; border-top: 2px dashed #26334d; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <div style="font-size: 14px; text-transform: uppercase; letter-spacing: 1px; color: rgba(255,255,255,0.8);">Airport Boarding Authority</div>
              <h1 style="font-size: 26px; font-weight: 900; margin: 8px 0 16px 0;">Boarding Pass Ready</h1>
              <div class="badge">%s</div>
            </div>
            <div class="grid">
              <div class="stat-box">
                <div class="stat-label">Passenger</div>
                <div style="font-size: 16px; font-weight: 700; margin-top: 6px;">%s</div>
                <div style="font-size: 12px; color: #94a3b8; margin-top: 2px;">PNR: %s</div>
              </div>
              <div class="stat-box">
                <div class="stat-label">Allocated Seat</div>
                <div class="stat-val">%s</div>
              </div>
              <div class="stat-box">
                <div class="stat-label">Flight Number</div>
                <div style="font-size: 20px; font-weight: 800; color: #f1f5f9; margin-top: 6px;">%s</div>
              </div>
              <div class="stat-box">
                <div class="stat-label">Terminal / Gate</div>
                <div style="font-size: 20px; font-weight: 800; color: #eab308; margin-top: 6px;">%s • %s</div>
              </div>
            </div>
            <div class="barcode">
              <div style="font-family: monospace; font-size: 22px; letter-spacing: 5px; color: #475569;">|||| || | |||| ||| || | ||| || |||</div>
              <div style="font-size: 11px; color: #64748b; margin-top: 8px;">Present this digital barcode at Security Gate & Aerobridge Scanner</div>
            </div>
          </div>
        </body>
        </html>
        """.formatted(
                boardingPassNumber != null ? boardingPassNumber : "BP-BOARDING-PASS",
                passengerName != null ? passengerName : "Passenger",
                pnr,
                seatNumber != null ? seatNumber : "12A",
                flightNumber != null ? flightNumber : "AB-101",
                terminal != null ? terminal : "T2",
                gate != null ? gate : "Gate 14B"
        );
    }

    private String buildAlertEmailHtml(String flightNumber, String subject, String message) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <style>
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #0b0f19; margin: 0; padding: 24px; color: #ffffff; }
            .container { max-width: 600px; margin: 0 auto; background: #131826; border-radius: 20px; overflow: hidden; border: 1px solid #1f293d; }
            .header { background: #dc2626; padding: 28px; }
            .body { padding: 28px; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <div style="font-size: 12px; text-transform: uppercase; color: rgba(255,255,255,0.8); font-weight: 700;">Operational Flight Alert</div>
              <h2 style="margin: 8px 0 0 0; font-size: 22px; font-weight: 900;">Flight %s: %s</h2>
            </div>
            <div class="body">
              <p style="font-size: 15px; line-height: 1.6; color: #e2e8f0;">%s</p>
              <div style="margin-top: 24px; padding: 14px; background: #1e293b; border-radius: 8px; font-size: 12px; color: #94a3b8;">
                Please verify live terminal displays or contact airport staff for connection inquiries.
              </div>
            </div>
          </div>
        </body>
        </html>
        """.formatted(
                flightNumber != null ? flightNumber : "Schedule",
                subject != null ? subject : "Update",
                message != null ? message : "Flight status has been updated."
        );
    }

    private String buildRefundEmailHtml(String pnr, String refundId, Double amount) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <style>
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #0b0f19; margin: 0; padding: 24px; color: #ffffff; }
            .container { max-width: 600px; margin: 0 auto; background: #131826; border-radius: 20px; overflow: hidden; border: 1px solid #1f293d; }
            .header { background: linear-gradient(135deg, #0284c7 0%, #0369a1 100%); padding: 28px; }
            .body { padding: 28px; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <div style="font-size: 12px; text-transform: uppercase; color: rgba(255,255,255,0.8); font-weight: 700;">Payment & Settlement Division</div>
              <h2 style="margin: 8px 0 0 0; font-size: 22px; font-weight: 900;">Refund Processed Successfully</h2>
            </div>
            <div class="body">
              <p style="font-size: 15px; color: #e2e8f0;">A refund has been credited for your reservation <strong>PNR %s</strong>.</p>
              <div style="background: #0f172a; padding: 20px; border-radius: 12px; margin: 20px 0; border: 1px solid #334155;">
                <div style="font-size: 12px; color: #64748b; text-transform: uppercase;">Refund Amount</div>
                <div style="font-size: 28px; font-weight: 900; color: #38bdf8; margin: 4px 0 12px 0;">₹%s</div>
                <div style="font-size: 12px; color: #94a3b8;">Refund ID: <span style="font-family: monospace; color: #f8fafc;">%s</span></div>
              </div>
              <p style="font-size: 12px; color: #64748b;">The refunded amount will reflect in your source account within 5-7 business banking days.</p>
            </div>
          </div>
        </body>
        </html>
        """.formatted(
                pnr,
                amount != null ? String.format("%.2f", amount) : "0.00",
                refundId != null ? refundId : "N/A"
        );
    }
}
