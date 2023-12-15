package org.example.repositories.implementation;

import org.example.DTOs.BaseDTO;
import org.example.DTOs.GroupDTO;
import org.example.DTOs.UserDTO;
import org.example.DataBaseConnection;
import org.example.repositories.interfaces.IUserRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static org.example.DataBaseConnection.URL;

public class UserRepositoryImpl implements IUserRepository {

    private Connection connection;

    @Override
    public List<UserDTO> findByName(String username) {
        return null;
    }

    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void add(UserDTO dto) {
        String SQL = "INSERT INTO USERS(user_login, user_password) VALUES (?,?)";
        String SQL_ASSIGN_USER_TO_GROUP = "INSERT INTO USERS_GROUPS(user_id, group_id) VALUES(?,?)";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement assignToGroup = this.getConnection().prepareStatement(SQL_ASSIGN_USER_TO_GROUP)) {
            this.beginTransaction();
            preparedStatement.setString(1, dto.getLogin());
            preparedStatement.setString(2, dto.getPassword());
            boolean inserted = preparedStatement.execute();

            try (ResultSet insertedUserId = preparedStatement.getGeneratedKeys()) {
                boolean idPresent = insertedUserId.next();
                if (idPresent) {
                    int userId = insertedUserId.getInt(1);
                    List<GroupDTO> groups = dto.getGroups();
                    for (GroupDTO group : groups) {
                        System.out.println("group id: " + group.getId() + " user id: " + userId);
                        int groupId = group.getId();
                        assignToGroup.setInt(1, userId);
                        assignToGroup.setInt(2, groupId);
                        boolean assignResult = assignToGroup.execute();
                    }
                    System.out.println("Insert status: " + inserted);
                    this.commitTransaction();
                }
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.rollbackTransaction();
        }
    }

    @Override
    public void update(UserDTO dto) {
        String SQL_ASSIGN_USER_TO_GROUP = "INSERT INTO USERS_GROUPS(user_id, group_id) VALUES(?,?)";
        String SQL = "UPDATE USERS SET user_login=?, user_password=? WHERE user_id=?";
        String SQL_DELETE_GROUPS = "DELETE FROM USERS_GROUPS WHERE user_id = ?";

        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SQL);
             PreparedStatement deleteGroups = this.getConnection().prepareStatement(SQL_DELETE_GROUPS);
             PreparedStatement assignUserToGRoup = this.getConnection().prepareStatement(SQL_ASSIGN_USER_TO_GROUP)) {
            this.beginTransaction();
            preparedStatement.setString(1, dto.getLogin());
            preparedStatement.setString(2, dto.getPassword());
            preparedStatement.setInt(3, dto.getId());
            int updateResult = preparedStatement.executeUpdate();
            System.out.println("UPDATE user with id: " + dto.getId() + " AFFECTED ROWS: " + updateResult);

            List<GroupDTO> groups = dto.getGroups();
            deleteGroups.setInt(1, dto.getId());
            int deleteGroupsResult = deleteGroups.executeUpdate();

            List<Integer> allUserGroupsIds = new ArrayList<>();
            for (GroupDTO group : groups) {
                allUserGroupsIds.add(group.getId());
            }


            for (int idPresentInDTO : allUserGroupsIds) {
                assignUserToGRoup.setInt(1, dto.getId());
                assignUserToGRoup.setInt(2, idPresentInDTO);
                assignUserToGRoup.execute();
            }
            this.commitTransaction();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.rollbackTransaction();
        }
    }

    @Override
    public void addOrUpdate(UserDTO dto) {

    }


   /* @Override
    public void addOrUpdate(UserDTO dto) { //i changed from UserDTO to int number
        boolean userExists = exists(dto);

        if (userExists) {
            this.update(dto);
        } else {
            this.add(dto);
        }
    }*/

    @Override
    public void delete(UserDTO dto) {
        String SQL = "DELETE FROM USERS WHERE user_id=?";
        String SQL_DELETE_ASSIGMENTS_TO_GROUPS = "DELETE FROM USERS_GROUPS WHERE user_id=?";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SQL);
             PreparedStatement deleteUserGroups = this.getConnection().prepareStatement(SQL_DELETE_ASSIGMENTS_TO_GROUPS)) {
            this.beginTransaction();
            deleteUserGroups.setInt(1, dto.getId());
            deleteUserGroups.executeUpdate();
            preparedStatement.setInt(1, dto.getId());
            int deleteResult = preparedStatement.executeUpdate();
            System.out.println("DELETE users AFFECTED ROWS: " + deleteResult);
            this.commitTransaction();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.rollbackTransaction();
        }
    }

    /*
    GROUP_ID INTEGER NOT NULL AUTO_INCREMENT
    , GROUP_NAME VARCHAR(20) NOT NULL
    , GROUP_DESCRIPTION TEXT NOT NULL
    */
    @Override
    public UserDTO findById(int id) {
        beginTransaction();
        String JOIN_GROUPS = "SELECT GROUPS.GROUP_ID, GROUPS.GROUP_NAME, GROUP_DESCRIPTION from USERS_GROUPS INNER JOIN GROUPS ON GROUPS.GROUP_ID = USERS_GROUPS.GROUP_ID WHERE USERS_GROUPS.USER_ID=?";
        String SELECT = "SELECT * FROM USERS WHERE user_id=?";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SELECT);
             PreparedStatement joinGroupsStatement = this.getConnection().prepareStatement(JOIN_GROUPS)) {
            this.beginTransaction();
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            boolean present = result.next();
            if (present) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(result.getInt(1));
                userDTO.setLogin(result.getString(2));
                userDTO.setPassword(result.getString(3));
                joinGroupsStatement.setInt(1, id);
                ResultSet groups = joinGroupsStatement.executeQuery();

                while (groups.next()) {
                    String groupName = groups.getString(2);
                    String groupDescription = groups.getString(3);
                    int groupId = groups.getInt(1);

                    GroupDTO groupDTO = new GroupDTO();
                    groupDTO.setName(groupName);
                    groupDTO.setDescription(groupDescription);
                    groupDTO.setId(groupId);
                    userDTO.addGroup(groupDTO);
                }
                commitTransaction();
                return userDTO;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            rollbackTransaction();
        }
        return null;
    }

    @Override
    public void beginTransaction() {
        try {
            this.getConnection().setAutoCommit(false);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void commitTransaction() {
        try {
            this.getConnection().commit();
            // dobra praktyka aby po zakonczeniu transkacji ustawic z powrotem auto commit na true zeby przykladowo nietransakcyjne metody byly zatwierdzane automatycznie tak jak poprzednio
            this.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            this.getConnection().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        String COUNT = "SELECT count(*) FROM USERS";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(COUNT)) {
            this.beginTransaction();
            ResultSet result = preparedStatement.executeQuery();
            boolean present = result.next();
            if (present) {
                int _result = result.getInt(1);
                this.commitTransaction();
                return _result;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.rollbackTransaction();
        }
        return 0;
    }
    //
    /// user 1 - > user 2
    /// operacja user 1 amount - 100
    // SQLException rollabck
    // operacja user 2 -> amount + 100
    ///

    @Override
    public boolean exists(int userId) {
        String SQL_CHECK_IF_EXISTS = "SELECT count(*) FROM USERS WHERE user_id = ?";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SQL_CHECK_IF_EXISTS)) {
            preparedStatement.setInt(1, userId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int count = result.getInt(1);
                if (count > 0) {
                    System.out.println("User with the provided id number: " + userId + " exists!");
                    return true;
                } else {
                    System.out.println("User with the provided id number: " + userId + " does not exist.");
                    return false;
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


