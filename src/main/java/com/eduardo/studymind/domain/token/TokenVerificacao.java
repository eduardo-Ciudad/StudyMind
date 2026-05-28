package com.eduardo.studymind.domain.token;


import com.eduardo.studymind.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens_verificacao")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TokenVerificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime expiracao;

    @Column(nullable = false)
    private Boolean utilizado = false;

    public TokenVerificacao(Usuario usuario){
        this.token = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.expiracao = LocalDateTime.now().plusHours(24);
    }

    public boolean isExpirado(){
        return LocalDateTime.now().isAfter(this.expiracao);
    }

    public void marcarComoUtilizado(){
        this.utilizado = true;
    }
}
