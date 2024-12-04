package br.com.breshop.repository;

import br.com.breshop.entity.Brecho;
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

    @Query("SELECT v.imgData FROM VendedorImages v WHERE v.id = :vendedorId")
    Optional<VendedorImages> findByVendedorId(Integer vendedorId);

    @Query("SELECT v.imgData FROM VendedorImages v")
    List<byte[]> findAllByBrechoSite();

    @Query("SELECT v.id FROM VendedorImages v WHERE v.imgData IS NOT NULL")
    List<Integer> findJoinVendedorImage();

    @Query("SELECT v.brechoImg FROM VendedorImages v")
    List<Brecho> findVendedorBrechos();

}
