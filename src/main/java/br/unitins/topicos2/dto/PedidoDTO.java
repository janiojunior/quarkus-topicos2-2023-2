package br.unitins.topicos2.dto;

import java.util.List;

public record PedidoDTO (
    // FormaPagamento pagamento,
    // EnderecoDTO endereco,
    List<ItemPedidoDTO> itens
) {

}
