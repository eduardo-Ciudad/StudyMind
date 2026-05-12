package com.eduardo.studymind.domain.plano;

import com.eduardo.studymind.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Entity
@Table(name = "plano_estudo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PlanoEstudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudoJson;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
