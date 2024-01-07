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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dwes.api.entidades.Ingrediente;
import com.dwes.api.entidades.Jabon;
import com.dwes.api.entidades.enumerados.TipoDePiel;
import com.dwes.api.errores.JabonNotFoundException;
import com.dwes.api.servicios.JabonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1/jabones")
public class JabonController {

    private static final Logger logger = LoggerFactory.getLogger(JabonController.class);

	
    @Autowired
    private JabonService jabonService;

   /**
    * 
    * @param tipoPiel
    * @param pageable
    * @return
    */
    @GetMapping
    @Operation(summary = "Obtener todos los jabones", description = "Devuelve una lista paginada de jabones")
    @ApiResponse(responseCode = "200", description = "Lista de jabones obtenida exitosamente")
    @ApiResponse(responseCode = "204", description = "No hay jabones disponibles")
    @ApiResponse(responseCode = "400", description = "Parámetros de solicitud incorrectos")
    public ResponseEntity<?> getAllJabones(
            @RequestParam(value = "piel", required = false) String tipoPiel, 
            Pageable pageable) {

        logger.info("## getAllJabones ##");
     
        if (tipoPiel != null) {
            try {
            	   Page<Jabon> page = (tipoPiel != null) ? getJabonesByTipoDePiel(tipoPiel, pageable) : jabonService.findAll(pageable);
                   return ResponseEntity.ok(page);
            } catch (IllegalArgumentException e) {
            	throw new IllegalArgumentException("Tipo de piel no válido: " + tipoPiel);
              
            }
        } else {
            Page<Jabon> page = jabonService.findAll(pageable);
            return ResponseEntity.ok(page);
        }
    }
    
    /**
     * 
     * @param tipoPiel
     * @param pageable
     * @return
     */
    private Page<Jabon> getJabonesByTipoDePiel(String tipoPiel, Pageable pageable) {
        try {
            TipoDePiel tipoDePielEnum = TipoDePiel.valueOf(tipoPiel.toUpperCase());
            return jabonService.findByTipoDePiel(tipoDePielEnum, pageable);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de piel no válido: " + tipoPiel);
        }
    }

    /**
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un jabón por ID", description = "Devuelve un jabón específico por su ID")
    @ApiResponse(responseCode = "200", description = "Jabón encontrado",
        content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Jabon.class)) })
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado")
    public ResponseEntity<Jabon> getJabonById(@PathVariable Long id) {
        logger.info("## getJabonById ## id:({}) " + id);
        Jabon jabon = jabonService.findById(id)
            .orElseThrow(() -> new JabonNotFoundException("Jabón con ID " + id + " no encontrado"));
        return ResponseEntity.ok(jabon);
    }

    @GetMapping("/{id}/ingredientes")
    @Operation(summary = "Obtener ingredientes de un jabón por ID", description = "Devuelve una lista de ingredientes para un jabón específico")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes encontrada")
    @ApiResponse(responseCode = "404", description = "Ingredientes no encontrados o jabón no existe")
    public ResponseEntity<List<Ingrediente>> getIngredientesByJabonId(@PathVariable Long id) {
    	   logger.info("## getIngredientesByJabonId ({}) ##",id);
    	List<Ingrediente> ingredientes = jabonService.findIngredientesByJabonId(id);
        if (ingredientes.isEmpty()) {
            return ResponseEntity.notFound().build(); // O alguna otra respuesta si no hay ingredientes o el jabón no existe
        }
        return ResponseEntity.ok(ingredientes);
    }
    

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un jabón", description = "Actualiza los detalles de un jabón existente")
    @ApiResponse(responseCode = "200", description = "Jabón actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado para actualizar")
    public ResponseEntity<Jabon> actualizarJabon(@PathVariable Long id, @RequestBody Jabon jabon) {
    	  logger.info("## actualizarJabon id({}) ##",id);
    	if (!jabonService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jabon.setId(id); // Establece el ID del objeto a actualizar
        Jabon jabonActualizado = jabonService.save(jabon); // Utiliza el servicio para guardar el jabón actualizado
        return ResponseEntity.ok(jabonActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar un jabón", description = "Elimina un jabón existente por su ID")
    @ApiResponse(responseCode = "204", description = "Jabón eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Jabón no encontrado para eliminar")
    public ResponseEntity<Void> borrarJabon(@PathVariable Long id) {
    	  logger.info("## borrarJabon id:{} ##",id);
        if (!jabonService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jabonService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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

        // Aplica las actualizaciones parciales a los campos del jabón
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

        // Guarda el jabón actualizado
        Jabon jabonActualizado = jabonService.save(jabonActual);
        return ResponseEntity.ok(jabonActualizado);
    }
    
    @PostMapping
    @Operation(summary = "Crear un nuevo jabón", description = "Crea un nuevo jabón y lo guarda en la base de datos")
    @ApiResponse(responseCode = "201", description = "Jabón creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos proporcionados para el nuevo jabón son inválidos")
    public ResponseEntity<Jabon> crearJabon(@RequestBody Jabon nuevoJabon) {
    	
    	logger.info("## crearJabon ##");
        // Realiza la validación y lógica de negocio necesaria antes de guardar el nuevo jabón
        // Por ejemplo, puedes verificar si ya existe un jabón con el mismo nombre

        // Guarda el nuevo jabón en la base de datos
        Jabon jabonCreado = jabonService.save(nuevoJabon);

        // Devuelve una respuesta con el jabón creado y el código de estado 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(jabonCreado);
    }

}