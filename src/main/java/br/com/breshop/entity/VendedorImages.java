package br.com.breshop.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_vendedor_imgs")
public class VendedorImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imagemId;

    @Column(name = "img_nome")
    private String imgNome;

    @Column(name = "img_tipo")
    private String imgTipo;

    @Lob
    @Column(name = "img_data", columnDefinition = "MEDIUMBLOB")
    private byte[] imgData;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Vendedor vendedor;

    @OneToOne
    @JoinColumn(name = "brecho_id")
    private Brecho brechoImg;

    public VendedorImages() {
    }

    public VendedorImages(Integer imagemId, String imgNome, String imgTipo, byte[] imgData, Brecho brechoImg, Vendedor vendedor) {
        this.imagemId = imagemId;
        this.imgNome = imgNome;
        this.imgTipo = imgTipo;
        this.imgData = imgData;
        this.brechoImg = brechoImg;
        this.vendedor = vendedor;
    }

    public static class builder {
        private Integer imagemId;
        private String imgNome;
        private String imgTipo;
        private byte[] imgData;
        private Brecho brechoImg;
        private Vendedor vendedor;

        public builder nome(String imgNome) {
            this.imgNome = imgNome;
            return this;
        }

        public builder tipo(String imgTipo) {
            this.imgTipo = imgTipo;
            return this;
        }

        public builder data(byte[] imgData) {
            this.imgData = imgData;
            return this;
        }

        public builder brechoImg(Brecho brechoImg) {
            this.brechoImg = brechoImg;
            return this;
        }

        public builder vendedor(Vendedor vendedor) {
            this.vendedor = vendedor;
            return this;
        }

        public VendedorImages build() {
            return new VendedorImages(imagemId, imgNome, imgTipo, imgData, brechoImg, vendedor);
        }
    }

    public Integer getImagemId() {
        return imagemId;
    }

    public void setImagemId(Integer imagemId) {
        this.imagemId = imagemId;
    }

    public String getImgNome() {
        return imgNome;
    }

    public void setImgNome(String imgNome) {
        this.imgNome = imgNome;
    }

    public String getImgTipo() {
        return imgTipo;
    }

    public void setImgTipo(String imgTipo) {
        this.imgTipo = imgTipo;
    }

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }


    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Brecho getBrechoImg() {
        return brechoImg;
    }

    public void setBrechoImg(Brecho brechoImg) {
        this.brechoImg = brechoImg;
    }
}