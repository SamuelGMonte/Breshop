package br.com.breshop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.breshop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.breshop.entity.ConfirmationTokenVendedor;
import br.com.breshop.entity.Vendedor;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenVendedor, UUID> {
    ConfirmationTokenVendedor findByConfirmationToken(UUID confirmationToken);
    Optional<ConfirmationTokenVendedor> findByVendedor(Vendedor vendedor);
    List<ConfirmationTokenVendedor> findAllByVendedor(Vendedor vendedor);
    ConfirmationTokenVendedor findFirstByVendedorOrderByCreatedDateDesc(Vendedor vendedor);
}
