package com.projectasks.minimundoproject.service;

import com.projectasks.minimundoproject.dto.request.TarefaRequest;
import com.projectasks.minimundoproject.dto.response.TarefaResponse;
import com.projectasks.minimundoproject.exception.BusinessException;
import com.projectasks.minimundoproject.exception.ResourceNotFoundException;
import com.projectasks.minimundoproject.model.Projeto;
import com.projectasks.minimundoproject.model.Tarefa;
import com.projectasks.minimundoproject.model.Tarefa.StatusTarefa;
import com.projectasks.minimundoproject.repository.ProjetoRepository;
import com.projectasks.minimundoproject.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TarefaService {
    
    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    
    /**
     * Cria uma nova tarefa
     */
    @Transactional
    public TarefaResponse create(TarefaRequest request, Long usuarioId) {
        log.info("Criando nova tarefa para projeto ID: {}", request.getProjetoId());
        
        // Valida se o projeto existe e pertence ao usuário
        Projeto projeto = projetoRepository.findById(request.getProjetoId())
            .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));
        
        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para criar tarefas neste projeto");
        }
        
        // Valida datas
        if (request.getDataInicio() != null && request.getDataFim() != null) {
            if (request.getDataFim().isBefore(request.getDataInicio())) {
                throw new BusinessException("A data de fim não pode ser anterior à data de início");
            }
        }
        
        // Valida tarefa predecessora
        Tarefa tarefaPredecessora = null;
        if (request.getTarefaPredecessoraId() != null) {
            tarefaPredecessora = tarefaRepository.findById(request.getTarefaPredecessoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa predecessora não encontrada"));
            
            // Valida se a predecessora pertence ao mesmo projeto
            if (!tarefaPredecessora.getProjeto().getId().equals(request.getProjetoId())) {
                throw new BusinessException("A tarefa predecessora deve pertencer ao mesmo projeto");
            }
        }
        
        // Cria a tarefa
        Tarefa tarefa = new Tarefa();
        tarefa.setDescricao(request.getDescricao());
        tarefa.setProjeto(projeto);
        tarefa.setDataInicio(request.getDataInicio());
        tarefa.setDataFim(request.getDataFim());
        tarefa.setTarefaPredecessora(tarefaPredecessora);
        tarefa.setStatus(request.getStatus() != null ? request.getStatus() : StatusTarefa.NAO_CONCLUIDA);
        
        tarefa = tarefaRepository.save(tarefa);
        log.info("Tarefa criada com sucesso: ID {}", tarefa.getId());
        
        return toResponse(tarefa);
    }
    
    /**
     * Lista todas as tarefas de um projeto
     */
    @Transactional(readOnly = true)
    public List<TarefaResponse> findByProjeto(Long projetoId, Long usuarioId, StatusTarefa status, String descricao) {
        log.info("Listando tarefas do projeto ID: {}", projetoId);
        
        // Valida se o projeto existe e pertence ao usuário
        Projeto projeto = projetoRepository.findById(projetoId)
            .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));
        
        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para acessar as tarefas deste projeto");
        }
        
        List<Tarefa> tarefas;
        
        if (descricao != null && !descricao.isEmpty()) {
            tarefas = tarefaRepository.findByProjetoIdAndDescricaoContaining(projetoId, descricao);
        } else if (status != null) {
            tarefas = tarefaRepository.findByProjetoIdAndStatus(projetoId, status);
        } else {
            tarefas = tarefaRepository.findByProjetoId(projetoId);
        }
        
        return tarefas.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca uma tarefa por ID
     */
    @Transactional(readOnly = true)
    public TarefaResponse findById(Long id, Long usuarioId) {
        log.info("Buscando tarefa ID: {}", id);
        
        Tarefa tarefa = tarefaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        
        // Valida se a tarefa pertence a um projeto do usuário
        if (!tarefa.getProjeto().getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para acessar esta tarefa");
        }
        
        return toResponse(tarefa);
    }
    
    /**
     * Atualiza uma tarefa
     */
    @Transactional
    public TarefaResponse update(Long id, TarefaRequest request, Long usuarioId) {
        log.info("Atualizando tarefa ID: {}", id);
        
        Tarefa tarefa = tarefaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        
        // Valida se a tarefa pertence a um projeto do usuário
        if (!tarefa.getProjeto().getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para atualizar esta tarefa");
        }
        
        // Valida datas
        if (request.getDataInicio() != null && request.getDataFim() != null) {
            if (request.getDataFim().isBefore(request.getDataInicio())) {
                throw new BusinessException("A data de fim não pode ser anterior à data de início");
            }
        }
        
        // Valida e atualiza tarefa predecessora
        if (request.getTarefaPredecessoraId() != null) {
            if (request.getTarefaPredecessoraId().equals(id)) {
                throw new BusinessException("Uma tarefa não pode ser predecessora de si mesma");
            }
            
            Tarefa tarefaPredecessora = tarefaRepository.findById(request.getTarefaPredecessoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa predecessora não encontrada"));
            
            if (!tarefaPredecessora.getProjeto().getId().equals(tarefa.getProjeto().getId())) {
                throw new BusinessException("A tarefa predecessora deve pertencer ao mesmo projeto");
            }
            
            tarefa.setTarefaPredecessora(tarefaPredecessora);
        } else {
            tarefa.setTarefaPredecessora(null);
        }
        
        // Atualiza os dados
        tarefa.setDescricao(request.getDescricao());
        tarefa.setDataInicio(request.getDataInicio());
        tarefa.setDataFim(request.getDataFim());
        if (request.getStatus() != null) {
            tarefa.setStatus(request.getStatus());
        }
        
        tarefa = tarefaRepository.save(tarefa);
        log.info("Tarefa atualizada com sucesso: ID {}", tarefa.getId());
        
        return toResponse(tarefa);
    }
    
    /**
     * Exclui uma tarefa
     */
    @Transactional
    public void delete(Long id, Long usuarioId) {
        log.info("Excluindo tarefa ID: {}", id);
        
        Tarefa tarefa = tarefaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
        
        // Valida se a tarefa pertence a um projeto do usuário
        if (!tarefa.getProjeto().getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para excluir esta tarefa");
        }
        
        // Valida se a tarefa é predecessora de outra
        if (tarefaRepository.existsByTarefaPredecessoraId(id)) {
            throw new BusinessException("Não é possível excluir uma tarefa que é predecessora de outra");
        }
        
        tarefaRepository.delete(tarefa);
        log.info("Tarefa excluída com sucesso: ID {}", id);
    }
    
    /**
     * Converte Tarefa para TarefaResponse
     */
    private TarefaResponse toResponse(Tarefa tarefa) {
        TarefaResponse response = new TarefaResponse();
        response.setId(tarefa.getId());
        response.setDescricao(tarefa.getDescricao());
        response.setProjetoId(tarefa.getProjeto().getId());
        response.setProjetoNome(tarefa.getProjeto().getNome());
        response.setDataInicio(tarefa.getDataInicio());
        response.setDataFim(tarefa.getDataFim());
        
        if (tarefa.getTarefaPredecessora() != null) {
            response.setTarefaPredecessoraId(tarefa.getTarefaPredecessora().getId());
            response.setTarefaPredecessoraDescricao(tarefa.getTarefaPredecessora().getDescricao());
        }
        
        response.setStatus(tarefa.getStatus());
        response.setCreatedAt(tarefa.getCreatedAt());
        response.setUpdatedAt(tarefa.getUpdatedAt());
        
        return response;
    }
}