package ch.rewiso.camundaapplication;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class NotificationSenderTest {

    @Mock
    private DelegateExecution execution;

    @Test
    public void testNotificationSender() throws Exception {
        when(execution.getVariable("item")).thenReturn("any-item");
        when(execution.getVariable("amount")).thenReturn(345L);

        NotificationSender notificationSender = new NotificationSender();
        notificationSender.execute(execution);

        // test business logic here

        verify(execution, times(1)).getVariable("item");
        verify(execution, times(1)).getVariable("amount");
    }

}
