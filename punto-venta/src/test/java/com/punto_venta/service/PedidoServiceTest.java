package com.punto_venta.service;

import com.punto_venta.dto.PedidoCreateRequestDTO;
import com.punto_venta.dto.PedidoItemRequestDTO;
import com.punto_venta.dto.PedidoResponseDTO;
import com.punto_venta.model.Categoria;
import com.punto_venta.model.Cliente;
import com.punto_venta.model.Pedido;
import com.punto_venta.model.PedidoEstado;
import com.punto_venta.model.PedidoItem;
import com.punto_venta.model.Product;
import com.punto_venta.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProductService productService;

    private PedidoService pedidoService;

    private Cliente cliente;
    private Product product;

    @BeforeEach
    void setUp() {
        pedidoService = new PedidoService(pedidoRepository, clienteService, productService);

        cliente = new Cliente("Juan", "Perez", "12345678");
        cliente.setId(1L);

        Categoria categoria = new Categoria();
        categoria.setId(10L);
        product = new Product("Mouse", 100.0, categoria);
        product.setId(2L);
        product.setStock(10);
    }

    @SuppressWarnings("null")
    @Test
    void createPedidoShouldBuildDraftWithItems() {
        PedidoCreateRequestDTO request = new PedidoCreateRequestDTO();
        request.setClienteId(1L);

        PedidoItemRequestDTO itemRequest = new PedidoItemRequestDTO();
        itemRequest.setProductId(2L);
        itemRequest.setCantidad(2);
        request.setItems(List.of(itemRequest));

        when(clienteService.getClienteActivoById(1L)).thenReturn(cliente);
        when(productService.getProductById(2L)).thenReturn(product);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponseDTO response = pedidoService.createPedido(request);

        assertEquals(PedidoEstado.BORRADOR, response.getEstado());
        assertEquals(1L, response.getClienteId());
        assertEquals(1, response.getItems().size());
        assertEquals(200.0, response.getItems().get(0).getSubtotal().doubleValue());
    }

    @SuppressWarnings("null")
    @Test
    void confirmarPedidoShouldDiscountStock() {
        Pedido pedido = buildPedidoDraft();
        when(pedidoRepository.findById(1L)).thenReturn(java.util.Optional.of(pedido));
        when(productService.getProductById(2L)).thenReturn(product);
        when(productService.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponseDTO response = pedidoService.confirmarPedido(1L);

        assertEquals(PedidoEstado.CONFIRMADO, response.getEstado());
        assertEquals(8, product.getStock());
        verify(productService, times(1)).saveProduct(any(Product.class));
    }

    @SuppressWarnings("null")
    @Test
    void cancelarPedidoConfirmedShouldRestoreStock() {
        Pedido pedido = buildPedidoDraft();
        pedido.setEstado(PedidoEstado.CONFIRMADO);
        when(pedidoRepository.findById(1L)).thenReturn(java.util.Optional.of(pedido));
        when(productService.getProductById(2L)).thenReturn(product);
        when(productService.saveProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponseDTO response = pedidoService.cancelarPedido(1L);

        assertEquals(PedidoEstado.CANCELADO, response.getEstado());
        assertEquals(12, product.getStock());
        verify(productService, times(1)).saveProduct(any(Product.class));
    }

    private Pedido buildPedidoDraft() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setEstado(PedidoEstado.BORRADOR);

        PedidoItem item = new PedidoItem();
        item.setId(1L);
        item.setPedido(pedido);
        item.setProduct(product);
        item.setCantidad(2);
        item.setPrecioUnitario(java.math.BigDecimal.valueOf(100.0));
        item.calcularSubtotal();

        pedido.setItems(new java.util.ArrayList<>());
        pedido.addItem(item);
        pedido.recalcularTotal();
        return pedido;
    }
}
