package ch.rewiso.camundaapplication;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.rewiso.camundaapplication.PaymentRetrievalConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * https://docs.camunda.org/manual/latest/user-guide/spring-boot-integration/testing/
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "camunda.bpm.generate-unique-process-engine-name=true",
        // this is only needed if a SpringBootProcessApplication is used for the test
        "camunda.bpm.generate-unique-process-application-name=true",
        "spring.datasource.generate-unique-name=true"
})
public class CamundaSpringBootTest {

    private static final Long AMOUNT_ALLOWED = 999L;
    private static final Long AMOUNT_NOT_ALLOWED = 1000L;
    private static final String ITEM_ALLOWED = "item-allowed";
    private static final String ITEM_NOT_ALLOWED = "any-other-item";

    @MockBean
    private NotificationSender notificationSender;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;


    @Before
    public void setUp() {

        init(processEngine);
    }


    @Test
    public void testPaymentGetsRejected() throws Exception {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        complete(task(), withVariables(VAR_APPROVED, false));

        verify(notificationSender, times(0)).execute(any(DelegateExecution.class));

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, REVIEW_REJECTED_TASK_ID, END_REJECTED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(3)
                .containsEntry(VAR_APPROVED, false)
                .containsEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED);
    }


    @Test
    public void testPaymentGetsReceived() throws Exception {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        verify(notificationSender, times(1)).execute(any(DelegateExecution.class));

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(2)
                .containsEntry(VAR_AMOUNT, AMOUNT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED)
                .doesNotContainKey(VAR_APPROVED);
    }


    @Test
    public void testPaymentGetsReceivedBecauseItemIsAllowed() throws Exception {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_ALLOWED);

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        verify(notificationSender, times(1)).execute(any(DelegateExecution.class));

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(3)
                .containsEntry(VAR_APPROVED, true)
                .containsEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_ALLOWED);
    }


    @Test
    public void testPaymentGetsReceivedBecauseUserApproves() throws Exception {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        complete(task(), withVariables(VAR_APPROVED, true));

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        verify(notificationSender, times(1)).execute(any(DelegateExecution.class));

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, REVIEW_REJECTED_TASK_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(3)
                .containsEntry(VAR_APPROVED, true)
                .containsEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED);
    }


    /**
     * This test shows how to start a process instance at a specific activity
     * https://docs.camunda.org/manual/7.8/user-guide/process-engine/process-engine-concepts/#start-a-process-instance-at-any-set-of-activities
     */
    @Test
    public void testStartProcessBeforeUsertask() throws Exception {

        ProcessInstance processInstance = startProcessBefore(REVIEW_REJECTED_TASK_ID,
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED,
                VAR_APPROVED, false);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        complete(task(), withVariables(VAR_APPROVED, false));

        verify(notificationSender, times(0)).execute(any(DelegateExecution.class));

        assertThat(processInstance).hasPassedInOrder(REVIEW_REJECTED_TASK_ID, END_REJECTED_EVENT_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(3)
                .containsEntry(VAR_APPROVED, false)
                .containsEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED);
    }


    /**
     * This test shows how to modify a process instance
     * https://docs.camunda.org/manual/7.8/user-guide/process-engine/process-engine-concepts/#start-a-process-instance-at-any-set-of-activities
     * https://docs.camunda.org/manual/7.8/user-guide/process-engine/process-instance-modification/
     */
    @Test
    public void testStartProcessBeforeUsertaskAndModifyProcess() {

        ProcessInstance processInstance = startProcessBefore(REVIEW_REJECTED_TASK_ID,
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        runtimeService.createProcessInstanceModification(processInstance.getId())
                .startBeforeActivity(SEND_NOTIFICATION_TASK_ID)
                .cancelAllForActivity(REVIEW_REJECTED_TASK_ID)
                .execute();

        assertThat(processInstance).hasPassedInOrder(SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        assertThat(processInstance).hasNotPassed(CHARGE_CREDIT_CARD_TASK_ID);

        assertThat(processInstance).isEnded().variables()
                .hasSize(2)
                .containsEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .containsEntry(VAR_ITEM, ITEM_NOT_ALLOWED)
                .doesNotContainKey(VAR_APPROVED);
    }


    private ProcessInstance startProcess(String key, Object value, Object... furtherKeyValuePairs) {

        return runtimeService.startProcessInstanceByKey(PROCESS_ID, withVariables(key, value, furtherKeyValuePairs));
    }


    private ProcessInstance startProcessBefore(String beforeActivity, String key, Object value, Object... furtherKeyValuePairs) {

        return runtimeService.createProcessInstanceByKey(PROCESS_ID)
                .startBeforeActivity(beforeActivity)
                .setVariables(withVariables(key, value, furtherKeyValuePairs))
                .execute();
    }


}
