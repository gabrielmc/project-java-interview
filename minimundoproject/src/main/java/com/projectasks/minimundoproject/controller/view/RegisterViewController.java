package com.projectasks.minimundoproject.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectasks.minimundoproject.dto.request.RegisterRequest;
import com.projectasks.minimundoproject.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterViewController {
    
    private final AuthService authService;
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, RedirectAttributes redirectAttributes) {
        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("success", 
                "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}