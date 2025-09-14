package phase3.com.litmus7.inventory.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase2.com.litmus7.inventory.controller.ProductController;
import phase2.com.litmus7.inventory.dto.Response;
import phase2.com.litmus7.inventory.thread.FileProcessorManager;

public class ProductController {
	private static final Logger logger = LogManager.getLogger(ProductController.class);
	
	private static final String INPUT_DIR = "input";
    private static final String PROCESSED_DIR = "processed";
    private static final String ERROR_DIR = "error";
    
    private FileProcessorManager fileProcessor = new FileProcessorManager(INPUT_DIR, PROCESSED_DIR, ERROR_DIR);
     
    public Response<String> addToInventory()
    {
    	try 
    	{
    		logger.info("Checking if file paths are valid");
			Files.createDirectories(Paths.get(PROCESSED_DIR));
			Files.createDirectories(Paths.get(ERROR_DIR));
			logger.info("File processing started..");
			fileProcessor.start();
			return new Response<>(true, "File processing successfull");
		} catch (IOException e) {
			return new Response<String>(false, e.getMessage(), "Invalid file paths");
		} catch (Exception e) {
			return new Response<String>(false, e.getMessage(), "Failed to process files");
		}
        
    }

}