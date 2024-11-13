package br.com.breshop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "tbl_brecho")
public class Brecho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer brechoId;

    @Column(name = "brecho_nome")
    private String brechoNome;

    @Column(unique = true, nullable = false)
    private String brechoSite;

    @Column(name = "brecho_endereco")
    private String brechoEndereco;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Vendedor vendedor;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime DateTimeInsert;

    @UpdateTimestamp
    private LocalDateTime DateTimeUpdate;

    @OneToOne(mappedBy = "brechoImg", cascade = CascadeType.ALL)
    private VendedorImages vendedorImagesList;

    public Brecho() {
    }

    public Brecho(String brechoNome, String brechoSite, String brechoEndereco, Vendedor vendedor, LocalDateTime dateTimeInsert, LocalDateTime dateTimeUpdate, VendedorImages vendedorImagesList) {
        this.brechoNome = brechoNome;
        this.brechoSite = brechoSite;
        this.brechoEndereco = brechoEndereco;
        this.vendedor = vendedor;
        this.DateTimeInsert = dateTimeInsert;
        this.DateTimeUpdate = dateTimeUpdate;
        this.vendedorImagesList = vendedorImagesList;
    }

    public Integer getBrechoId() {
        return this.brechoId;
    }

    public void setBrechoId(Integer brechoId) {
        this.brechoId = brechoId;
    }

    public String getBrechoNome() {
        return this.brechoNome;
    }

    public void setBrechoNome(String brechoNome) {
        this.brechoNome = brechoNome;
    }

    public String getBrechoSite() {
        return this.brechoSite;
    }

    public void setBrechoSite(String brechoSite) {
        this.brechoSite = brechoSite;
    }

    public String getBrechoEndereco() {
        return this.brechoEndereco;
    }

    public void setBrechoEndereco(String brechoEndereco) {
        this.brechoEndereco = brechoEndereco;
    }

    public Vendedor getVendedor() {
        return this.vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public LocalDateTime getDateTimeInsert() {
        return this.DateTimeInsert;
    }

    public void setDateTimeInsert(LocalDateTime DateTimeInsert) {
        this.DateTimeInsert = DateTimeInsert;
    }

    public LocalDateTime getDateTimeUpdate() {
        return this.DateTimeUpdate;
    }

    public void setDateTimeUpdate(LocalDateTime DateTimeUpdate) {
        this.DateTimeUpdate = DateTimeUpdate;
    }

    public VendedorImages getVendedorImage() {
        return this.vendedorImagesList;
    }

    public void setVendedorImage(VendedorImages vendedorImagesList) {
        this.vendedorImagesList = vendedorImagesList;
    }

}
