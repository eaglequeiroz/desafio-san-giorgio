package br.com.desafio.controller;

import br.com.desafio.domain.mapper.PaymentMapper;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.usecase.ConfirmPaymentUseCase;
import br.com.desafio.exception.ChargeNotFoundException;
import br.com.desafio.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentControllerTest {

    @Mock
    private ConfirmPaymentUseCase confirmPaymentUseCase;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentModel paymentModel;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        PaymentItem item1 = PaymentItem.builder()
                .paymentId("0001")
                .paymentValue(new BigDecimal("1000"))
                .paymentStatus("PARTIAL").build();
        PaymentItem item2 = PaymentItem.builder()
                .paymentId("0002")
                .paymentValue(new BigDecimal("15000"))
                .paymentStatus("EXCEEDING").build();

        paymentModel = PaymentModel.builder()
                .clientId("C0001")
                .paymentItems(List.of(item1,item2)).build();

    }

    @Test
    void testPaymentControllerSuccess(){
        when(confirmPaymentUseCase.confirm(any(PaymentModel.class))).thenReturn(paymentModel);
        Payment payment = PaymentMapper.toPaymentResponse(paymentModel);
        ResponseEntity<Payment> response = paymentController.setPayment(payment);

        verify(confirmPaymentUseCase, times(1)).confirm(any(PaymentModel.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).getPaymentItems().size());
    }

    @Test
    void testPaymentControllerClientNotFound(){
        when(confirmPaymentUseCase.confirm(any(PaymentModel.class))).thenThrow(new ClientNotFoundException());
        Payment payment = PaymentMapper.toPaymentResponse(paymentModel);
        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class,() -> paymentController.setPayment(payment));

        assertEquals("Client not found", exception.getMessage());
        verify(confirmPaymentUseCase, times(1)).confirm(any(PaymentModel.class));
    }

    @Test
    void testPaymentControllerChargeNotFound(){
        when(confirmPaymentUseCase.confirm(any(PaymentModel.class))).thenThrow(new ChargeNotFoundException());
        Payment payment = PaymentMapper.toPaymentResponse(paymentModel);
        ChargeNotFoundException exception = assertThrows(ChargeNotFoundException.class,() -> paymentController.setPayment(payment));

        assertEquals("Charge not found", exception.getMessage());
        verify(confirmPaymentUseCase, times(1)).confirm(any(PaymentModel.class));
    }

}
