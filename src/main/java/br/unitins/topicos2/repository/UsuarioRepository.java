package br.unitins.topicos2.repository;

import java.util.List;

import br.unitins.topicos2.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario>{
    public List<Usuario> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1) ", "%"+nome+"%").list();
    }

    public Usuario findByLogin(String login) {
        return find("login = ?1 ", login).firstResult();
    }

    public Usuario findByLoginAndSenha(String login, String senha){
        if (login == null || senha == null)
            return null;
            
        return find("login = ?1 AND senha = ?2 ", login, senha).firstResult();
    }
}
