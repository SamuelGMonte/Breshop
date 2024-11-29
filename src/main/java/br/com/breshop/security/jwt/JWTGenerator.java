package br.com.breshop.security.jwt;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import br.com.breshop.entity.Usuario;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.repository.VendedorRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Component
public class JWTGenerator {
    @Value("${jwt.secret}")
    private String secretKey;

    public long JWT_EXPIRATION = SecurityConstants.EXPIRATION_TIME;

    @Autowired
    VendedorRepository vendedorRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        Optional<Usuario> usuarioOptional = null;
        Optional<Vendedor> vendedorOptional = null;

        String role = "";
        Integer usuarioId = null;

        if ((vendedorOptional = vendedorRepository.findByEmail(username)).isPresent()) {
            usuarioId = vendedorOptional.get().getVendedorId();
            role = "Vendedor";
        } else if ((usuarioOptional = usuarioRepository.findByEmail(username)).isPresent()) {
            usuarioId = usuarioOptional.get().getUsuarioId();
            role = "Usuario";
        }

        // Extract roles from the authentication object
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Build the token with roles included in claims
        return Jwts.builder()
                .setSubject(username)
                .claim("id", usuarioId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getUserRoleFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public Integer getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id", Integer.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("Token expirado: " + ex.getMessage());
        } catch (JwtException ex) {
            System.out.println("Token inválido: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Erro ao validar o token: " + ex.getMessage());
        }
        return false;
    }
}
