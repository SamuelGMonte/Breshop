package br.com.breshop.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_vendedor")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID vendedorId;

    @Column(name = "vendedor_usuario")
    private String username;

    @Column(name = "vendedor_email")
    private String email;

    @Column(name = "vendedor_senha")
    private String senha;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime DateTimeInsert;

    @UpdateTimestamp
    private LocalDateTime  DateTimeUpdate;

    public Vendedor() {
    }

    public Vendedor(UUID vendedorID, String username, String email, String senha, LocalDateTime  dateTimeInsert, LocalDateTime  dateTimeUpdate) {
        this.vendedorId = vendedorID;
        this.username = username;
        this.email = email;
        this.senha = senha;
        DateTimeInsert = dateTimeInsert;
        DateTimeUpdate = dateTimeUpdate;
    }

    public UUID getVendedorID() {
        return vendedorId;
    }

    public void setVendedorID(UUID vendedorID) {
        this.vendedorId = vendedorID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDateTimeInsert() {
        return DateTimeInsert;
    }

    public void setDateTimeInsert(LocalDateTime dateTimeInsert) {
        DateTimeInsert = dateTimeInsert;
    }

    public LocalDateTime getDateTimeUpdate() {
        return DateTimeUpdate;
    }

    public void setDateTimeUpdate(LocalDateTime dateTimeUpdate) {
        DateTimeUpdate = dateTimeUpdate;
    }
}
