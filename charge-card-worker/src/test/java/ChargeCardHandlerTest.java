import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ChargeCardHandlerTest {

    @Mock
    private ExternalTask externalTask;

    @Mock
    private ExternalTaskService externalTaskService;

    @Test
    public void testTaskHandlerSupplier() {
        when(externalTask.getVariable("item")).thenReturn("any-item");
        when(externalTask.getVariable("amount")).thenReturn(345L);

        ChargeCardHandler.getTaskHandler().execute(externalTask, externalTaskService);

        verify(externalTask, times(1)).getVariable("item");
        verify(externalTask, times(1)).getVariable("amount");
        verify(externalTaskService, times(1)).complete(externalTask);
    }




}
