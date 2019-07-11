package ch.rewiso.camundaapplication;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;


/** You can start the process via 3 different ways:
 *
 * 1) automatically in processDeployHook
 * 2) via rest api http://localhost:8080/rest/process-definition/key/payment-retrieval/start
 * 3) via "start process" in Tasklist UI
 *
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

//        Map<String, Object> variables = new HashMap <>();
//        variables.put("amount", 1200L);
//        variables.put("item", "item-allowed");
//
//        runtimeService.startProcessInstanceByKey(PAYMENT_RETRIEVAL, variables);
    }

}
