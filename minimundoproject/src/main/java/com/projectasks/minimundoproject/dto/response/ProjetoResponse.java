package com.projectasks.minimundoproject.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.projectasks.minimundoproject.model.Projeto.StatusProjeto;

import lombok.Data;

@Data
public class ProjetoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private StatusProjeto status;
    private BigDecimal orcamentoDisponivel;
    private Long usuarioId;
    private Integer totalTarefas;
    private Integer tarefasConcluidas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}