package br.com.gsolutions.productapi.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gsolutions.productapi.dto.ClientDTO;
import br.com.gsolutions.productapi.entities.Client;
import br.com.gsolutions.productapi.repositories.ClientRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository repository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> list(PageRequest pageRequest){
        Page<Client> list = repository.findAll(pageRequest);
        return list.map(ClientDTO::new);
    }

    @Transactional
    public ClientDTO create(ClientDTO dto){
        Client savedClient = new Client();
        copyDataFromDTO(dto, savedClient);
        savedClient =  repository.save(savedClient);
        return new ClientDTO(savedClient);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id){
        Optional<Client> optional = repository.findById(id);
        return optional.map(ClientDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto){
        try{
            Client entity = repository.getOne(id);
            copyDataFromDTO(dto, entity);
            entity = repository.save(entity);
            return new ClientDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Entity not Found by id: "+ id);
        }
    }

    public void delete(Long id){
        try{
            repository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Entity not Found: " + id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDataFromDTO(ClientDTO dto, Client entity) {
        entity.setCpf(dto.getCpf());
        entity.setName(dto.getName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setChildren(dto.getChildren());
        entity.setIncome(dto.getIncome());
    }
}
