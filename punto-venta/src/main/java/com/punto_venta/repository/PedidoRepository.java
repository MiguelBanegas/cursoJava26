package com.punto_venta.repository;

import com.punto_venta.model.Pedido;
import com.punto_venta.model.PedidoEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByCreatedAtDesc(Long clienteId);
    List<Pedido> findByEstadoOrderByCreatedAtDesc(PedidoEstado estado);
    List<Pedido> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime desde, LocalDateTime hasta);
}
