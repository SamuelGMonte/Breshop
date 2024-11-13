package br.com.breshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.breshop.entity.Vendedor;
import br.com.breshop.repository.VendedorRepository;

@Service
public class VendedorDetailsService implements UserDetailsService {

    @Autowired
    private VendedorRepository vendedorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<Vendedor> vendedores = vendedorRepository.findByEmail(email);
        if (!vendedores.isEmpty()) {
            Vendedor vendedor = vendedores.get(0);  // Usa o primeiro vendedor encontrado
            return new org.springframework.security.core.userdetails.User(
                    vendedor.getEmail(),
                    vendedor.getSenha(),
                    new ArrayList<>()
            );
        }  else {
            throw new UsernameNotFoundException("Vendedor não encontrado com email: " + email);
        }
    }
}
