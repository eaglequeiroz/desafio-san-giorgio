package br.com.desafio.domain.repository.impl;

import br.com.desafio.domain.repository.ClientRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final Set<String> clients = new HashSet<>();

    public ClientRepositoryImpl(){
        clients.add("CLIENT_0001");
        clients.add("CLIENT_0002");
        clients.add("CLIENT_0003");
        clients.add("CLIENT_0004");
        clients.add("CLIENT_0005");
    }


    @Override
    public Optional<String> findClientById(String clientId) {
        return clients.stream().filter(s -> s.equals(clientId)).findAny();
    }
}
