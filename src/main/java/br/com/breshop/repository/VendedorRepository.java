package br.com.breshop.repository;

import br.com.breshop.entity.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, UUID> {
    Optional<Vendedor> findByEmail(String email);
    boolean existsByEmail(String email);
}
