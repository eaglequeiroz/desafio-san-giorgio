package br.com.desafio.domain.model;

import br.com.desafio.controller.PaymentItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentModel {

    private String clientId;
    private List<PaymentItem> paymentItems;
}
