package com.projectasks.minimundoproject.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectasks.minimundoproject.dto.request.TarefaRequest;
import com.projectasks.minimundoproject.dto.response.ProjetoResponse;
import com.projectasks.minimundoproject.dto.response.TarefaResponse;
import com.projectasks.minimundoproject.model.Tarefa;
import com.projectasks.minimundoproject.service.ProjetoService;
import com.projectasks.minimundoproject.service.TarefaService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaViewController {

    private final TarefaService tarefaService;
    private final ProjetoService projetoService;

    @GetMapping
    public String tarefas(@RequestParam Long projetoId,
                          @RequestParam(required = false) Tarefa.StatusTarefa status,
                          @RequestParam(required = false) String descricao,
                          HttpSession session,
                          Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";

        try {
            ProjetoResponse projeto = projetoService.findById(projetoId, usuarioId);
            List<TarefaResponse> tarefas = tarefaService.findByProjeto(projetoId, usuarioId, status, descricao);

            // ===============================
            // CÁLCULOS SEM USO DO THYMELEAF - teste
            // ===============================
            int totalTarefas = tarefas.size();
            int tarefasConcluidas = (int) tarefas.stream()
                    .filter(t -> t.getStatus() == Tarefa.StatusTarefa.CONCLUIDA)
                    .count();
            int tarefasPendentes = totalTarefas - tarefasConcluidas;

            model.addAttribute("projeto", projeto);
            model.addAttribute("tarefas", tarefas);
            model.addAttribute("totalTarefas", totalTarefas);
            model.addAttribute("tarefasConcluidas", tarefasConcluidas);
            model.addAttribute("tarefasPendentes", tarefasPendentes);
            model.addAttribute("usuarioNome", session.getAttribute("usuarioNome"));
            model.addAttribute("tarefaRequest", new TarefaRequest());

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "tarefas";
    }

    @PostMapping("/criar")
    public String criar(@ModelAttribute TarefaRequest request,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";
        try {
            tarefaService.create(request, usuarioId);
            redirectAttributes.addFlashAttribute(
                    "success", "Tarefa criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }
        return "redirect:/tarefas?projetoId=" + request.getProjetoId();
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id,
                          @RequestParam Long projetoId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";
        try {
            tarefaService.delete(id, usuarioId);
            redirectAttributes.addFlashAttribute(
                    "success", "Tarefa excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }
        return "redirect:/tarefas?projetoId=" + projetoId;
    }
}