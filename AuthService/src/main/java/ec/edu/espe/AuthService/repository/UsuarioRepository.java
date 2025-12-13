package ec.edu.espe.AuthService.repository;

import ec.edu.espe.AuthService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface  UsuarioRepository extends JpaRepository<Usuario, UUID> {
    List<Usuario> findByUserId(UUID id);
    Boolean existsByUsername(String username);
}
