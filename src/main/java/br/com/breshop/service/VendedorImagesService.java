package br.com.breshop.service;

import br.com.breshop.entity.VendedorImages;
import br.com.breshop.repository.VendedorImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VendedorImagesService {

    @Autowired
    private VendedorImagesRepository vendedorImagesRepository;

    public String storeFile(MultipartFile file) throws IOException {
        VendedorImages files = new VendedorImages.builder()
                .nome(file.getOriginalFilename())
                .tipo(file.getContentType())
                .data(file.getBytes()).build();

        files = vendedorImagesRepository.save(files);

        if (files.getImagemId() != null) {
            return "Arquivo inserido no banco de dados.";
        }

        return null;
    }
}
