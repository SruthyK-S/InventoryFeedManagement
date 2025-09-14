package phase2.com.litmus7.inventory.thread;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase2.com.litmus7.inventory.dao.ProductDAO;
import phase2.com.litmus7.inventory.util.FileUtil;

public class FileTask implements Runnable {
    private static final Logger logger = LogManager.getLogger("FileTask");


    private File file;
    private String processed;
    private String error;

    public FileTask(File file, String processed, String error) {
        this.file = file;
        this.processed = processed;
        this.error = error;
    }

    @Override
    public void run() {
        logger.info("Processing file in thread {} → {}", Thread.currentThread().getName(), file.getName());

        boolean success = false;
        ProductDAO productDao = new ProductDAO();

        try
        {
        	success = productDao.insertDataToDB(file);
        }
         catch (Exception ex) {
        	 success = false;
            logger.error("Error while processing file " + file.getName() + ": " + ex.getMessage());
        }

        if (success) {
            FileUtil.moveFile(file, processed);
        } else {
            FileUtil.moveFile(file, error);
        }
    }


}