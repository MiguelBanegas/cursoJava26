package com.punto_venta.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.punto_venta.exception.ClienteNotFoundException;
import com.punto_venta.model.Cliente;
import com.punto_venta.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @SuppressWarnings("removal")
    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    void setUp() {
        cliente1 = new Cliente("Juan", "Perez", "12345678");
        cliente1.setId(1L);
        cliente1.setEmail("juan@example.com");
        cliente1.setTelefono("11223344");
        cliente1.setDireccion("Calle Falsa 123");

        cliente2 = new Cliente("Maria", "Gomez", "87654321");
        cliente2.setId(2L);
        cliente2.setEmail("maria@example.com");
    }

    @SuppressWarnings("null")
    @Test
    void testGetAllClientes() throws Exception {
        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);
        when(clienteService.getAllClientes()).thenReturn(clientes);

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")))
                .andExpect(jsonPath("$[1].nombre", is("Maria")));

        verify(clienteService, times(1)).getAllClientes();
    }

    @SuppressWarnings("null")
    @Test
    void testGetClienteById_Success() throws Exception {
        when(clienteService.getClienteById(1L)).thenReturn(cliente1);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.apellido", is("Perez")))
                .andExpect(jsonPath("$.dni", is("12345678")));

        verify(clienteService, times(1)).getClienteById(1L);
    }

    @SuppressWarnings("null")
    @Test
    void testGetClienteById_NotFound() throws Exception {
        when(clienteService.getClienteById(99L)).thenThrow(new ClienteNotFoundException("Cliente con ID 99 no encontrado"));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Cliente con ID 99 no encontrado")));

        verify(clienteService, times(1)).getClienteById(99L);
    }

    @SuppressWarnings("null")
    @Test
    void testGetClienteByDni_Success() throws Exception {
        when(clienteService.getClienteByDni("12345678")).thenReturn(cliente1);

        mockMvc.perform(get("/clientes/dni/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni", is("12345678")))
                .andExpect(jsonPath("$.nombre", is("Juan")));

        verify(clienteService, times(1)).getClienteByDni("12345678");
    }

    @SuppressWarnings("null")
    @Test
    void testAddCliente_Success() throws Exception {
        Cliente newCliente = new Cliente("Carlos", "Lopez", "11223344");
        Cliente savedCliente = new Cliente("Carlos", "Lopez", "11223344");
        savedCliente.setId(3L);

        when(clienteService.addCliente(any(Cliente.class))).thenReturn(savedCliente);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Carlos")))
                .andExpect(jsonPath("$.dni", is("11223344")));

        verify(clienteService, times(1)).addCliente(any(Cliente.class));
    }

    @SuppressWarnings("null")
    @Test
    void testAddCliente_ValidationError() throws Exception {
        // Enviar un cliente sin apellido y DNI vacío para forzar error de validación
        Cliente invalidCliente = new Cliente("A", "", "");

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCliente)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Error de validación en los campos")));

        verify(clienteService, never()).addCliente(any(Cliente.class));
    }

    @SuppressWarnings("null")
    @Test
    void testUpdateCliente() throws Exception {
        Cliente updatedInfo = new Cliente("Juan Carlos", "Perez Gomez", "12345678");
        Cliente updatedCliente = new Cliente("Juan Carlos", "Perez Gomez", "12345678");
        updatedCliente.setId(1L);

        when(clienteService.updateCliente(eq(1L), any(Cliente.class))).thenReturn(updatedCliente);

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Carlos")))
                .andExpect(jsonPath("$.apellido", is("Perez Gomez")));

        verify(clienteService, times(1)).updateCliente(eq(1L), any(Cliente.class));
    }

    @SuppressWarnings("null")
    @Test
    void testDeleteCliente() throws Exception {
        doNothing().when(clienteService).deleteCliente(1L);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Cliente con ID 1 eliminado exitosamente")));

        verify(clienteService, times(1)).deleteCliente(1L);
    }
}
