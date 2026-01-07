package com.projectasks.minimundoproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectasks.minimundoproject.dto.request.ProjetoRequest;
import com.projectasks.minimundoproject.dto.response.ProjetoResponse;
import com.projectasks.minimundoproject.exception.BusinessException;
import com.projectasks.minimundoproject.exception.ResourceNotFoundException;
import com.projectasks.minimundoproject.model.Projeto;
import com.projectasks.minimundoproject.model.Projeto.StatusProjeto;
import com.projectasks.minimundoproject.model.Usuario;
import com.projectasks.minimundoproject.repository.ProjetoRepository;
import com.projectasks.minimundoproject.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cria um novo projeto
     */
    @Transactional
    public ProjetoResponse create(ProjetoRequest request, Long usuarioId) {
        if (usuarioId == null)
            throw new IllegalArgumentException("ID do usuário não pode ser nulo");
        log.info("Criando projeto para usuário ID {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Regra de negócio: nome único por usuário
        if (projetoRepository.existsByNomeAndUsuarioId(request.getNome(), usuarioId)) {
            throw new BusinessException("Já existe um projeto com este nome");
        }

        Projeto projeto = new Projeto();
        projeto.setNome(request.getNome());
        projeto.setDescricao(request.getDescricao());
        projeto.setStatus(
            request.getStatus() != null ? request.getStatus() : StatusProjeto.ATIVO
        );
        projeto.setOrcamentoDisponivel(request.getOrcamentoDisponivel());
        projeto.setUsuario(usuario);

        projeto = projetoRepository.save(projeto);

        log.info("Projeto criado com sucesso: ID {}", projeto.getId());
        return toResponse(projeto);
    }

    /**
     * Lista projetos do usuário com filtros
     */
    @Transactional(readOnly = true)
    public List<ProjetoResponse> findAll(Long usuarioId, StatusProjeto status, String nome) {
        log.info("Listando projetos do usuário ID {}", usuarioId);

        List<Projeto> projetos;

        if (nome != null && !nome.isBlank()) {
            projetos = projetoRepository.findByUsuarioIdAndNomeContaining(usuarioId, nome);
        } else if (status != null) {
            projetos = projetoRepository.findByUsuarioIdAndStatus(usuarioId, status);
        } else {
            projetos = projetoRepository.findByUsuarioId(usuarioId);
        }

        return projetos.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Busca projeto por ID
     */
    @Transactional(readOnly = true)
    public ProjetoResponse findById(Long id, Long usuarioId) {
        if (id == null)
            throw new IllegalArgumentException("IDs do usuário não podem ser nulos");
        log.info("Buscando projeto ID {}", id);
        Projeto projeto = projetoRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para acessar este projeto");
        }

        return toResponse(projeto);
    }

    /**
     * Atualiza um projeto
     */
    @Transactional
    public ProjetoResponse update(Long id, ProjetoRequest request, Long usuarioId) {
        if (id == null)
            throw new IllegalArgumentException("IDs do usuário não podem ser nulos");
        log.info("Atualizando projeto ID {}", id);
        Projeto projeto = projetoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para atualizar este projeto");
        }

        // Regra: nome único
        if (!projeto.getNome().equals(request.getNome())
            && projetoRepository.existsByNomeAndUsuarioId(request.getNome(), usuarioId)) {
            throw new BusinessException("Já existe um projeto com este nome");
        }

        projeto.setNome(request.getNome());
        projeto.setDescricao(request.getDescricao());
        projeto.setOrcamentoDisponivel(request.getOrcamentoDisponivel());

        if (request.getStatus() != null) {
            projeto.setStatus(request.getStatus());
        }

        projeto = projetoRepository.save(projeto);

        log.info("Projeto atualizado com sucesso: ID {}", projeto.getId());
        return toResponse(projeto);
    }

    /**
     * Exclui um projeto
     */
    @Transactional
    public void delete(Long id, Long usuarioId) {
        if (id == null)
            throw new IllegalArgumentException("IDs do usuário não podem ser nulos");
        log.info("Excluindo projeto ID {}", id);
        Projeto projeto = projetoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Você não tem permissão para excluir este projeto");
        }

        if (!projeto.getTarefas().isEmpty()) {
            throw new BusinessException("Não é possível excluir um projeto com tarefas associadas");
        }

        projetoRepository.delete(projeto);
        log.info("Projeto excluído com sucesso: ID {}", id);
    }

    /**
     * Mapper Projeto → ProjetoResponse
     */
    private ProjetoResponse toResponse(Projeto projeto) {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(projeto.getId());
        response.setNome(projeto.getNome());
        response.setDescricao(projeto.getDescricao());
        response.setStatus(projeto.getStatus());
        response.setOrcamentoDisponivel(projeto.getOrcamentoDisponivel());
        response.setCreatedAt(projeto.getCreatedAt());
        response.setUpdatedAt(projeto.getUpdatedAt());
        return response;
    }
}