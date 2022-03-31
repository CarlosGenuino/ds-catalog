package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.ClientDTO;
import br.com.gsolutions.productapi.dto.ProductDTO;
import br.com.gsolutions.productapi.entities.Client;
import br.com.gsolutions.productapi.factory.ClientFactory;
import br.com.gsolutions.productapi.factory.ProductFactory;
import br.com.gsolutions.productapi.repositories.ClientRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService service;

    @Mock
    private ClientRepository repository;

    private Long existingId;
    private Long nonExistId;
    private Long dependantId;
    private Client client;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistId = 2L;
        dependantId = 3L;
        client = ClientFactory.createNewClient();
        PageImpl<Client> page = new PageImpl<>(List.of(client));

        // FindAll
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        // FindOne
        Mockito.when(repository.getOne(existingId)).thenReturn(client);
        Mockito.when(repository.getOne(nonExistId)).thenThrow(EntityNotFoundException.class);

        // FindById
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));
        Mockito.when(repository.findById(nonExistId)).thenReturn(Optional.empty());

        // Save
        Mockito.when(repository.save(client)).thenReturn(client);

        // Update
        Mockito.when(repository.save(client)).thenReturn(client);

        // Delete
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependantId);
    }

    @Test
    void whenCreate(){
        ClientDTO dto = ClientFactory.createNewClientDTO();
        ClientDTO retorno = service.create(dto);
        Assertions.assertEquals(retorno.getName(), dto.getName());
    }

    @Test
    void whenSave(){
        repository.save(client);
        Assertions.assertEquals(client, client);
    }

    @Test
    void whenUpdate(){
        ClientDTO dto = service.findById(existingId);
        String novoNome = "Toys";
        dto.setName(novoNome);
        dto = service.update(existingId, dto);
        Assertions.assertEquals(novoNome, dto.getName());
        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(client);
    }

    @Test
    void whenUpdateTrowsResourceNotFoundException(){
        ClientDTO dto = ClientFactory.createNewClientDTO();
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> service.update(nonExistId, dto));
    }

    @Test
    void whenFindAllReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientDTO> result =  service.list(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }


    @Test
    public void sholdDoNothingWhenIdExist(){
        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void sholdThowsExceptionWhenIdDoesExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistId);
    }

    @Test
    public void sholdThowsDataIntegrityViolationExceptionWhenIdDoesExist(){
        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependantId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(dependantId);
    }

}