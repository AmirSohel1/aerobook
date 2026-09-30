package com.aerobook.notification.service;

public interface EmailService {

    void sendBookingConfirmationEmail(String toEmail, String pnr, String flightNumber,
                                      String source, String destination, String departureTime,
                                      String passengerName, String seatNumber, Double totalFare);

    void sendCheckInConfirmationEmail(String toEmail, String pnr, String flightNumber,
                                      String passengerName, String seatNumber, String boardingPassNumber,
                                      String gate, String terminal);

    void sendFlightAlertEmail(String toEmail, String flightNumber, String subject, String message);

    void sendRefundConfirmationEmail(String toEmail, String pnr, String refundId, Double amount);
}
