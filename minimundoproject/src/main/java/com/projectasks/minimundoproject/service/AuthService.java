package com.projectasks.minimundoproject.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectasks.minimundoproject.dto.request.LoginRequest;
import com.projectasks.minimundoproject.dto.request.RegisterRequest;
import com.projectasks.minimundoproject.dto.response.AuthResponse;
import com.projectasks.minimundoproject.exception.BusinessException;
import com.projectasks.minimundoproject.model.Usuario;
import com.projectasks.minimundoproject.repository.UsuarioRepository;
import com.projectasks.minimundoproject.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Registra um novo usuário no sistema
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro de novo usuário: {}", request.getEmail());
        
        // Valida se o email já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentativa de registro com email já existente: {}", request.getEmail());
            throw new BusinessException("Email já cadastrado no sistema");
        }
        
        // Cria o novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        
        usuario = usuarioRepository.save(usuario);
        log.info("Usuário registrado com sucesso: ID {}", usuario.getId());
        
        // Gera o token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId());
        
        return new AuthResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }
    
    /**
     * Realiza o login do usuário
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Tentativa de login para: {}", request.getEmail());
        
        // Busca o usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado: {}", request.getEmail());
                return new BusinessException("Email ou senha inválidos");
            });
        
        // Valida a senha
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            log.warn("Senha incorreta para o usuário: {}", request.getEmail());
            throw new BusinessException("Email ou senha inválidos");
        }
        
        log.info("Login bem-sucedido para: {}", request.getEmail());
        
        // Gera o token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId());
        
        return new AuthResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }
}