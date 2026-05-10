package com.eduardo.studymind.infra.security.Utils;

import com.eduardo.studymind.domain.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

public class SecurityUtils {

    public static Usuario getUsuarioAuthenticado(Authentication authentication) {
        return (Usuario) authentication.getPrincipal();
    }


    //Um usuário comum só pode acessar os próprios dados.
    //Um administrador pode acessar os dados de qualquer usuário.
    public static void verificarOwnership(Long usuarioId, Authentication authentication) {
        var usuarioAutenticado = getUsuarioAuthenticado(authentication);
        boolean isAdmin = usuarioAutenticado.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        //Percorre todas as permissões do usuário e verifica se alguma é "ADMIN".


        if (!isAdmin && !usuarioAutenticado.getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }
}
