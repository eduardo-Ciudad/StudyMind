package com.eduardo.studymind.domain.resultado;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resultado_sessoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ResultadoSessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id")
    private Topico topico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @Column(nullable = false, length = 150)
    private String topicoNome;

    @Column(nullable = false, length = 100)
    private String materiaNome;

    @Column(nullable = false)
    private Integer totalQuestoes;

    @Column(nullable = false)
    private Integer acertos;

    @Column(nullable = false)
    private Double taxaAcerto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime respondidoEm = LocalDateTime.now();
}
