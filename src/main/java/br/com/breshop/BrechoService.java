package br.com.breshop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.breshop.repository.BrechoRepository;

@Service
public class BrechoService {
    private final BrechoRepository brechoRepository;


    @Autowired
    BrechoService(BrechoRepository brechoRepository) {
        this.brechoRepository = brechoRepository;
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

}
