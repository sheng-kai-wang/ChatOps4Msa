package ntou.soselab.chatops4msa.Service.CapabilityOrchestrator;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * It's just a template for RabbitMQ.
 * Currently, we receive messages via Webhook.
 */
@Service
public class MessageDeliverer {

//    @RabbitListener(queues = "receive-rabbitmq-service_code_scanning")
//    public void receiveMessage(String message) {
//        System.out.println("Received message: " + message);
//    }
}
