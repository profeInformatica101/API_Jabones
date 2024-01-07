package com.dwes.api.configuracion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dwes.api.entidades.Ingrediente;
import com.dwes.api.entidades.Jabon;
import com.dwes.api.entidades.Producto;
import com.dwes.api.entidades.enumerados.TipoDePiel;
import com.dwes.api.repositorios.JabonRepository;
import com.dwes.api.repositorios.ProductoRepository;
import com.github.javafaker.Faker;

@Component
public class InicializarDatos implements CommandLineRunner {


    @Autowired
    private JabonRepository jabonRepository;
    
    Faker faker = new Faker();
    
    
	@Override
	public void run(String... args) throws Exception {
		


        for (int i = 0; i < 100; i++) {
            Jabon jabon = new Jabon();
            jabon.setNombre(faker.commerce().productName());
            jabon.setPrecio(Double.parseDouble(faker.commerce().price().replaceAll("[^\\d.]+", "")));
            jabon.setDescripcion(faker.lorem().sentence());
            jabon.setStock(faker.number().numberBetween(0, 100));
            jabon.setImagenUrl(generarUrlImagenAleatoria());
            jabon.setAroma(faker.lorem().word());
            jabon.setTipoDePiel(TipoDePiel.values()[faker.random().nextInt(TipoDePiel.values().length)]); // Asume que tienes una enumeración TipoDePiel
            
            List<Ingrediente> ingredientes = new ArrayList<>();
            for (int j = 0; j < faker.number().numberBetween(1, 5); j++) {
                // Asume que tienes un método para generar un Ingrediente ficticio
                ingredientes.add(generarIngredienteFicticio(faker));
            }
            jabon.setIngredientes(ingredientes);

            jabonRepository.save(jabon);
        }
	}
	
	private Ingrediente generarIngredienteFicticio(Faker faker) {
	    Ingrediente ingrediente = new Ingrediente();
	    String[] elementos = {"jabón de glicerina", "gel aloe vera", "miel", "aceite de oliva", "ralladura de limón", "aceite esencial"};
	    String[] cantidades = {"2 pastillas", "1 taza", "4 cucharadas", "5 cucharadas", "1 cucharada"};

	    // Elegir aleatoriamente un elemento y una cantidad
	    String elemento = elementos[faker.random().nextInt(elementos.length)];
	    String cantidad = cantidades[faker.random().nextInt(cantidades.length)];

	    // Si el elemento es "gel aloe vera" o "aceite de oliva", añadir la medida
	    if (elemento.equals("gel aloe vera")) {
	        cantidad += " (200 g)";
	    } else if (elemento.equals("aceite de oliva")) {
	        cantidad += " (100 ml)";
	    }

	    ingrediente.setElemento(elemento);
	    ingrediente.setCantidad(cantidad);

	    return ingrediente;
	}
	
	 private String generarUrlImagenAleatoria() {
	        return "https://e00-telva.uecdn.es/assets/multimedia/imagenes/2019/11/08/15732087888279.jpg";
	    }
}
