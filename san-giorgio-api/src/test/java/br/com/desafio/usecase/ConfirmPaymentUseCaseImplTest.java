package br.com.desafio.usecase;

import br.com.desafio.controller.PaymentItem;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.repository.ChargeRepository;
import br.com.desafio.domain.repository.ClientRepository;
import br.com.desafio.domain.service.SqsService;
import br.com.desafio.domain.usecase.impl.ConfirmPaymentUseCaseImpl;
import br.com.desafio.exception.ChargeNotFoundException;
import br.com.desafio.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ConfirmPaymentUseCaseImplTest {

    @Mock
    private ChargeRepository chargeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SqsService sqsService;

    @InjectMocks
    private ConfirmPaymentUseCaseImpl confirmPaymentUseCase;

    private PaymentModel paymentModel;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        PaymentItem item = PaymentItem.builder().paymentId("ID-01").paymentValue(new BigDecimal("100")).build();

        paymentModel = PaymentModel.builder().clientId("CID-01").paymentItems(List.of(item)).build();
    }

    @Test
    void testOnePaymentExceedingSuccess() {
        when(clientRepository.findClientById(anyString())).thenReturn(Optional.of("CID-01"));
        when(chargeRepository.findChargeById(anyString())).thenReturn(Optional.of(new PaymentItem("ID-O1", new BigDecimal("50"), null)));

        doReturn(paymentModel.getPaymentItems().get(0)).when(sqsService).sendToExceedingQueue(any(PaymentItem.class));

        PaymentModel result = confirmPaymentUseCase.confirm(paymentModel);

        verify(sqsService, times(1)).sendToExceedingQueue(any(PaymentItem.class));

        assertEquals("EXCEEDING", result.getPaymentItems().get(0).getPaymentStatus());
    }

    @Test
    void testOnePaymentPartialSuccess() {
        when(clientRepository.findClientById(anyString())).thenReturn(Optional.of("CID-01"));
        when(chargeRepository.findChargeById(anyString())).thenReturn(Optional.of(new PaymentItem("ID-O1", new BigDecimal("150"), null)));

        doReturn(paymentModel.getPaymentItems().get(0)).when(sqsService).sendToPartialQueue(any(PaymentItem.class));

        PaymentModel result = confirmPaymentUseCase.confirm(paymentModel);

        verify(sqsService, times(1)).sendToPartialQueue(any(PaymentItem.class));

        assertEquals("PARTIAL", result.getPaymentItems().get(0).getPaymentStatus());
    }

    @Test
    void testOnePaymentTotalSuccess() {
        when(clientRepository.findClientById(anyString())).thenReturn(Optional.of("CID-01"));
        when(chargeRepository.findChargeById(anyString())).thenReturn(Optional.of(new PaymentItem("ID-O1", new BigDecimal("100"), null)));

        doReturn(paymentModel.getPaymentItems().get(0)).when(sqsService).sendToTotalQueue(any(PaymentItem.class));

        PaymentModel result = confirmPaymentUseCase.confirm(paymentModel);

        verify(sqsService, times(1)).sendToTotalQueue(any(PaymentItem.class));

        assertEquals("TOTAL", result.getPaymentItems().get(0).getPaymentStatus());
    }

    @Test
    void testClientNotFound() {
        when(clientRepository.findClientById(anyString())).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () -> confirmPaymentUseCase.confirm(paymentModel));

        assertEquals("Client not found", exception.getMessage());
        verify(sqsService, times(0)).sendToPartialQueue(any(PaymentItem.class));
    }

    @Test
    void testChargeNotFound() {
        when(clientRepository.findClientById(anyString())).thenReturn(Optional.of("CID-01"));
        when(chargeRepository.findChargeById(anyString())).thenReturn(Optional.empty());

        ChargeNotFoundException exception = assertThrows(ChargeNotFoundException.class, () -> confirmPaymentUseCase.confirm(paymentModel));

        assertEquals("Charge not found", exception.getMessage());
        verify(sqsService, times(0)).sendToPartialQueue(any(PaymentItem.class));
    }

}
