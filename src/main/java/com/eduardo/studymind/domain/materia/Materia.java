package com.eduardo.studymind.domain.materia;

import com.eduardo.studymind.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativa = true;
}
