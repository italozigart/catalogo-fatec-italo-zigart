package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String exibirAuditoria(Model model) {
        model.addAttribute("produtos", produtoService.listarParaAuditoria());
        return "auditoria";
    }
}