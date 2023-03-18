package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.RoleDTO;
import br.com.gsolutions.productapi.dto.UserDTO;
import br.com.gsolutions.productapi.dto.UserInsertDTO;
import br.com.gsolutions.productapi.dto.UserUpdateDTO;
import br.com.gsolutions.productapi.entities.User;
import br.com.gsolutions.productapi.repositories.RoleRepository;
import br.com.gsolutions.productapi.repositories.UserRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);



    @Transactional(readOnly = true)
    public Page<UserDTO> list(Pageable pageable){
        Page<User> list = repository.findAll(pageable);
        return list.map(UserDTO::new);
    }

    @Transactional
    public UserDTO create(@Valid UserInsertDTO dto){
        User savedUser = new User();
        copyDataFromDTO(dto, savedUser);
        savedUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        repository.save(savedUser);
        return new UserDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        Optional<User> optional = repository.findById(id);
        return optional.map(UserDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto){
        try{
            User entity = repository.getReferenceById(id);
            copyDataFromDTO(dto, entity);
            entity = repository.save(entity);
            return new UserDTO(entity);
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

    private void copyDataFromDTO(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.getRoles().clear();
        for (RoleDTO roleDTO: dto.getRoles()){
            var optRole = roleRepository.findById(roleDTO.getId());
            optRole.ifPresent(role -> entity.getRoles().add(role));
        }
    }

    public Optional<User> findByEmail(String email) {

       return repository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var opt = repository.findByEmail(username);
        if (opt.isEmpty()){
            logger.error("user not found for email {}", username);
            throw new UsernameNotFoundException("cannot find user "+ username);
        }
        logger.info("user found for email {}", username);
        return opt.get();
    }
}
