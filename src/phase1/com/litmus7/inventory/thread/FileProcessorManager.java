package phase1.com.litmus7.inventory.thread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileProcessorManager extends Thread{
	private static final Logger logger = LogManager.getLogger("FileProcessorThread");
    
    
    @Override
    public void run() {
        try {
            Files.createDirectories(Paths.get(PROCESSED_DIR));
            Files.createDirectories(Paths.get(ERROR_DIR));


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

}
