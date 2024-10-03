package br.com.desafio.domain.service;

import br.com.desafio.controller.PaymentItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@RequiredArgsConstructor
public class SqsService {

    private static final Logger log = LoggerFactory.getLogger(SqsService.class);
    private final SqsClient sqsClient;

    private static final String PARTIAL_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/277707125786/PartialQueue";
    private static final String TOTAL_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/277707125786/TotalQueue";
    private static final String EXCEEDING_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/277707125786/ExceedingQueue";

    public PaymentItem sendToPartialQueue(PaymentItem paymentItem) {
        return sendMessage(PARTIAL_QUEUE_URL, paymentItem);
    }

    public PaymentItem sendToTotalQueue(PaymentItem paymentItem) {
        return sendMessage(TOTAL_QUEUE_URL, paymentItem);
    }

    public PaymentItem sendToExceedingQueue(PaymentItem paymentItem) {
        return sendMessage(EXCEEDING_QUEUE_URL, paymentItem);
    }

    private PaymentItem sendMessage(String queueUrl, PaymentItem paymentItem) {
        String messageBody = toJson(paymentItem);

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();

        SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);
        log.info("The message was sent to the specific Queue and the response message id was: {}", response.messageId());
        return paymentItem;
    }

    private String toJson(PaymentItem paymentItem) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(paymentItem);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't convert Payment Item to JSON!");
        }
    }
}
