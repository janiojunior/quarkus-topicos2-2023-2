package br.unitins.topicos2.service;

import java.io.File;
import java.io.IOException;

public interface FileService {

    String salvar(byte[] imagem, String nomeImagem) throws IOException;
    
    File download(String nomeArquivo); 
}
