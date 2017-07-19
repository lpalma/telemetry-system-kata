package com.codurance.telemetrySystemKata;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class TelemetryDiagnosticControlsShould {

    public static final boolean DISCONNECTED = false;
    @Mock
    private TelemetryClient telemetryClient;
    private TelemetryDiagnosticControls telemetryDiagnosticControls;

    @Before
    public void setUp() throws Exception {
        telemetryDiagnosticControls = new TelemetryDiagnosticControls(telemetryClient);
    }

    @Test
    public void CheckTransmission_should_send_a_diagnostic_message_and_receive_a_status_message_response() throws Exception {
        given(telemetryClient.getOnlineStatus()).willReturn(true);

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
}
