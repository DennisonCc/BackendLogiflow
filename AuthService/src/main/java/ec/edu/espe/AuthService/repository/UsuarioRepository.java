package ec.edu.espe.AuthService.repository;

import ec.edu.espe.AuthService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface  UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
