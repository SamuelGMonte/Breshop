package br.com.breshop.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_confirmation_token")
public class ConfirmationTokenVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "confirmation_token", nullable = false, unique = true)
    private UUID confirmationToken;

    @Column(name = "date_time_insert", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", referencedColumnName = "vendedorId")
    private Vendedor vendedor;

    // Default constructor
    public ConfirmationTokenVendedor() {
    }

    // Constructor with all parameters


    public ConfirmationTokenVendedor(LocalDateTime dateExpiration, UUID confirmationToken, LocalDateTime createdDate, Vendedor vendedor) {
        this.dateExpiration = dateExpiration;
        this.confirmationToken = confirmationToken;
        this.createdDate = createdDate;
        this.vendedor = vendedor;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public UUID getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(UUID confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }
}
