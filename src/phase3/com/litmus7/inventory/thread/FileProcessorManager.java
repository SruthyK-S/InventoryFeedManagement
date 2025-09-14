package phase3.com.litmus7.inventory.thread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileProcessorManager extends Thread{
	private static final Logger logger = LogManager.getLogger(FileProcessorManager.class);
	String input;
	String processed;
	String error;
	
    
	public FileProcessorManager(String input, String processed, String error)
	{
		this.input = input;
		this.processed = processed;
		this.error = error;
	}
    
	@Override
    public void run() {
        try 
        {
            Files.createDirectories(Paths.get(input));

            File inputFolder = new File(input);
            File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".csv"));

            if (files == null || files.length == 0) {
                logger.info("No files found in input folder.");
                return;
            }

            for (File file : files) {
                Thread worker = new Thread(new FileTask(file, processed, error), "Worker-" + file.getName());
                worker.start();
                logger.info("Started thread {} for file {}", worker.getName(), file.getName());
            }

        } catch (Exception e) {
            logger.error("Error in FileProcessorManager: {}", e.getMessage());
        }
    }
    
    
}
