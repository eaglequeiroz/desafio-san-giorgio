package br.com.desafio.domain.repository.impl;

import br.com.desafio.controller.PaymentItem;
import br.com.desafio.domain.repository.ChargeRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class ChargeRepositoryImpl implements ChargeRepository {

    private final Set<PaymentItem> charges = new HashSet<>();

    public ChargeRepositoryImpl(){
        charges.add(PaymentItem.builder().paymentId("CHARGE_0001").paymentValue(new BigDecimal("200")).build());
        charges.add(PaymentItem.builder().paymentId("CHARGE_0002").paymentValue(new BigDecimal("2050")).build());
        charges.add(PaymentItem.builder().paymentId("CHARGE_0003").paymentValue(new BigDecimal("7800")).build());
        charges.add(PaymentItem.builder().paymentId("CHARGE_0004").paymentValue(new BigDecimal("110")).build());
        charges.add(PaymentItem.builder().paymentId("CHARGE_0005").paymentValue(new BigDecimal("201")).build());
    }

    @Override
    public Optional<PaymentItem> findChargeById(String paymentId) {
        return charges.stream().filter(paymentItem -> Objects.equals(paymentItem.getPaymentId(), paymentId)).findAny();
    }
}
