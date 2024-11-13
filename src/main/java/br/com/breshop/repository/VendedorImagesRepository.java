package br.com.breshop.repository;

import br.com.breshop.entity.Vendedor;
import br.com.breshop.entity.VendedorImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorImagesRepository extends JpaRepository<VendedorImages, Long> {
    Optional<VendedorImages> findByVendedor(Vendedor vendedor);

    @Query("SELECT v.imgData FROM VendedorImages v")
    List<byte[]> findAllByBrechoSite();
}
