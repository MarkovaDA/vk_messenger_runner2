package su.vistar.multithreadingtest;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCFactory {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://vps1.vistar.su:3306/vk_messenger";
    static final String USER = "dasha";
    static final String PASSWORD = "dasha";
    static Connection connection;
    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println("JDBC DRIVER не создан");
            Logger.getLogger(JDBCFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //синглтон конекшена
    public static Connection getConnection() throws PropertyVetoException, SQLException {
        if (connection == null) {
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setDriverClass(JDBC_DRIVER);
            cpds.setJdbcUrl(DB_URL);
            cpds.setUser(USER);
            cpds.setPassword(PASSWORD);
            return cpds.getConnection();
        }
        return connection;
    }
}
