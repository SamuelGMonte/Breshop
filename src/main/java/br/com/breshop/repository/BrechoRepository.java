package br.com.breshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.breshop.entity.Brecho;

@Repository
public interface BrechoRepository extends JpaRepository<Brecho, Integer> {
    Optional<Brecho> findByBrechoSite(String brechoSite);
    Optional<Brecho> findByBrechoNome(String brechoNome);

    Optional<Brecho> findByBrechoEndereco(String brechoEndereco);

    @Query("SELECT b FROM Brecho b WHERE b.vendedor.vendedorId = :vendedorId")
    List<Brecho> findByVendedorId(@Param("vendedorId") Integer vendedorId);

    @Query("SELECT b.brechoNome FROM Brecho b")
    List<String> findAllByBrechoNome(); 

    @Query("SELECT b.brechoEndereco FROM Brecho b")
    List<String> findAllByBrechoEndereco(); 

    @Query("SELECT b.brechoSite FROM Brecho b")
    List<String> findAllByBrechoSite();
}
