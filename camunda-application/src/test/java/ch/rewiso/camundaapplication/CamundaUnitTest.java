package ch.rewiso.camundaapplication;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.spring.boot.starter.test.helper.StandaloneInMemoryTestConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static ch.rewiso.camundaapplication.PaymentRetrievalConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;


/**
 * Simple unit-tests can be written, if not a full spring boot application is required for testing.
 * Same tests like in CamundaSpringBootTest are implemented here as a showcase.
 */
@Deployment(resources = {"bpmn/payment.bpmn", "dmn/payment.dmn"})
public class CamundaUnitTest {

    private static final Long AMOUNT_ALLOWED = 999L;
    private static final Long AMOUNT_NOT_ALLOWED = 1000L;
    private static final String ITEM_ALLOWED = "item-allowed";
    private static final String ITEM_NOT_ALLOWED = "any-other-item";

    @Rule
    public final ProcessEngineRule processEngineRule = new StandaloneInMemoryTestConfiguration().rule();


    @Test
    public void testPaymentGetsRejected() {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, REVIEW_REJECTED_TASK_ID, END_REJECTED_EVENT_ID);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        complete(task(), withVariables(VAR_APPROVED, false));

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, REVIEW_REJECTED_TASK_ID, END_REJECTED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(3)
                .containsEntry(VAR_APPROVED, false)
                .containsEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED);
    }


    @Test
    public void testPaymentGetsReceived() {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(2)
                .containsEntry(VAR_AMOUNT, AMOUNT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED)
                .doesNotContainKey(VAR_APPROVED);
    }

    private ProcessInstance startProcess(String key, Object value, Object... furtherKeyValuePairs) {

        return processEngineRule.getRuntimeService().startProcessInstanceByKey(PROCESS_ID, withVariables(key, value, furtherKeyValuePairs));
    }
}
