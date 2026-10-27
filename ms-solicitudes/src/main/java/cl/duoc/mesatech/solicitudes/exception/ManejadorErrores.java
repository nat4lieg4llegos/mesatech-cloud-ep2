package cl.duoc.mesatech.solicitudes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        Map<String, Object> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> errores.put(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos", "campos", errores));
    }

    // Ej: estado inexistente en el JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("error",
                "Cuerpo inválido. Estados permitidos: ABIERTA, EN_PROCESO, RESUELTA"));
    }

    // 422: categoría aún no recibida o transición de estado no permitida
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> regla(ReglaNegocioException e) {
        return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(NoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
}
