package br.com.breshop.repository;

import br.com.breshop.entity.VendedorImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnabledVendedorImagesRepository extends JpaRepository<VendedorImages, Long> {
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM VendedorImages v WHERE v.imgData = :vendedorImg AND v.isVerified = true")
    boolean isVerified(@Param("vendedorImg") byte[] vendedorImg);

}
