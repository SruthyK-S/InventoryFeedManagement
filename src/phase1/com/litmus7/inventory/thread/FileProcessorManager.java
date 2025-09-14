package phase1.com.litmus7.inventory.thread;



import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase1.com.litmus7.inventory.util.FileUtil;

public class FileProcessorManager extends Thread{
	private static final Logger logger = LogManager.getLogger("FileProcessorThread");
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
        try {
            
	            File inputFolder = new File(input);
	            File[] files = inputFolder.listFiles((dir, name) -> name.endsWith(".csv"));
	
	            if (files == null || files.length == 0) {
	                logger.info("No files found in input folder.");
	                return;
	            }
	
	            for (File file : files) {
	                boolean success = FileUtil.processFile(file);
	                if (success) {
	                    FileUtil.moveFile(file, processed);
	                } else {
	                    FileUtil.moveFile(file, error);
	                }
            }

        } catch (Exception e) {
            logger.error("Error in thread: " + e.getMessage());
        }
    }
    
    
}
