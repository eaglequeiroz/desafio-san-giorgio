package br.com.desafio.domain.usecase.impl;

import br.com.desafio.controller.PaymentItem;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.repository.ChargeRepository;
import br.com.desafio.domain.repository.ClientRepository;
import br.com.desafio.domain.service.SqsService;
import br.com.desafio.domain.usecase.ConfirmPaymentUseCase;
import br.com.desafio.exception.ChargeNotFoundException;
import br.com.desafio.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmPaymentUseCaseImpl implements ConfirmPaymentUseCase {

    private final ClientRepository clientRepository;
    private final ChargeRepository chargeRepository;
    private final SqsService sqsService;

    private static final String PARTIAL_PAYMENT = "PARTIAL";
    private static final String TOTAL_PAYMENT = "TOTAL";
    private static final String EXCEEDING_PAYMENT = "EXCEEDING";

    @Override
    public PaymentModel confirm(PaymentModel paymentModel) {

        isClientExists(paymentModel.getClientId());
        List<PaymentItem> payments = paymentModel.getPaymentItems();

        for (PaymentItem payment : payments) {
            PaymentItem originalPayment = getOriginalPayment(payment);
            calculatePaymentStatus(payment, originalPayment);
        }

        return paymentModel;
    }

    private void isClientExists(String clientId) {
        clientRepository.findClientById(clientId).orElseThrow(ClientNotFoundException::new);
    }

    private PaymentItem getOriginalPayment(PaymentItem payment) {
        return chargeRepository.findChargeById(payment.getPaymentId()).orElseThrow(ChargeNotFoundException::new);
    }

    private void calculatePaymentStatus(PaymentItem payment, PaymentItem originalPayment) {
        if (payment.getPaymentValue().compareTo(originalPayment.getPaymentValue()) < 0) {
            payment = sqsService.sendToPartialQueue(payment);
            payment.setPaymentStatus(PARTIAL_PAYMENT);
        } else if (payment.getPaymentValue().compareTo(originalPayment.getPaymentValue()) == 0) {
            payment = sqsService.sendToTotalQueue(payment);
            payment.setPaymentStatus(TOTAL_PAYMENT);
        } else {
            payment = sqsService.sendToExceedingQueue(payment);
            payment.setPaymentStatus(EXCEEDING_PAYMENT);
        }
    }


}
