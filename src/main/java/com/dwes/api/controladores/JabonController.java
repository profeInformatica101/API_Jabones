package com.dwes.api.controladores;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dwes.api.entidades.Ingrediente;
import com.dwes.api.entidades.Jabon;
import com.dwes.api.entidades.enumerados.TipoDePiel;
import com.dwes.api.errores.JabonNotFoundException;
import com.dwes.api.servicios.JabonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Controlador REST para la gestión de jabones.
 * <p>
 * Provee endpoints para crear, actualizar, eliminar y consultar jabones,
 * incluyendo funcionalidades de paginación y filtrado por tipo de piel.
 */
@RestController
@RequestMapping("/api/v1/jabones")
public class JabonController {

    private static final Logger logger = LoggerFactory.getLogger(JabonController.class);

    @Autowired
    private JabonService jabonService;

    /**
     * Obtiene todos los jabones de forma paginada.  
     * Si se proporciona el parámetro "piel", filtra los resultados por {@link TipoDePiel}.
     *
     * @param tipoPiel   Tipo de piel a filtrar (opcional).
     * @param pageable   Parámetros de paginación (page, size, sort).
     * @return           Respuesta HTTP con la página de {@link Jabon} encontrados.
     * @throws IllegalArgumentException Si el tipo de piel proporcionado no es válido.
     */
    @GetMapping
    @Operation(
        summary = "Obtener todos los jabones", 
        description = "Devuelve una lista paginada de jabones"
    )
    @ApiResponse(responseCode = "200", description = "Lista de jabones obtenida exitosamente")
    @ApiResponse(responseCode = "204", description = "No hay jabones disponibles")
    @ApiResponse(responseCode = "400", description = "Parámetros de solicitud incorrectos")
    public ResponseEntity<?> getAllJabones(
            @RequestParam(value = "piel", required = false) String tipoPiel,
            Pageable pageable) {

        logger.info("## getAllJabones ##");

        if (tipoPiel != null) {
            // Filtramos por tipo de piel
            try {
                Page<Jabon> page = getJabonesByTipoDePiel(tipoPiel, pageable);
                return ResponseEntity.ok(page);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Tipo de piel no válido: " + tipoPiel);
            }
        } else {
            // Sin filtro de piel, devolvemos todos
            Page<Jabon> page = jabonService.findAll(pageable);
            return ResponseEntity.ok(page);
        }
    }

    /**
     * Obtiene una página de jabones por tipo de piel.
     *
     * @param tipoPiel  Cadena que representa el tipo de piel (por ejemplo, "SECA", "GRASA").
     * @param pageable  Parámetros de paginación (page, size, sort).
     * @return          Página de {@link Jabon} que cumplen el tipo de piel solicitado.
     * @throws IllegalArgumentException Si el tipo de piel proporcionado no es un valor válido.
     */
    private Page<Jabon> getJabonesByTipoDePiel(String tipoPiel, Pageable pageable) {
        try {
            TipoDePiel tipoDePielEnum = TipoDePiel.valueOf(tipoPiel.toUpperCase());
            return jabonService.findByTipoDePiel(tipoDePielEnum, pageable);
        } catch (IllegalArgumentException e) {
            // Se lanza si el enum no coincide con el valor pasado
            throw new IllegalArgumentException("Tipo de piel no válido: " + tipoPiel);
        }
    }

    /**
     * Obtiene un jabón específico por su ID.
     *
     * @param id Identificador único del jabón.
     * @return   Respuesta HTTP con el {@link Jabon} encontrado.
     * @throws JabonNotFoundException Si no existe un jabón con el ID proporcionado.
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener un jabón por ID", 
        description = "Devuelve un jabón específico por su ID"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Jabón encontrado",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Jabon.class))
    )
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado")
    public ResponseEntity<Jabon> getJabonById(@PathVariable Long id) {
        logger.info("## getJabonById ## id:({})", id);
        Jabon jabon = jabonService.findById(id)
                .orElseThrow(() -> new JabonNotFoundException("Jabón con ID " + id + " no encontrado"));
        return ResponseEntity.ok(jabon);
    }

    /**
     * Obtiene la lista de ingredientes asociados a un jabón por su ID.
     *
     * @param id Identificador único del jabón.
     * @return   Lista de {@link Ingrediente} o estado 404 si no existen ingredientes o el jabón no existe.
     */
    @GetMapping("/{id}/ingredientes")
    @Operation(
        summary = "Obtener ingredientes de un jabón por ID", 
        description = "Devuelve una lista de ingredientes para un jabón específico"
    )
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes encontrada")
    @ApiResponse(responseCode = "404", description = "Ingredientes no encontrados o jabón no existe")
    public ResponseEntity<List<Ingrediente>> getIngredientesByJabonId(@PathVariable Long id) {
        logger.info("## getIngredientesByJabonId ({}) ##", id);
        List<Ingrediente> ingredientes = jabonService.findIngredientesByJabonId(id);

        if (ingredientes.isEmpty()) {
            // Se podría devolver 200 con lista vacía; depende del comportamiento deseado
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ingredientes);
    }

