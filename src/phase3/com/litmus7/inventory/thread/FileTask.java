package phase3.com.litmus7.inventory.thread;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase2.com.litmus7.inventory.dao.ProductDAO;
import phase2.com.litmus7.inventory.util.FileUtil;

public class FileTask implements Runnable {
    private static final Logger logger = LogManager.getLogger("FileTask");


    private File file;
    private String processed_dir;
    private String error_dir;

    public FileTask(File file, String processed, String error) {
        this.file = file;
        this.processed_dir = processed;
        this.error_dir = error;
    }

    @Override
    public void run() {
        logger.info("Processing file: " + file.getName());
        boolean success = false;
        ProductDAO productDao = new ProductDAO();
        success = productDao.insertDataToDB(file);

        if (success) {
            FileUtil.moveFile(file, processed_dir);
        } else {
            FileUtil.moveFile(file, error_dir);
        }
    }


}