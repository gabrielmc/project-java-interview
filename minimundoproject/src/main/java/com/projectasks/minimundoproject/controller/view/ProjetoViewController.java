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

import com.projectasks.minimundoproject.dto.request.ProjetoRequest;
import com.projectasks.minimundoproject.dto.response.ProjetoResponse;
import com.projectasks.minimundoproject.model.Projeto;
import com.projectasks.minimundoproject.service.ProjetoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoViewController {
    
    private final ProjetoService projetoService;
    
    @GetMapping
    public String projetos(HttpSession session, Model model,
                          @RequestParam(required = false) String nome,
                          @RequestParam(required = false) Projeto.StatusProjeto status) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) 
			return "redirect:/login";
        try {
            List<ProjetoResponse> projetos = projetoService.findAll(usuarioId, status, nome);
            model.addAttribute("projetos", projetos);
            model.addAttribute("usuarioNome", session.getAttribute("usuarioNome"));
            model.addAttribute("projetoRequest", new ProjetoRequest());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "projetos";
    }
    
    @PostMapping("/criar")
    public String criar(@ModelAttribute ProjetoRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
			return "redirect:/login";
        
        try {
            projetoService.create(request, usuarioId);
            redirectAttributes.addFlashAttribute("success", "Projeto criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/projetos";
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
			return "redirect:/login";
        
        try {
            ProjetoResponse projeto = projetoService.findById(id, usuarioId);
            model.addAttribute("projeto", projeto);
            model.addAttribute("usuarioNome", session.getAttribute("usuarioNome"));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "projeto-editar";
    }
    
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id,
                           @ModelAttribute ProjetoRequest request,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
			return "redirect:/login";
        
        try {
            projetoService.update(id, request, usuarioId);
            redirectAttributes.addFlashAttribute("success", "Projeto atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/projetos";
    }
    
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
			return "redirect:/login";
        
        try {
            projetoService.delete(id, usuarioId);
            redirectAttributes.addFlashAttribute("success", "Projeto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/projetos";
    }
}