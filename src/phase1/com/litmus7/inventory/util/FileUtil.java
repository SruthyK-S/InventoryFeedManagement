package phase1.com.litmus7.inventory.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase1.com.litmus7.inventory.dao.ProductDAO;

public class FileUtil {
	String processed;
    String error;
	public FileUtil(String processed, String error)
	{
		this.processed = processed;
		this.error = error;
	}
	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	private ProductDAO productDao = new ProductDAO();

	public void processFile(File file) {
        logger.info("Processing file: " + file.getName());

        boolean success; 
        try
        {
        	success = productDao.insertDataToDB(file);
        }
         catch (Exception ex) {
        	 success = false;
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
