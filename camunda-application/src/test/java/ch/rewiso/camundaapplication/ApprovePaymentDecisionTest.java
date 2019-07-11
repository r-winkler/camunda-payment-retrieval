package ch.rewiso.camundaapplication;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.test.DmnEngineRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;


public class ApprovePaymentDecisionTest {

    @Rule
    public DmnEngineRule dmnEngineRule = new DmnEngineRule();


    private static final String APPROVE_PAYMENT_DMN_PATH = "/dmn/payment.dmn";
    private static final String APPROVE_PAYMENT_ID = "approve-payment";


    private DmnEngine dmnEngine;
    private DmnDecision decision;


    @Before
    public void parseDecision() {

        InputStream inputStream = getClass().getResourceAsStream(APPROVE_PAYMENT_DMN_PATH);
        dmnEngine = dmnEngineRule.getDmnEngine();
        decision = dmnEngine.parseDecision(APPROVE_PAYMENT_ID, inputStream);

    }


    @Test
    public void shouldNotApproveItem() {
        VariableMap variables = Variables.putValue("item", "item-mot-allowed");

        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
        assertThat((boolean)result.getSingleResult().getEntry("approved")).isFalse();
    }


    @Test
    public void shouldApproveItem() {
        VariableMap variables = Variables.putValue("item", "item-allowed");

        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
        assertThat((boolean)result.getSingleResult().getEntry("approved")).isTrue();
    }


}
