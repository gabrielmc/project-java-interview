package com.projectasks.minimundoproject.dto.request;

import java.time.LocalDate;

import com.projectasks.minimundoproject.model.Tarefa.StatusTarefa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TarefaRequest {
    @NotBlank(message = "Descrição da tarefa é obrigatória")
    private String descricao;
    
    @NotNull(message = "Projeto é obrigatório")
    private Long projetoId;
    
    private LocalDate dataInicio;
    
    private LocalDate dataFim;
    
    private Long tarefaPredecessoraId;
    
    private StatusTarefa status;
}