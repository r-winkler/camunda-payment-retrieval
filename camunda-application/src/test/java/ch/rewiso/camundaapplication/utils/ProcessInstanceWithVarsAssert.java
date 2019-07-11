package ch.rewiso.camundaapplication.utils;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.ProcessInstanceAssert;

import static org.assertj.core.api.Assertions.entry;


public class ProcessInstanceWithVarsAssert extends ProcessInstanceAssert {


    protected ProcessInstanceWithVarsAssert(ProcessEngine engine, ProcessInstance actual) {

        super(engine, actual);
    }

    protected static ProcessInstanceWithVarsAssert assertThat(ProcessEngine engine, ProcessInstance actual) {
        return new ProcessInstanceWithVarsAssert(engine, actual);
    }

    public ProcessInstanceWithVarsAssert hasVariableEntry(String key, Object value) {

        org.assertj.core.api.Assertions.assertThat(super.vars()).contains(entry(key, value));
        return this;
    }

    public ProcessInstanceWithVarsAssert hasNoVariable(String key) {

        org.assertj.core.api.Assertions.assertThat(super.vars().keySet()).doesNotContain(key);
        return this;
    }
}
