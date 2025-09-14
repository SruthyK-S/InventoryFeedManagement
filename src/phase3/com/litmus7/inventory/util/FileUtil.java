package phase3.com.litmus7.inventory.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class FileUtil {
	String processed;
    String error;

	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	
	public static void moveFile(File file, String targetDir) {
        try {
            Files.move(file.toPath(),
                    Paths.get(targetDir, file.getName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to move file " + file.getName() + ": " + e.getMessage());
        }
    }



}
