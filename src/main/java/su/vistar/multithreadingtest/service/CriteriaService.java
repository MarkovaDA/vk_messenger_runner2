package su.vistar.multithreadingtest.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import su.vistar.multithreadingtest.dto.CriteriaDTO;
import su.vistar.multithreadingtest.dto.UserDTO;

public class CriteriaService {
    
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
    public void updateCriteriaOffset(int number, int offset, Connection connection){
        String sql = "update vk_messenger.criteria "
                + "set offset = ? where id = ?;";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, offset);
            preparedStatement.setInt(2, number);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(CriteriaService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public CriteriaDTO getCriteria(int id, Connection connection){
        String sql = "select * from vk_messenger.criteria where id=?;";
        PreparedStatement preparedStatement;
        CriteriaDTO criteria = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                criteria = new CriteriaDTO();
                criteria.setId(id);
                criteria.setCondition(rs.getString("condition"));
                criteria.setId(rs.getInt("offset"));
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(CriteriaService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return criteria;
    }
    public void insert(int criteriaId, List<UserDTO> users, Connection connection){
        String sql = "insert into vk_messenger.people (uid, criteria_id, first_name, last_name) "
                + "values (?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        Iterator<UserDTO> iterator = users.iterator();
        try {
            preparedStatement = connection.prepareStatement(sql);
            UserDTO currentUser;
            while(iterator.hasNext()){
                currentUser = iterator.next();
                preparedStatement.setString(1, currentUser.getUid());
                preparedStatement.setInt(2, criteriaId);
                preparedStatement.setString(3, currentUser.getFirst_name());
                preparedStatement.setString(4, currentUser.getLast_name());
                preparedStatement.execute();
            }
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(CriteriaService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
