package cl.duoc.mesatech.catalogo.controller;

import cl.duoc.mesatech.catalogo.dto.CategoriaRequest;
import cl.duoc.mesatech.catalogo.entity.Categoria;
import cl.duoc.mesatech.catalogo.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // 201: guardada en RDS y evento confirmado por RabbitMQ
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody CategoriaRequest request) {
        Categoria c = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "categoriaId", c.getId(),
                "nombre", c.getNombre(),
                "fechaCreacion", c.getFechaCreacion().toString(),
                "eventoPublicado", true,
                "mensaje", "Categoría creada. Estará disponible en Solicitudes en unos segundos."));
    }

    @GetMapping
    public List<Categoria> listar() {
        return service.listar();
    }
}
