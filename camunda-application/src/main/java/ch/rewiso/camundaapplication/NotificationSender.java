package ch.rewiso.camundaapplication;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.logging.Logger;

import static ch.rewiso.camundaapplication.PaymentRetrievalConstants.VAR_AMOUNT;
import static ch.rewiso.camundaapplication.PaymentRetrievalConstants.VAR_ITEM;


public class NotificationSender implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(NotificationSender.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String item = (String) execution.getVariable(VAR_ITEM);
        Long amount = (Long) execution.getVariable(VAR_AMOUNT);
        LOGGER.info("Payment '" + item + "' was executed and credit card was charged with amount of " + amount + "CHF.");

    }
}
