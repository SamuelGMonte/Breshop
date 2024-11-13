package br.com.breshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.breshop.entity.ConfirmationTokenUser;
import br.com.breshop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConfirmationTokenUserRepository extends JpaRepository<ConfirmationTokenUser, UUID> {
    ConfirmationTokenUser findByConfirmationToken(UUID confirmationToken);
    Optional<ConfirmationTokenUser> findByUsuario(Usuario usuario);
    List<ConfirmationTokenUser> findAllByUsuario(Usuario usuario);
}

