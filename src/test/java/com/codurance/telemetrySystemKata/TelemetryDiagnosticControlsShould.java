package com.codurance.telemetrySystemKata;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TelemetryDiagnosticControlsShould {

    public static final boolean DISCONNECTED = false;
    public static final String TELEMETRY_SERVER_CONNECTION_STRING ="*111#";
    public static final int MAX_NUMBER_OF_CONNECTION_ATTEMPTS = 3;
    public static final boolean CONNECTED = true;
    @Mock
    private TelemetryClient telemetryClient;
    private TelemetryDiagnosticControls telemetryDiagnosticControls;

    @Before
    public void setUp() throws Exception {
        telemetryDiagnosticControls = new TelemetryDiagnosticControls(telemetryClient);
    }

    @Test
    public void send_a_diagnostic_message_and_receive_a_status_message_response() throws Exception {
        given(telemetryClient.getOnlineStatus()).willReturn(CONNECTED);

        telemetryDiagnosticControls.checkTransmission();

        InOrder inOrder = inOrder(telemetryClient);
        inOrder.verify(telemetryClient).send(TelemetryClient.DIAGNOSTIC_MESSAGE);
        inOrder.verify(telemetryClient).receive();
    }

    @Test(expected = Exception.class)
    public void throw_exception_if_unable_to_connect_to_client() throws Exception {
        given(telemetryClient.getOnlineStatus()).willReturn(DISCONNECTED);

        telemetryDiagnosticControls.checkTransmission();
    }

    @Test
    public void not_retry_to_connect_to_client_forever() {
        given(telemetryClient.getOnlineStatus()).willReturn(DISCONNECTED);

        try {
            telemetryDiagnosticControls.checkTransmission();
        } catch (Exception ignored) {
        }

        verify(telemetryClient, atMost(MAX_NUMBER_OF_CONNECTION_ATTEMPTS)).connect(TELEMETRY_SERVER_CONNECTION_STRING);
    }

    @Test
    public void stop_retrying_if_connection_is_received() throws Exception {
        given(telemetryClient.getOnlineStatus()).willReturn(DISCONNECTED, CONNECTED);

        telemetryDiagnosticControls.checkTransmission();

        verify(telemetryClient, times(1)).connect(TELEMETRY_SERVER_CONNECTION_STRING);
    }

    @Test
    public void disconnect_before_trying_new_connection() throws Exception {
        given(telemetryClient.getOnlineStatus()).willReturn(DISCONNECTED, CONNECTED);

        telemetryDiagnosticControls.checkTransmission();

        InOrder inOrder = inOrder(telemetryClient);
        inOrder.verify(telemetryClient).disconnect();
        inOrder.verify(telemetryClient).connect(TELEMETRY_SERVER_CONNECTION_STRING);
    }
}
