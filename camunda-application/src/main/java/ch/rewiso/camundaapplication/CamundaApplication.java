package ch.rewiso.camundaapplication;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;

import static ch.rewiso.camundaapplication.PaymentRetrievalConstants.*;


/**
 * You can start the process via 3 different ways:
 * <p>
 * 1) automatically in processDeployHook
 * 2) via rest api http://localhost:8080/rest/process-definition/key/payment-retrieval/start
 * 3) via "start process" in Tasklist UI
 */
@EnableProcessApplication
@SpringBootApplication
public class CamundaApplication {

    @Autowired
    private RuntimeService runtimeService;


    public static void main(String[] args) {


        SpringApplication.run(CamundaApplication.class, args);
    }


    @EventListener
    private void processPostDeploy(PostDeployEvent event) {

//        Map<String,Object> variables = new HashMap<>();
//        variables.put(VAR_AMOUNT, 1200L);
//        variables.put(VAR_ITEM, "item-nallowed");
//
//        runtimeService.startProcessInstanceByKey(PROCESS_ID, variables);
    }


    @Order(1)
    @EventListener(condition="#taskDelegate.eventName=='create'")
    public void onCreateTaskEvent(DelegateTask taskDelegate) {
        // handle create task event
        System.out.println("onCreateTaskEvent:" + taskDelegate.getName() + ":" + taskDelegate.getTaskDefinitionKey() + ":" + taskDelegate.getEventName());
    }

    @Order(2)
    @EventListener
    public void onTaskEvent(DelegateTask taskDelegate) {
        // handle task event
        System.out.println("onTaskEvent:" + taskDelegate.getName() + ":" + taskDelegate.getTaskDefinitionKey() + ":" + taskDelegate.getEventName());
    }


    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        // handle execution event
        System.out.println("onExecutionEvent:" + executionDelegate.getCurrentActivityName() + ":" + executionDelegate.getEventName());
    }


    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        // handle history event
        System.out.println("onHistoryEvent:" + historyEvent.getEventType());
    }


}
