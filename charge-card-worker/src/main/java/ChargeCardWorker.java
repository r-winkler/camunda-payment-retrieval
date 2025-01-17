import org.camunda.bpm.client.ExternalTaskClient;


public class ChargeCardWorker {


    public static void main(String[] args) {

        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();

        // subscribe to an external task topic as specified in the process
        client.subscribe("charge-card")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler(ChargeCardHandler.getTaskHandler())
                .open();
    }
}
