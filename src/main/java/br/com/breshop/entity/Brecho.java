package br.com.breshop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "tbl_brecho")
public class Brecho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brecho_id")
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private LocalDateTime dateTimeInsert;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private LocalDateTime dateTimeUpdate;

    @OneToOne(mappedBy = "brechoImg", cascade = CascadeType.ALL)
    private VendedorImages vendedorImagesList;

    @Lob
    @Column(name = "brecho_descricao", length=512)
    private String brechoDescricao;

    public Brecho() {
    }

    public Brecho(String brechoNome, String brechoSite, String brechoEndereco, Vendedor vendedor, LocalDateTime dateTimeInsert, LocalDateTime dateTimeUpdate, VendedorImages vendedorImagesList, String brechoDescricao) {
        this.brechoNome = brechoNome;
        this.brechoSite = brechoSite;
        this.brechoEndereco = brechoEndereco;
        this.vendedor = vendedor;
        this.dateTimeInsert = dateTimeInsert;
        this.dateTimeUpdate = dateTimeUpdate;
        this.vendedorImagesList = vendedorImagesList;
        this.brechoDescricao = brechoDescricao;
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

    public LocalDateTime getdateTimeInsert() {
        return this.dateTimeInsert;
    }

    public void setdateTimeInsert(LocalDateTime dateTimeInsert) {
        this.dateTimeInsert = dateTimeInsert;
    }

    public LocalDateTime getdateTimeUpdate() {
        return this.dateTimeUpdate;
    }

    public void setdateTimeUpdate(LocalDateTime dateTimeUpdate) {
        this.dateTimeUpdate = dateTimeUpdate;
    }

    public VendedorImages getVendedorImage() {
        return this.vendedorImagesList;
    }

    public void setVendedorImage(VendedorImages vendedorImagesList) {
        this.vendedorImagesList = vendedorImagesList;
    }

    public String getBrechoDescricao() {
        return brechoDescricao;
    }

    public void setBrechoDescricao(String brechoDescricao) {
        this.brechoDescricao = brechoDescricao;
    }
}
