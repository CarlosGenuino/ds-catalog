package br.com.gsolutions.productapi.token;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import br.com.gsolutions.productapi.entities.User;
import br.com.gsolutions.productapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
public class TokenRepositoryTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Test
    public void whenFindAllValidTokenByUser_thenReturnValidTokens() {
        // Given
        User user = new User();
        user.setId(1L);
        userRepository.save(user);

        Token validToken = new Token();
        validToken.setUser(user);
        validToken.setExpired(false);
        validToken.setRevoked(false);
        tokenRepository.save(validToken);

        Token expiredToken = new Token();
        expiredToken.setUser(user);
        expiredToken.setExpired(true);
        expiredToken.setRevoked(false);
        tokenRepository.save(expiredToken);

        Token revokedToken = new Token();
        revokedToken.setUser(user);
        revokedToken.setExpired(false);
        revokedToken.setRevoked(true);
        tokenRepository.save(revokedToken);

        // When
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // Then
        assertThat(validTokens).hasSize(1);
        assertThat(validTokens.get(0).isExpired()).isFalse();
        assertThat(validTokens.get(0).isRevoked()).isFalse();
    }

    @Test
    public void whenFindByToken_thenReturnToken() {
        // Given
        User user = new User();
        user.setId(1L);
        userRepository.save(user);

        Token token = new Token();
        token.setUser(user);
        token.setToken("valid-token");
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);


        // When
        Optional<Token> foundToken = tokenRepository.findByToken("valid-token");

        // Then
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getToken()).isEqualTo("valid-token");
    }

    @Test
    public void whenFindByInvalidToken_thenReturnEmpty() {
        // When
        Optional<Token> foundToken = tokenRepository.findByToken("invalid-token");

        // Then
        assertThat(foundToken).isNotPresent();
    }
}