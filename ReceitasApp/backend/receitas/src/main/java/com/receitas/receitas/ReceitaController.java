package com.receitas.receitas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receitas")
public class ReceitaController {

    @Autowired
    private ReceitaRepository repository;

    @GetMapping
    public List<Receita> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Receita criar(@RequestBody Receita receita) {
        return repository.save(receita);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receita> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Receita> atualizar(@PathVariable Long id, @RequestBody Receita novaReceita) {
    return repository.findById(id)
        .map(receitaExistente -> {
            receitaExistente.setNome(novaReceita.getNome());
            receitaExistente.setDescricao(novaReceita.getDescricao());
            receitaExistente.setIngredientes(novaReceita.getIngredientes());
            receitaExistente.setModoDePreparo(novaReceita.getModoDePreparo());
            Receita atualizada = repository.save(receitaExistente);
            return ResponseEntity.ok(atualizada);
        })
        .orElse(ResponseEntity.notFound().build());
}
    
}
