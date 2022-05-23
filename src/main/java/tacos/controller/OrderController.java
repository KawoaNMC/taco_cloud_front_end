package tacos.controller;

import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import tacos.model.Order;
import tacos.model.Taco;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {
	private RestTemplate rest = new RestTemplate();
	@GetMapping("/current")
	public String orderForm(Model model) {
		model.addAttribute("order", new Order());
		return "orderForm";
	}

	@PostMapping
	public String processOrder(@Valid Order order, Errors errors) {
		if (errors.hasErrors()) {
			return "orderForm";
		}
		log.info("Order submitted: " + order);
		order.setTacoIds(DesignTacoController.tacoIds);
		HttpEntity<Order> request = new HttpEntity<>(order);
		rest.postForEntity("http://localhost:8080/orders", request, Taco.class);
		DesignTacoController.tacoIds = new ArrayList<Long>();
		return "redirect:/";
	}

}
