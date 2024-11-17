package br.com.breshop.repository;

import java.util.List;
import java.util.Optional;

import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.entity.Brecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.breshop.entity.Vendedor;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {
    @Query("SELECT v FROM Vendedor v WHERE v.email = :email")
    Optional<Vendedor> findByEmail(String email);

    @Query("SELECT v.id FROM Vendedor v WHERE v.email = :email")
    Optional<Integer> findIdByEmail(String email);

    @Query("SELECT v.id FROM VendedorImages v LEFT JOIN Vendedor f ON v.id = f.id")
    List<Integer> findJoinVendedorImage();
}
