package br.com.desafio.controller;


import br.com.desafio.domain.mapper.PaymentMapper;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.usecase.ConfirmPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    @PutMapping(path = "/api/payment")
    public ResponseEntity<Payment> setPayment(@RequestBody Payment request) {
        PaymentModel paymentModel = PaymentMapper.toPaymentModel(request);

        PaymentModel processedPaymentModel = confirmPaymentUseCase.confirm(paymentModel);

        Payment response = PaymentMapper.toPaymentResponse(processedPaymentModel);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
