package cl.duoc.mesatech.catalogo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorErrores {

    // 400: datos inválidos (no se guarda nada)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        Map<String, Object> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> errores.put(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos", "campos", errores));
    }

    // 409: nombre repetido (no se guarda nada)
    @ExceptionHandler(NombreDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> duplicado(NombreDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    // 503: P7. Se informa honestamente que el registro SÍ quedó guardado, pero sin evento.
    @ExceptionHandler(PublicacionFallidaException.class)
    public ResponseEntity<Map<String, Object>> publicacion(PublicacionFallidaException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "La categoría se guardó en Catálogo, pero no se pudo confirmar la publicación del evento");
        body.put("categoriaGuardada", true);
        body.put("eventoPublicado", false);
        body.put("categoriaId", e.getCategoria().getId());
        body.put("nombre", e.getCategoria().getNombre());
        body.put("eventId", e.getEventId());
        body.put("detalle", "Solicitudes no recibirá esta categoría hasta que se republique manualmente");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
