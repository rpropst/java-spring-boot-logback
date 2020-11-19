package io.sentry.demos.example;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;;

@SpringBootApplication
@RestController
public class Application {

	private static Map<String, Integer> inventory = new HashMap<>();

	private void checkout(List<Item> cart) {
		Map<String, Integer> tempInventory = inventory;
		for(Item item : cart) {
			int currentInventory = tempInventory.get(item.getId());
			if(currentInventory <= 0) {
				throw new RuntimeException("No inventory for %s" + item.getId());
			}

			tempInventory.put(item.getId(), currentInventory--);
		}
		inventory = tempInventory;
	}

	@GetMapping("/checkout")
	public void CheckoutCart(@RequestHeader(name = "X-Session-ID", required = true) String sessionId,
							 @RequestHeader(name = "X-Transaction-ID", required = true) String transactionId,
							 @RequestParam(value="order") Order order) {

		// perform checkout
		checkout(order.getCart());
	}

	@GetMapping("/capture-message")
	public String CaptureMessage() {
		return "Success";
	}

	@GetMapping("/handled")
	public String HandledError() {
		String someLocalVariable = "stack locals";

		try {
			int example = 1/0;
		} catch (Exception e) {
			return "Fail";
		}
		return "Success";
	}

	@GetMapping("/filtered")
	public String HandledFilteredError() {
		try {
			int example = 1/0;
		} catch (Exception e) {
			return "Success";
		}
		return "Success";
	}

	@GetMapping("/unhandled")
	public String UnhandledError() {
		String someLocalVariable = "stack locals";

		throw new RuntimeException("Unhandled exception!");
	} 

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/hello")
	public String Hello(@RequestParam(value="name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/")
	public String rootCall() {
		return "This is the root url";
	}
}
