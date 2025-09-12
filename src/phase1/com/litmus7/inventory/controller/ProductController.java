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

    private void processFile(File file) {
        logger.info("Processing file: " + file.getName());

        boolean success = false; 

        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (
                BufferedReader br = new BufferedReader(new FileReader(file));
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO products (sku, product_name, quantity, price) VALUES (?, ?, ?, ?)")
            ) {
                String line = br.readLine(); //header

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 4) {
                        throw new SQLException("Invalid record format: " + line);
                    }

                    ps.setInt(1, Integer.parseInt(parts[0].trim()));
                    ps.setString(2, parts[1].trim());
                    ps.setInt(3, Integer.parseInt(parts[2].trim()));
                    ps.setDouble(4, Double.parseDouble(parts[3].trim()));
                    ps.executeUpdate();
                }

                conn.commit();
                success = true;
                logger.info("File committed: " + file.getName());

            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException sqle) {
                    logger.error("Rollback failed for " + file.getName() + ": " + sqle.getMessage());
                }
                logger.error("Error in file " + file.getName() + " → rollback. Reason: " + e.getMessage());
            }

        } catch (Exception ex) {
            logger.error("DB connection error for file " + file.getName() + ": " + ex.getMessage());
        }


        if (success) {
            moveFile(file, PROCESSED_DIR);
        } else {
            moveFile(file, ERROR_DIR);
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