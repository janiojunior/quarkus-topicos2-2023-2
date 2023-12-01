package br.unitins.topicos2.service;

import java.util.List;

import br.unitins.topicos2.dto.FaixaDTO;
import br.unitins.topicos2.dto.FaixaResponseDTO;
import jakarta.validation.Valid;

public interface FaixaService {

        // recursos basicos
        List<FaixaResponseDTO> getAll(int page, int pageSize);

        FaixaResponseDTO findById(Long id);
    
        FaixaResponseDTO create(@Valid FaixaDTO dto);
    
        FaixaResponseDTO update(Long id, FaixaDTO dto);

        FaixaResponseDTO salveImage(Long id, String nomeImagem);
    
        void delete(Long id);
    
        // recursos extras
    
        List<FaixaResponseDTO> findByNome(String nome, int page, int pageSize);
    
        long count();

        long countByNome(String nome);

        byte[] createReportFaixas(String filterNome);

    
}