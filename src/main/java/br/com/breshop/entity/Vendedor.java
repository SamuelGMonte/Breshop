package br.com.breshop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List; // Import List

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "tbl_vendedor")
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendedorId;

    @Column(name = "vendedor_usuario")
    private String username;

    @Column(name = "vendedor_email", unique = true)
    private String email;

    @Column(name = "vendedor_senha")
    private String senha;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConfirmationTokenVendedor> confirmationTokenVendedors;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Brecho> brechos = new ArrayList<>();

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private LocalDateTime dateTimeInsert;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private LocalDateTime dateTimeUpdate;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = false;

    public Vendedor() {
    }

    public Vendedor(String username, String email, String senha, LocalDateTime dateTimeInsert, LocalDateTime dateTimeUpdate, List<Brecho> brechos, boolean isEnabled) {
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.dateTimeInsert = dateTimeInsert;
        this.dateTimeUpdate = dateTimeUpdate;
        this.brechos = brechos;
        this.isEnabled = isEnabled;

        if (brechos != null) {
            for (Brecho brecho : brechos) {
                brecho.setVendedor(this);
            }
        }
    }



    // Getters and setters


    public Integer getVendedorId() {
        return this.vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<ConfirmationTokenVendedor> getConfirmationTokens() {
        return this.confirmationTokenVendedors;
    }

    public void setConfirmationTokens(List<ConfirmationTokenVendedor> confirmationTokenVendedors) {
        this.confirmationTokenVendedors = confirmationTokenVendedors;
    }

    public LocalDateTime getDateTimeInsert() {
        return this.dateTimeInsert;
    }

    public void setDateTimeInsert(LocalDateTime dateTimeInsert) {
        this.dateTimeInsert = dateTimeInsert;
    }

    public LocalDateTime getDateTimeUpdate() {
        return this.dateTimeUpdate;
    }

    public void setDateTimeUpdate(LocalDateTime dateTimeUpdate) {
        this.dateTimeUpdate = dateTimeUpdate;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public List<Brecho> getBrechos() {
        return brechos;
    }

    public void setBrechos(List<Brecho> brechos) {
        this.brechos = brechos;
    }
}
