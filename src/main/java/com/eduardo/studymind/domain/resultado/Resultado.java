package com.eduardo.studymind.domain.resultado;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resultados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RespostaStatus status;

    @Column(length = 1000)
    private String respostaUsuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime respondidoEm = LocalDateTime.now();
}
