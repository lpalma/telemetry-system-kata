package com.codurance.telemetrySystemKata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class TelemetryDiagnosticControlsShould
{

    @Mock
    private TelemetryClient telemetryClient;

    @Test
    public void CheckTransmission_should_send_a_diagnostic_message_and_receive_a_status_message_response() throws Exception {
        given(telemetryClient.getOnlineStatus()).willReturn(true);

        TelemetryDiagnosticControls telemetryDiagnosticControls = new TelemetryDiagnosticControls(telemetryClient);

        telemetryDiagnosticControls.checkTransmission();

        InOrder inOrder = inOrder(telemetryClient);
        inOrder.verify(telemetryClient).send(TelemetryClient.DIAGNOSTIC_MESSAGE);
        inOrder.verify(telemetryClient).receive();
    }

}
