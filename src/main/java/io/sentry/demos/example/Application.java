package io.sentry.demos.example;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.system.SystemProperties;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.sentry.Sentry;;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static Map<String, Integer> inventory = new HashMap<>();

	private void checkout(List<Item> cart) {
		Map<String, Integer> tempInventory = inventory;
		for(Item item : cart) {
			int currentInventory = tempInventory.get(item.getId());
			if(currentInventory <= 0) {
				String message = "No inventory for " + item.getId();
				throw new RuntimeException(message);
			}

			tempInventory.put(item.getId(), currentInventory--);
		}
		inventory = tempInventory;
	}

	// Class constructor to initialize logging 
	public void Application() {

	}

	@PostMapping("/checkout")
	public void CheckoutCart(@RequestHeader(name = "X-Session-ID", required = true) String sessionId,
							 @RequestHeader(name = "X-Transaction-ID", required = true) String transactionId,
							 @RequestBody Order order) {

		// perform checkout
		
		// Because MDC is thread dependent, it won't be added
		// to exceptions that aren't caught
		//
		// So we will wrap in try/catch
		Sentry.configureScope(scope -> {
			scope.setTag("sessionId", sessionId);
			scope.setTag("transactionId", transactionId);

			scope.setExtra("cart", order.toJson());
		});

		MDC.put("sessionId", sessionId);
		MDC.put("transactionId", transactionId);
		logger.info("Called checkout");
		try {
			checkout(order.getCart());
		} catch (Exception e) {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/capture-message")
	public String CaptureMessage() {
		Sentry.configureScope(scope -> {
			scope.setExtra("springVersion", SpringVersion.getVersion());
			scope.setExtra("jdkVersion", SystemProperties.get("java.version"));
		});
		MDC.put("springVersion", SpringVersion.getVersion());
		MDC.put("jdkVersion", SystemProperties.get("java.version"));
		logger.info("Called capture-message");
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
		inventory.put("wrench", 0);
		inventory.put("nails", 0);
		inventory.put("hammer", 1);

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
