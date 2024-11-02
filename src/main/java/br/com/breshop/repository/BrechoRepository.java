package br.com.breshop.repository;

import java.util.List;
import java.util.Optional;

import br.com.breshop.entity.Brecho;
import br.com.breshop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.breshop.entity.Vendedor;

@Repository
public interface BrechoRepository extends JpaRepository<Brecho, Integer> {
    List<Brecho> findByBrechoSite(String brechoSite);
}
