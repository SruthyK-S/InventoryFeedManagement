package phase2.com.litmus7.inventory.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import phase1.com.litmus7.inventory.util.DBConnectionUtil;

public class ProductDAO {
	private static final Logger logger = LogManager.getLogger(ProductDAO.class); 
	
	public boolean insertDataToDB(File file)
	{
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
                logger.info("File committed: " + file.getName());
                return true;

            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException sqle) {
                    logger.error("Rollback failed for " + file.getName() + ": " + sqle.getMessage());
                }
                logger.error("Error in file " + file.getName() + " → rollback. Reason: " + e.getMessage());
                return false;
            } 

        } catch (SQLException sqle) {
        	logger.error("Data base connection error " + file.getName() + ": " + sqle.getMessage());
			sqle.printStackTrace();
			return false;
		}
	}
}
