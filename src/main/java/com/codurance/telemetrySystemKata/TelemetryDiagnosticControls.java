package com.codurance.telemetrySystemKata;

public class TelemetryDiagnosticControls {
    private final String DiagnosticChannelConnectionString = "*111#";

    private final TelemetryClient telemetryClient;
    private String diagnosticInfo = "";

    public TelemetryDiagnosticControls() {
        this(new TelemetryClient());
    }

    public TelemetryDiagnosticControls(TelemetryClient telemetryClient) {
        this.telemetryClient = telemetryClient;
    }

    public String getDiagnosticInfo() {
        return diagnosticInfo;
    }

    public void setDiagnosticInfo(String diagnosticInfo) {
        this.diagnosticInfo = diagnosticInfo;
    }

    public void checkTransmission() throws Exception {
        resetDiagnosticInfo();

        startConnection();

        sendDiagnosticMessage();

        receiveDiagnosticInfo();
    }

    private void receiveDiagnosticInfo() {
        diagnosticInfo = telemetryClient.receive();
    }

    private void sendDiagnosticMessage() {
        telemetryClient.send(TelemetryClient.DIAGNOSTIC_MESSAGE);
    }

    private void resetDiagnosticInfo() {
        diagnosticInfo = "";
    }

    private void startConnection() throws Exception {
        telemetryClient.disconnect();

        int retryLeft = 3;
        while (!telemetryClient.getOnlineStatus() && retryLeft > 0) {
            telemetryClient.connect(DiagnosticChannelConnectionString);
            retryLeft -= 1;
        }

        if (!telemetryClient.getOnlineStatus()) {
            throw new Exception("Unable to connect.");
        }
    }
}