    /**
     * Actualiza un jabón existente.
     *
     * @param id    Identificador único del jabón a actualizar.
     * @param jabon Objeto {@link Jabon} con los nuevos datos.
     * @return      Respuesta HTTP con el jabón actualizado o 404 si no existe el jabón.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un jabón", description = "Actualiza los detalles de un jabón existente")
    @ApiResponse(responseCode = "200", description = "Jabón actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado para actualizar")
    public ResponseEntity<Jabon> actualizarJabon(@PathVariable Long id, @RequestBody Jabon jabon) {
        logger.info("## actualizarJabon id({}) ##", id);

        if (!jabonService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        jabon.setId(id);  
        Jabon jabonActualizado = jabonService.save(jabon);
        return ResponseEntity.ok(jabonActualizado);
    }

    /**
     * Elimina un jabón existente por su ID.
     *
     * @param id Identificador único del jabón a eliminar.
     * @return   Respuesta HTTP sin contenido (204) si se elimina correctamente, o 404 si no existe.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un jabón", description = "Elimina un jabón existente por su ID")
    @ApiResponse(responseCode = "204", description = "Jabón eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado para eliminar")
    public ResponseEntity<Void> borrarJabon(@PathVariable Long id) {
        logger.info("## borrarJabon id:{} ##", id);

        if (!jabonService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        jabonService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza parcialmente los datos de un jabón. Solo modifica
     * los campos presentes en el mapa de actualizaciones.
     *
     * @param id      Identificador único del jabón.
     * @param updates Mapa con las claves y valores de los campos a actualizar.
     * @return        Jabón actualizado parcialmente o 404 si el jabón no existe.
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un jabón", description = "Actualiza parcialmente los detalles de un jabón")
    @ApiResponse(responseCode = "200", description = "Jabón actualizado parcialmente")
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado para actualización parcial")
    public ResponseEntity<Jabon> actualizarParcialmenteJabon(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        if (!jabonService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Jabon jabonActual = jabonService.findById(id)
                .orElseThrow(() -> new JabonNotFoundException("Jabón con ID " + id + " no encontrado"));

        // Campos a actualizar
        if (updates.containsKey("nombre")) {
            jabonActual.setNombre((String) updates.get("nombre"));
        }
        if (updates.containsKey("descripcion")) {
            jabonActual.setDescripcion((String) updates.get("descripcion"));
        }
        if (updates.containsKey("precio")) {
            Double precio = (Double) updates.get("precio");
            jabonActual.setPrecio(precio);
        }

        // Guarda los cambios
        Jabon jabonActualizado = jabonService.save(jabonActual);
        return ResponseEntity.ok(jabonActualizado);
    }

    /**
     * Crea un nuevo jabón y lo guarda en la base de datos.
     *
     * @param nuevoJabon Objeto {@link Jabon} con los datos del nuevo jabón.
     * @return           Respuesta HTTP con el jabón creado y estado 201.
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo jabón", description = "Crea un nuevo jabón y lo guarda en la base de datos")
    @ApiResponse(responseCode = "201", description = "Jabón creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos proporcionados para el nuevo jabón son inválidos")
    public ResponseEntity<Jabon> crearJabon(@RequestBody Jabon nuevoJabon) {
        logger.info("## crearJabon ##");
        // Validaciones y lógica de negocio antes de guardar, si fuera necesario

        // Guarda el nuevo jabón en la BD
        Jabon jabonCreado = jabonService.save(nuevoJabon);

        // Respuesta con estado 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(jabonCreado);
    }
}
