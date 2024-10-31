package br.com.breshop.entity;

import java.time.LocalDateTime;
import java.util.List; // Import List

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usuarioId;

    @Column(name = "usuario_username")
    private String username;

    @Column(name = "usuario_email")
    private String email;

    @Column(name = "usuario_senha")
    private String senha;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ConfirmationTokenUser> confirmationTokenUsers;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTimeInsert;

    @UpdateTimestamp
    private LocalDateTime dateTimeUpdate;

    private boolean isEnabled;

    private boolean received;

    public Usuario() {
    }

    public Usuario(String username, String email, String senha, LocalDateTime dateTimeInsert, LocalDateTime dateTimeUpdate, boolean isEnabled, boolean received) {
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.dateTimeInsert = dateTimeInsert;
        this.dateTimeUpdate = dateTimeUpdate;
        this.isEnabled = isEnabled;
        this.received = received;
    }

    // Getters and setters


    public Integer getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
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

    public List<ConfirmationTokenUser> getConfirmationTokens() {
        return this.confirmationTokenUsers;
    }

    public void setConfirmationTokens(List<ConfirmationTokenUser> confirmationTokenVendedors) {
        this.confirmationTokenUsers = confirmationTokenVendedors;
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

    public boolean isIsEnabled() {
        return this.isEnabled;
    }

    public boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isReceived() {
        return this.received;
    }

    public boolean getReceived() {
        return this.received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }



}
