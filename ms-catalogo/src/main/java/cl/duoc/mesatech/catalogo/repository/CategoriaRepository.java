package cl.duoc.mesatech.catalogo.repository;

import cl.duoc.mesatech.catalogo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
