package br.com.breshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.breshop.entity.Vendedor;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {
    @Query("SELECT v FROM Vendedor v WHERE v.email = :email")
    Optional<Vendedor> findByEmail(String email);

    @Query("SELECT v.id FROM Vendedor v WHERE v.email = :email")
    Optional<Integer> findIdByEmail(String email);

    @Query("SELECT v.id FROM VendedorImages v JOIN v.imgData")
    List<Integer> findJoinVendedorImage();
}
