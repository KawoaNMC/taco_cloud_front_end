package tacos.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import tacos.model.Ingredient;
import tacos.model.Taco;
import tacos.model.Ingredient.Type;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {
	private RestTemplate rest = new RestTemplate();
	public static List<Long> tacoIds = new ArrayList<>();
	
	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		List<Ingredient> ingredients =
		Arrays.asList(rest.getForObject("http://localhost:8080/ingredients",Ingredient[].class));
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(),filterByType(ingredients, type));
		}
	}

	@GetMapping
	public String showDesignForm(Model model) {
		model.addAttribute("taco", new Taco());
		return "design";
	}

	@PostMapping
	public String processDesign(@Valid Taco taco, Errors errors) {
		if (errors.hasErrors()) {
			return "design";
		}
	
		log.info("Processing design: " + taco);
		HttpEntity<Taco> request = new HttpEntity<>(taco);
		ResponseEntity<Long> res = rest.postForEntity("http://localhost:8080/design", request, Long.class);
		tacoIds.add(res.getBody());
		return "redirect:/orders/current";
	}

	private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		List<Ingredient> ingrList = new ArrayList<Ingredient>();
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getType().equals(type))

				ingrList.add(ingredient);
		}
		return ingrList;
	}
}
