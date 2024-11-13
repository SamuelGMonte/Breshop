package br.com.breshop.security;

import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.repository.VendedorRepository;
import br.com.breshop.service.PasswordEncoderService;
import br.com.breshop.service.UsuarioDetailsService;
import br.com.breshop.service.VendedorDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthManager implements AuthenticationManager {

    private final UsuarioDetailsService usuarioDetailsService;
    private final VendedorDetailsService vendedorDetailsService;
    private final VendedorRepository vendedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderService passwordEncoder;

    @Autowired
    public CustomAuthManager(UsuarioDetailsService usuarioDetailsService,
                             VendedorDetailsService vendedorDetailsService, VendedorRepository vendedorRepository, UsuarioRepository usuarioRepository,
                             PasswordEncoderService passwordEncoder) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.vendedorDetailsService = vendedorDetailsService;
        this.vendedorRepository = vendedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = null;
        UserDetails vendedorDetails = null;

        if (!vendedorRepository.findByEmail(username).isEmpty()) {
            userDetails = vendedorDetailsService.loadUserByUsername(username);
        } else if (usuarioRepository.findByEmail(username).isPresent()) {
            userDetails = usuarioDetailsService.loadUserByUsername(username);
        }

        if (userDetails == null) {
            throw new BadCredentialsException("Credenciais inválidas: Usuário não encontrado.");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Senha inválida");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
