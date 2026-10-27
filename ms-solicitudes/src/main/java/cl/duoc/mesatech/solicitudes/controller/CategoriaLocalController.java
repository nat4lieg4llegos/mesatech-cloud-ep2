package cl.duoc.mesatech.solicitudes.controller;

import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import cl.duoc.mesatech.solicitudes.service.CategoriaLocalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Categorías DISPONIBLES para crear solicitudes (copia local recibida por RabbitMQ). */
@RestController
@RequestMapping("/api/v1/categorias-disponibles")
public class CategoriaLocalController {

    private final CategoriaLocalService service;

    public CategoriaLocalController(CategoriaLocalService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoriaLocal> listar() {
        return service.listar();
    }
}
