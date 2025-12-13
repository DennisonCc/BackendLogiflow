package ec.edu.espe.AuthService.repository;

import ec.edu.espe.AuthService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
