package com.projectasks.minimundoproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectasks.minimundoproject.model.Tarefa;
import com.projectasks.minimundoproject.model.Tarefa.StatusTarefa;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    
    List<Tarefa> findByProjetoId(Long projetoId);
    
    List<Tarefa> findByProjetoIdAndStatus(Long projetoId, StatusTarefa status);
    
    @Query("SELECT t FROM Tarefa t WHERE t.projeto.id = :projetoId AND LOWER(t.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Tarefa> findByProjetoIdAndDescricaoContaining(@Param("projetoId") Long projetoId, @Param("descricao") String descricao);
    
    @Query("SELECT COUNT(t) > 0 FROM Tarefa t WHERE t.tarefaPredecessora.id = :tarefaId")
    boolean existsByTarefaPredecessoraId(@Param("tarefaId") Long tarefaId);
    
    @Query("SELECT t FROM Tarefa t WHERE t.projeto.usuario.id = :usuarioId")
    List<Tarefa> findAllByUsuarioId(@Param("usuarioId") Long usuarioId);
}