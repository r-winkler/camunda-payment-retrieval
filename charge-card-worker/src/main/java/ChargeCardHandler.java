import org.camunda.bpm.client.task.ExternalTaskHandler;

import java.util.function.Supplier;
import java.util.logging.Logger;


public class ChargeCardHandler {

    private final static Logger LOGGER = Logger.getLogger(ChargeCardHandler.class.getName());


    private static Supplier<ExternalTaskHandler> externalTaskHandlerSupplier = () -> (externalTask, externalTaskService) -> {
        // Put your business logic here

        // Get a process variable
        String item = (String) externalTask.getVariable("item");
        Long amount = (Long) externalTask.getVariable("amount");
        LOGGER.info("Charging credit card with an amount of '" + amount + "'€ for the item '" + item + "'...");

        // Complete the task
        externalTaskService.complete(externalTask);
    };


    public static ExternalTaskHandler getTaskHandler() {

        return externalTaskHandlerSupplier.get();
    }
}
