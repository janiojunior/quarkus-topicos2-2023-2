package br.unitins.topicos2.service;

import java.util.List;

import br.unitins.topicos2.dto.PedidoDTO;
import br.unitins.topicos2.dto.PedidoResponseDTO;

public interface PedidoService {

        public PedidoResponseDTO insert(PedidoDTO dto, String login);
        public PedidoResponseDTO findById(Long id);
        public List<PedidoResponseDTO> findByAll();
        public List<PedidoResponseDTO> findByAll(String login);
}
