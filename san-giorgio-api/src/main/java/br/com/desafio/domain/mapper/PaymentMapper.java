package br.com.desafio.domain.mapper;

import br.com.desafio.controller.Payment;
import br.com.desafio.domain.model.PaymentModel;

public class PaymentMapper {

    public static PaymentModel toPaymentModel(Payment payment){
        return PaymentModel.builder()
                .clientId(payment.getClientId())
                .paymentItems(payment.getPaymentItems())
                .build();
    }

    public static Payment toPaymentResponse(PaymentModel paymentModel){
        return Payment.builder()
                .clientId(paymentModel.getClientId())
                .paymentItems(paymentModel.getPaymentItems())
                .build();
    }
}
