package br.com.breshop;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.breshop.entity.Vendedor;
import br.com.breshop.entity.VendedorImages;
import br.com.breshop.repository.EnabledVendedorImagesRepository;
import br.com.breshop.repository.VendedorImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.breshop.entity.Brecho;
import br.com.breshop.repository.BrechoRepository;

@Service
public class BrechoService {
    private final BrechoRepository brechoRepository;
    private final VendedorImagesRepository vendedorImagesRepository;
    private final EnabledVendedorImagesRepository enabledVendedorImagesRepository;

    @Autowired
    BrechoService(BrechoRepository brechoRepository, VendedorImagesRepository vendedorImagesRepository, EnabledVendedorImagesRepository enabledVendedorImagesRepository) {
        this.brechoRepository = brechoRepository;
        this.vendedorImagesRepository = vendedorImagesRepository;
        this.enabledVendedorImagesRepository = enabledVendedorImagesRepository;
    }

    public List<Brecho> getBrechosByVendedorId(Integer vendedorId) {
        return brechoRepository.findByVendedorId(vendedorId);
    }

    public List<String> getAllBrechosNomes() {
        List<String> brechoOptional = brechoRepository.findAllByBrechoNome();
        return brechoOptional.stream()
        .collect(Collectors.toList());
    }

    public List<String> getAllBrechoEnderecos() {
        List<String> brechoOptional = brechoRepository.findAllByBrechoEndereco();
        return brechoOptional.stream()
        .collect(Collectors.toList());
    }

    public List<String> getAllBrechoSites() {
        List<String> brechoOptional = brechoRepository.findAllByBrechoSite();
        return brechoOptional.stream()
        .collect(Collectors.toList());
    }

    public byte[] getBrechoImg(Vendedor vendedor) {
        Optional<VendedorImages> brechoOptional = vendedorImagesRepository.findByVendedor(vendedor);
        return brechoOptional.map(VendedorImages::getImgData)
                .orElse(new byte[0]);
    }

    public boolean checkVerifiedImage(byte[] vendedor) {
        return enabledVendedorImagesRepository.isVerified(vendedor);
    }

    public List<byte[]> getAllBrechoImgs() {
        return vendedorImagesRepository.findAllByBrechoSite();
    }

}
