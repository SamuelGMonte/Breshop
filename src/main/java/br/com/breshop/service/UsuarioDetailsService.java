package br.com.breshop.service;

import java.util.ArrayList;
import java.util.Optional;

import br.com.breshop.entity.Usuario;
import br.com.breshop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.breshop.entity.Vendedor;
import br.com.breshop.repository.VendedorRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    usuario.get().getEmail(),
                    usuario.get().getSenha(),
                    new ArrayList<>()
            );
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado com email: " + email);
        }
    }
}
