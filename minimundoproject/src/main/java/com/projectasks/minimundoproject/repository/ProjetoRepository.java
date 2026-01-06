package com.projectasks.minimundoproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectasks.minimundoproject.model.Projeto;
import com.projectasks.minimundoproject.model.Projeto.StatusProjeto;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    
    List<Projeto> findByUsuarioId(Long usuarioId);
    
    List<Projeto> findByUsuarioIdAndStatus(Long usuarioId, StatusProjeto status);
    
    @Query("SELECT p FROM Projeto p WHERE p.usuario.id = :usuarioId AND LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Projeto> findByUsuarioIdAndNomeContaining(@Param("usuarioId") Long usuarioId, @Param("nome") String nome);
    
    boolean existsByNomeAndUsuarioId(String nome, Long usuarioId);
    
    @Query("SELECT p FROM Projeto p LEFT JOIN FETCH p.tarefas WHERE p.id = :id")
    Optional<Projeto> findByIdWithTarefas(@Param("id") Long id);
}