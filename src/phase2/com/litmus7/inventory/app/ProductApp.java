package phase2.com.litmus7.inventory.app;

import phase2.com.litmus7.inventory.controller.ProductController;
import phase2.com.litmus7.inventory.dto.Response;

public class ProductApp {
	public static void main(String[] args) {
     
		ProductController productController = new ProductController();
		Response<?> response = productController.addToInventory();
		if(response.isSuccess())
			System.out.println(response.getMessage());
		else
			System.out.println(response.getMessage() + "\n" + response.getData());
    }
}
