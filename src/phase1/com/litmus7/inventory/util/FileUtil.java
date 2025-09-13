package phase1.com.litmus7.inventory.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {
	String processed;
    String error;
	public FileUtil(String processed, String error)
	{
		this.processed = processed;
		this.error = error;
	}
	private static final Logger logger = LogManager.getLogger(FileUtil.class);

	public void processFile(File file) {
        logger.info("Processing file: " + file.getName());

        boolean success = false; 

        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (
                BufferedReader br = new BufferedReader(new FileReader(file));
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO products (sku, product_name, quantity, price) VALUES (?, ?, ?, ?)")
            ) {
                String line = br.readLine(); 

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
            moveFile(file, processed);
        } else {
            moveFile(file, error);
        }
    }
	
	public void moveFile(File file, String targetDir) {
        try {
            Files.move(file.toPath(),
                    Paths.get(targetDir, file.getName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to move file " + file.getName() + ": " + e.getMessage());
        }
    }



}
