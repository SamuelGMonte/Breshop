package br.com.breshop.dto;

import br.com.breshop.entity.VendedorImages;
import org.springframework.web.multipart.MultipartFile;

public record CreateBrechoDto(String brechoNome, String brechoEndereco, String brechoSite, VendedorImages brechoImg) {
}
