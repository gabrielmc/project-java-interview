package com.projectasks.minimundoproject.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectasks.minimundoproject.dto.request.ProjetoRequest;
import com.projectasks.minimundoproject.dto.response.ProjetoResponse;
import com.projectasks.minimundoproject.model.Projeto.StatusProjeto;
import com.projectasks.minimundoproject.service.ProjetoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjetoController {
    
    private final ProjetoService projetoService;
    
    @PostMapping
    public ResponseEntity<ProjetoResponse> create(
            @Valid @RequestBody ProjetoRequest request,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        ProjetoResponse response = projetoService.create(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProjetoResponse>> findAll(
            @RequestParam(required = false) StatusProjeto status,
            @RequestParam(required = false) String nome,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        List<ProjetoResponse> projetos = projetoService.findAll(usuarioId, status, nome);
        return ResponseEntity.ok(projetos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponse> findById(
            @PathVariable Long id,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        ProjetoResponse projeto = projetoService.findById(id, usuarioId);
        return ResponseEntity.ok(projeto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoRequest request,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        ProjetoResponse response = projetoService.update(id, request, usuarioId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        projetoService.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
