package br.com.gsolutions.productapi.repositories;

import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
