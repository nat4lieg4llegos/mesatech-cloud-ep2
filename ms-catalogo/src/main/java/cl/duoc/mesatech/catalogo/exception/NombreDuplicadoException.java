package cl.duoc.mesatech.catalogo.exception;

public class NombreDuplicadoException extends RuntimeException {

    public NombreDuplicadoException(String nombre) {
        super("Ya existe una categoría con el nombre '" + nombre + "'");
    }
}
