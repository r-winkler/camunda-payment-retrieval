package ch.rewiso.camundaapplication;

import ch.rewiso.camundaapplication.utils.BpmnAwareWithVarsTests;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static ch.rewiso.camundaapplication.PaymentRetrievalConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

//import static org.assertj.core.api.Assertions.assertThat;


/**
 * See https://docs.camunda.org/manual/latest/user-guide/spring-boot-integration/testing/
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "camunda.bpm.generate-unique-process-engine-name=true",
        // this is only needed if a SpringBootProcessApplication is used for the test
        "camunda.bpm.generate-unique-process-application-name=true",
        "spring.datasource.generate-unique-name=true"
})
public class CamundaApplicationTests {

    private static final Long AMOUNT_ALLOWED = 999L;
    private static final Long AMOUNT_NOT_ALLOWED = 1000L;
    private static final String ITEM_ALLOWED = "item-allowed";
    private static final String ITEM_NOT_ALLOWED = "any-other-item";

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
    public void testPaymentGetsRejected() {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        complete(task(), withVariables(VAR_APPROVED, false));

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, REVIEW_REJECTED_TASK_ID, END_REJECTED_EVENT_ID);

        BpmnAwareWithVarsTests.assertThat(processInstance)
                .hasVariableEntry(VAR_APPROVED, false)
                .hasVariableEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .hasVariableEntry(VAR_ITEM, ITEM_NOT_ALLOWED)
                .isEnded();

    }


    @Test
    public void testPaymentGetsReceived() {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        BpmnAwareWithVarsTests.assertThat(processInstance)
                .printVars()
                .hasNoVariable(VAR_APPROVED)
                .hasVariableEntry(VAR_AMOUNT, AMOUNT_ALLOWED)
                .hasVariableEntry(VAR_ITEM, ITEM_NOT_ALLOWED)
                .isEnded();

    }


    @Test
    public void testPaymentGetsReceivedBecauseItemIsAllowed() {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_ALLOWED);

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        BpmnAwareWithVarsTests.assertThat(processInstance)
                .hasVariableEntry(VAR_APPROVED, true)
                .hasVariableEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .hasVariableEntry(VAR_ITEM, ITEM_ALLOWED)
                .isEnded();

    }


    @Test
    public void testPaymentGetsReceivedBecauseUserApproves() {

        final ProcessInstance processInstance = startProcess(
                VAR_AMOUNT, AMOUNT_NOT_ALLOWED,
                VAR_ITEM, ITEM_NOT_ALLOWED);

        assertThat(processInstance).isWaitingAt(REVIEW_REJECTED_TASK_ID);

        complete(task(), withVariables(VAR_APPROVED, true));

        assertThat(processInstance).isWaitingAt(CHARGE_CREDIT_CARD_TASK_ID);

        complete(externalTask());

        assertThat(processInstance).hasPassedInOrder(START_EVENT_ID, AMOUNT_GATEWAY_ID, APPROVE_PAYMENT_TASK_ID, REVIEW_REJECTED_TASK_ID, CHARGE_CREDIT_CARD_TASK_ID, SEND_NOTIFICATION_TASK_ID, END_RECEIVED_EVENT_ID);

        BpmnAwareWithVarsTests.assertThat(processInstance)
                .hasVariableEntry(VAR_APPROVED, true)
                .hasVariableEntry(VAR_AMOUNT, AMOUNT_NOT_ALLOWED)
                .hasVariableEntry(VAR_ITEM, ITEM_NOT_ALLOWED)
                .isEnded();

    }


    private ProcessInstance startProcess(String key, Object value, Object... furtherKeyValuePairs) {
        return runtimeService.startProcessInstanceByKey(PROCESS_ID, withVariables(key, value, furtherKeyValuePairs));
    }


}
