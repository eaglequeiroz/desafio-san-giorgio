package br.com.desafio.domain.repository;

import java.util.Optional;

public interface ClientRepository {
    Optional<String> findClientById(String clientId);
}
