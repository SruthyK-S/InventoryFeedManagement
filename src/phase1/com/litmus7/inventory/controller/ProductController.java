package phase1.com.litmus7.inventory.controller;



import java.io.*;
import java.nio.file.*;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase1.com.litmus7.inventory.thread.FileProcessorManager;
import phase1.com.litmus7.inventory.util.DBConnectionUtil;

public class ProductController {
	private static final Logger logger = LogManager.getLogger(ProductController.class);
	
	private static final String INPUT_DIR = "input";
    private static final String PROCESSED_DIR = "processed";
    private static final String ERROR_DIR = "error";
    
    private FileProcessorManager fileProcessor = new FileProcessorManager(INPUT_DIR);
    
    public int addToInventory()
    {
    	try {
			Files.createDirectories(Paths.get(PROCESSED_DIR));
			Files.createDirectories(Paths.get(ERROR_DIR));
			return 1;
		} catch (IOException e) {
			return 0;
		}
        
    }

    


    private void moveFile(File file, String targetDir) {
        try {
            Files.move(file.toPath(),
                    Paths.get(targetDir, file.getName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to move file " + file.getName() + ": " + e.getMessage());
        }
    }
}