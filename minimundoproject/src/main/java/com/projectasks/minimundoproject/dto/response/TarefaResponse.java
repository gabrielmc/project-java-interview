package com.projectasks.minimundoproject.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.projectasks.minimundoproject.model.Tarefa.StatusTarefa;

import lombok.Data;

@Data
public class TarefaResponse {
    private Long id;
    private String descricao;
    private Long projetoId;
    private String projetoNome;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long tarefaPredecessoraId;
    private String tarefaPredecessoraDescricao;
    private StatusTarefa status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}