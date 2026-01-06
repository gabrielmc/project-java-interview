package com.projectasks.minimundoproject.controller;

import com.projectasks.minimundoproject.dto.request.TarefaRequest;
import com.projectasks.minimundoproject.dto.response.TarefaResponse;
import com.projectasks.minimundoproject.model.Tarefa.StatusTarefa;
import com.projectasks.minimundoproject.service.TarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TarefaController {
    
    private final TarefaService tarefaService;
    
    @PostMapping
    public ResponseEntity<TarefaResponse> create(
            @Valid @RequestBody TarefaRequest request,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        TarefaResponse response = tarefaService.create(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TarefaResponse>> findByProjeto(
            @RequestParam Long projetoId,
            @RequestParam(required = false) StatusTarefa status,
            @RequestParam(required = false) String descricao,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        List<TarefaResponse> tarefas = tarefaService.findByProjeto(projetoId, usuarioId, status, descricao);
        return ResponseEntity.ok(tarefas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponse> findById(
            @PathVariable Long id,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        TarefaResponse tarefa = tarefaService.findById(id, usuarioId);
        return ResponseEntity.ok(tarefa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TarefaRequest request,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        TarefaResponse response = tarefaService.update(id, request, usuarioId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        tarefaService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}