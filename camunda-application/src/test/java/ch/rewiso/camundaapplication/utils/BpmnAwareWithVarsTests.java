package ch.rewiso.camundaapplication.utils;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;


public class BpmnAwareWithVarsTests extends BpmnAwareTests {

    protected BpmnAwareWithVarsTests() {
        super();
    }

    public static ProcessInstanceWithVarsAssert assertThat(ProcessInstance actual) {
        return ProcessInstanceWithVarsAssert.assertThat(processEngine(), actual);
    }

}
