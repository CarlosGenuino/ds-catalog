package br.com.gsolutions.productapi.auth;

import br.com.gsolutions.productapi.config.JwtService;
import br.com.gsolutions.productapi.entities.Role;
import br.com.gsolutions.productapi.entities.RoleType;
import br.com.gsolutions.productapi.entities.User;
import br.com.gsolutions.productapi.repositories.UserRepository;
import br.com.gsolutions.productapi.token.Token;
import br.com.gsolutions.productapi.token.TokenRepository;
import br.com.gsolutions.productapi.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var initialRole = new Role(RoleType.ROLE_OPERATOR.getId(), RoleType.ROLE_OPERATOR.getRole());
        var user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .build();
        user.addRole(initialRole);
        var jwtToken = jwtService.generateToken(user);
        var savedUser = repository.save(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

//    private void revokeAllUserTokens(User user) {
//        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
//        if (validUserTokens.isEmpty())
//            return;
//        validUserTokens.forEach(token -> {
//            token.setExpired(true);
//            token.setRevoked(true);
//        });
//        tokenRepository.saveAll(validUserTokens);
//    }
}
