package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.RoleDTO;
import br.com.gsolutions.productapi.dto.UserDTO;
import br.com.gsolutions.productapi.dto.UserInsertDTO;
import br.com.gsolutions.productapi.dto.UserUpdateDTO;
import br.com.gsolutions.productapi.entities.Role;
import br.com.gsolutions.productapi.entities.User;
import br.com.gsolutions.productapi.repositories.RoleRepository;
import br.com.gsolutions.productapi.repositories.UserRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserInsertDTO userInsertDTO;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "John", "Doe", "john.doe@example.com", "password", new ArrayList<>(), new HashSet<>());
        userInsertDTO = new UserInsertDTO("password");
        userUpdateDTO = new UserUpdateDTO(1L, "John", "Doe", "john.doe@example.com");
    }

    @Test
    public void testList() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserDTO> result = userService.list(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testCreate() {
        // Arrange
        user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Collections.emptyList(), Set.of());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.create(userInsertDTO);

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
    }

    @Test
    public void testFindById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserDTO result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindById_NotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdate() {
        // Arrange
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userUpdateDTO.getRoles().add(new RoleDTO(1L, "ADMIN"));
        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(new Role(1L, "ADMIN")));

        // Act
        UserDTO result = userService.update(1L, userUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).getReferenceById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdate_NotFound() {
        // Arrange
        when(userRepository.getReferenceById(1L)).thenThrow(EntityNotFoundException.class);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.update(1L, userUpdateDTO));
        verify(userRepository, times(1)).getReferenceById(1L);
    }

    @Test
    public void testDelete() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDelete_NotFound() {
        // Arrange
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(1L);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDelete_DatabaseException() {
        // Arrange
        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(1L);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> userService.delete(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    public void testLoadUserByUsername() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userService.loadUserByUsername("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getUsername());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("john.doe@example.com"));
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }
}