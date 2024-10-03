package br.com.desafio.domain.repository;

import br.com.desafio.controller.PaymentItem;

import java.util.Optional;

public interface ChargeRepository {
    Optional<PaymentItem> findChargeById(String paymentId);
}
