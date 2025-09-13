package phase1.com.litmus7.inventory.thread;



import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase1.com.litmus7.inventory.util.FileUtil;

public class FileProcessorManager extends Thread{
	private static final Logger logger = LogManager.getLogger("FileProcessorThread");
	private FileUtil fileUtil;
	String input;
	String processed;
	String error;
	
    
	public FileProcessorManager(String input, String processed, String error)
	{
		this.input = input;
		this.processed = processed;
		this.error = error;
		this.fileUtil = new FileUtil(processed, error);
	}
    
    @Override
    public void run() {
        try {
            
	            File inputFolder = new File(input);
	            File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".csv"));
	
	            if (files == null || files.length == 0) {
	                logger.info("No files found in input folder.");
	                return;
	            }
	
	            for (File file : files) {
	                fileUtil.processFile(file);
            }

        } catch (Exception e) {
            logger.error("Error in thread: " + e.getMessage());
        }
    }
    
    
}
