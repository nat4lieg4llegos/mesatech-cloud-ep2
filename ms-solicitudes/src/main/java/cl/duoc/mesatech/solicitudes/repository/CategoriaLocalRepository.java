package cl.duoc.mesatech.solicitudes.repository;

import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaLocalRepository extends JpaRepository<CategoriaLocal, Long> {

    Optional<CategoriaLocal> findByCategoriaId(Long categoriaId);
}
