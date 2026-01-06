package com.projectasks.minimundoproject.dto.request;

import java.math.BigDecimal;

import com.projectasks.minimundoproject.model.Projeto.StatusProjeto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjetoRequest {
    @NotBlank(message = "Nome do projeto é obrigatório")
    private String nome;
    
    private String descricao;
    
    private StatusProjeto status;
    
    private BigDecimal orcamentoDisponivel;
}