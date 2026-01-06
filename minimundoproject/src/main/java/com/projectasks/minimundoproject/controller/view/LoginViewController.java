package com.projectasks.minimundoproject.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectasks.minimundoproject.dto.request.LoginRequest;
import com.projectasks.minimundoproject.dto.response.AuthResponse;
import com.projectasks.minimundoproject.service.AuthService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginViewController {
    
    private final AuthService authService;
    
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            AuthResponse response = authService.login(request);
            // Salva dados na sessão
            session.setAttribute("token", response.getToken());
            session.setAttribute("usuarioId", response.getUsuarioId());
            session.setAttribute("usuarioNome", response.getNome());
            session.setAttribute("usuarioEmail", response.getEmail());
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}