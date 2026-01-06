package com.projectasks.minimundoproject.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projectasks.minimundoproject.dto.response.ProjetoResponse;
import com.projectasks.minimundoproject.service.ProjetoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeViewController {
    
    private final ProjetoService projetoService;
    
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNome = (String) session.getAttribute("usuarioNome");
        if (usuarioId == null)
            return "redirect:/login";
        
        try {
            List<ProjetoResponse> projetos = projetoService.findAll(usuarioId, null, null);
            
            int totalProjetos = projetos.size();
            int totalTarefas = projetos.stream()
                .mapToInt(ProjetoResponse::getTotalTarefas)
                .sum();
            int tarefasConcluidas = projetos.stream()
                .mapToInt(ProjetoResponse::getTarefasConcluidas)
                .sum();
            
            model.addAttribute("usuarioNome", usuarioNome);
            model.addAttribute("totalProjetos", totalProjetos);
            model.addAttribute("totalTarefas", totalTarefas);
            model.addAttribute("tarefasConcluidas", tarefasConcluidas);
            model.addAttribute("projetos", projetos);
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "home";
    }
}