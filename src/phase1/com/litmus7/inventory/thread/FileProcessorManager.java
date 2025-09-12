package phase1.com.litmus7.inventory.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase1.com.litmus7.inventory.util.DBConnectionUtil;

public class FileProcessorManager extends Thread{
	private static final Logger logger = LogManager.getLogger("FileProcessorThread");
	private String INPUT_DIR = "";
    
	public FileProcessorManager(String input)
	{
		this.INPUT_DIR = input;
	}
    
    @Override
    public void run() {
        try {
            
	            File inputFolder = new File(INPUT_DIR);
	            File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".csv"));
	
	            if (files == null || files.length == 0) {
	                logger.info("No files found in input folder.");
	                return;
	            }
	
	            for (File file : files) {
	                processFile(file);
            }

        } catch (Exception e) {
            logger.error("Error in thread: " + e.getMessage());
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


//        if (success) {
//            moveFile(file, PROCESSED_DIR);
//        } else {
//            moveFile(file, ERROR_DIR);
//        }
    }

}
