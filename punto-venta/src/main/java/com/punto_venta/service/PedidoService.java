package com.punto_venta.service;

import com.punto_venta.dto.PedidoCreateRequestDTO;
import com.punto_venta.dto.PedidoItemRequestDTO;
import com.punto_venta.dto.PedidoItemResponseDTO;
import com.punto_venta.dto.PedidoResponseDTO;
import com.punto_venta.exception.InsufficientStockException;
import com.punto_venta.model.Cliente;
import com.punto_venta.model.Pedido;
import com.punto_venta.model.PedidoEstado;
import com.punto_venta.model.PedidoItem;
import com.punto_venta.model.Product;
import com.punto_venta.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProductService productService;

    public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService, ProductService productService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.productService = productService;
    }

    @Transactional
    public PedidoResponseDTO createPedido(PedidoCreateRequestDTO request) {
        Cliente cliente = clienteService.getClienteActivoById(request.getClienteId());
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado(PedidoEstado.BORRADOR);
        pedido.setItems(new ArrayList<>());

        for (PedidoItemRequestDTO itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());
            PedidoItem item = new PedidoItem();
            item.setProduct(product);
            item.setCantidad(itemRequest.getCantidad());
            item.setPrecioUnitario(BigDecimal.valueOf(product.getPrice()));
            item.calcularSubtotal();
            pedido.addItem(item);
        }

        pedido.recalcularTotal();
        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO confirmarPedido(Long pedidoId) {
        Pedido pedido = getPedidoById(pedidoId);
        if (pedido.getEstado() == PedidoEstado.CONFIRMADO) {
            return toResponse(pedido);
        }
        if (pedido.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalArgumentException("El pedido ya fue cancelado");
        }

        for (PedidoItem item : pedido.getItems()) {
            Product product = productService.getProductById(item.getProduct().getId());
            if (product.getStock() == null) {
                product.setStock(0);
            }
            if (product.getStock() < item.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el producto " + product.getName());
            }
            product.setStock(product.getStock() - item.getCantidad());
            productService.saveProduct(product);
        }

        pedido.setEstado(PedidoEstado.CONFIRMADO);
        pedido.setConfirmedAt(LocalDateTime.now());
        pedido.recalcularTotal();
        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long pedidoId) {
        Pedido pedido = getPedidoById(pedidoId);
        if (pedido.getEstado() == PedidoEstado.CANCELADO) {
            return toResponse(pedido);
        }

        if (pedido.getEstado() == PedidoEstado.CONFIRMADO) {
            for (PedidoItem item : pedido.getItems()) {
                Product product = productService.getProductById(item.getProduct().getId());
                if (product.getStock() == null) {
                    product.setStock(0);
                }
                product.setStock(product.getStock() + item.getCantidad());
                productService.saveProduct(product);
            }
        }

        pedido.setEstado(PedidoEstado.CANCELADO);
        return toResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO getPedido(Long pedidoId) {
        return toResponse(getPedidoById(pedidoId));
    }

    @Transactional
    public List<PedidoResponseDTO> getPedidos() {
        return pedidoRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<PedidoResponseDTO> getPedidosByCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByCreatedAtDesc(clienteId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<PedidoResponseDTO> getPedidosByEstado(PedidoEstado estado) {
        return pedidoRepository.findByEstadoOrderByCreatedAtDesc(estado).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<PedidoResponseDTO> getPedidosByRangoFecha(LocalDateTime desde, LocalDateTime hasta) {
        return pedidoRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(desde, hasta).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Pedido getPedidoById(Long pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo");
        }
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido con ID " + pedidoId + " no encontrado"));
    }

    private PedidoResponseDTO toResponse(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setClienteId(pedido.getCliente().getId());
        response.setClienteNombre(pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
        response.setEstado(pedido.getEstado());
        response.setCreatedAt(pedido.getCreatedAt());
        response.setUpdatedAt(pedido.getUpdatedAt());
        response.setConfirmedAt(pedido.getConfirmedAt());
        response.setTotal(pedido.getTotal());

        List<PedidoItemResponseDTO> itemResponses = new ArrayList<>();
        for (PedidoItem item : pedido.getItems()) {
            PedidoItemResponseDTO itemResponse = new PedidoItemResponseDTO();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setCantidad(item.getCantidad());
            itemResponse.setPrecioUnitario(item.getPrecioUnitario());
            itemResponse.setSubtotal(item.getSubtotal());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);
        return response;
    }
}
