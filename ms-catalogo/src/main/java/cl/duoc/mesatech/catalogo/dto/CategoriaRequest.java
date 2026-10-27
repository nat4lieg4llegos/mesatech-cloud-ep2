package cl.duoc.mesatech.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo del POST /api/v1/categorias */
public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion
) {
}
