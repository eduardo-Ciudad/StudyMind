package com.eduardo.studymind.dto.output.tarefadescricaooutput;

import java.util.List;

public record DadosTarefaDescricao(
        String titulo,
        String descricaoDetalhada,
        List<String> passos
) {
}
