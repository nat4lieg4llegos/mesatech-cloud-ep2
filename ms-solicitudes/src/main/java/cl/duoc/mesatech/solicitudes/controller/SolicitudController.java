package cl.duoc.mesatech.solicitudes.controller;

import cl.duoc.mesatech.solicitudes.dto.CambioEstadoRequest;
import cl.duoc.mesatech.solicitudes.dto.SolicitudRequest;
import cl.duoc.mesatech.solicitudes.entity.Solicitud;
import cl.duoc.mesatech.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud crear(@Valid @RequestBody SolicitudRequest request) {
        return service.crear(request);
    }

    @GetMapping
    public List<Solicitud> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Solicitud obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    public Solicitud cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return service.cambiarEstado(id, request.estado());
    }
}
