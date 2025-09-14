package phase3.com.litmus7.inventory.thread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileProcessorManager extends Thread{
	private static final Logger logger = LogManager.getLogger(FileProcessorManager.class);
	String input_dir;
	String processed_dir;
	String error_dir;
	
    
	public FileProcessorManager(String input, String processed, String error)
	{
		this.input_dir = input;
		this.processed_dir = processed;
		this.error_dir = error;
	}
    
	public void startProcessing() {
        try {
            Files.createDirectories(Paths.get(input_dir));

            File inputFolder = new File(input_dir);
            File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".csv"));

            if (files == null || files.length == 0) {
                logger.info("No files found in input folder.");
                return;
            }

            ExecutorService executor = Executors.newFixedThreadPool(5);

            for (File file : files) {
                executor.submit(new FileTask(file, processed_dir, error_dir));
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                logger.warn("Timeout reached before all files were processed.");
            }

        } catch (Exception e) {
            logger.error("Error in FileProcessorManager: " + e.getMessage(), e);
        }
    }
    
    
}
