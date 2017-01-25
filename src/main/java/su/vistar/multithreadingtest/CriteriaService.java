package su.vistar.multithreadingtest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CriteriaService {
    //извлечь неучтенные критерии
    public List<Integer> notViewedCriteria(Connection connection) {
        List<Integer> nextCriteriaIds = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String sql = "select id as criteriaId from vk_messenger.criteria as B where B.considered=0;";
            ResultSet rs = statement.executeQuery(sql);
            Object resultObject;
            while(rs.next()) {
                resultObject = rs.getObject("criteriaId");
                if (resultObject == null)
                    break;
                nextCriteriaIds.add(rs.getInt("criteriaId"));
            }
            rs.close();
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(CriteriaService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return nextCriteriaIds;
    }
    //обновить статус неучтенных критериев
    public void updateCriteriaStatus(int number, Connection connection)  {
        String sql = "update vk_messenger.criteria as A "
                + "set A.considered=1 where A.id = ?;";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, number);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(CriteriaService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
