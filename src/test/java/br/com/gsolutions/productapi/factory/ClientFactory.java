package br.com.gsolutions.productapi.factory;

import br.com.gsolutions.productapi.dto.ClientDTO;
import br.com.gsolutions.productapi.entities.Client;

import java.time.Instant;

public class ClientFactory {

    public static Client createNewClient(){
        return Client.builder()
                .id(1L)
                .cpf("123123123-12")
                .name("Carlos Alexandre Silva Genuino")
                .income(5000D)
                .children(4)
                .updatedAt(Instant.now())
                .createdAt(Instant.now())
                .birthdate(Instant.now())
                .build();
    }

    public static ClientDTO createNewClientDTO(){
        return new ClientDTO(createNewClient());
    }

}
