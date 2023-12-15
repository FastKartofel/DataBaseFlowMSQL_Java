package org.example.repositories.implementation;

import org.example.DTOs.BaseDTO;
import org.example.DTOs.GroupDTO;
import org.example.DTOs.UserDTO;
import org.example.repositories.interfaces.IGroupRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRepositoryImpl implements  IGroupRepository {

    private Connection connection;

    public GroupRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<GroupDTO> findByName(String name) {
        String FIND_BY_NAME = "SELECT * FROM GROUPS WHERE GROUP_NAME = ?";
        try (PreparedStatement statement = this.getConnection().prepareStatement(FIND_BY_NAME)) {
            statement.setString(1, name);
            ResultSet groups = statement.executeQuery();
            List<GroupDTO> result = new ArrayList<>();
            while (groups.next()) {
                int id = groups.getInt(1);
                String groupName = groups.getString(2);
                String desc = groups.getString(3);
                GroupDTO group = new GroupDTO();
                group.setId(id);
                group.setDescription(desc);
                group.setName(groupName);
                result.add(group);
            }
            return result;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void add(GroupDTO groupDTO) {
        String SQL = "INSERT INTO GROUPS (GROUP_NAME, GROUP_DESCRIPTION) VALUES (?, ?)";
        String SQL_ASSIGN = "INSERT INTO USERS_GROUPS (user_id, group_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement assignToGroup = this.getConnection().prepareStatement(SQL_ASSIGN)) {
            this.beginTransaction();

            preparedStatement.setString(1, groupDTO.getName());
            preparedStatement.setString(2, groupDTO.getDescription());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating group failed, no rows affected.");
            }

            try (ResultSet insertedGroupId = preparedStatement.getGeneratedKeys()) {
                if (insertedGroupId.next()) {
                    int groupId = insertedGroupId.getInt(1);
                    List<UserDTO> users = groupDTO.getUsers();
                    for (UserDTO user : users) {
                        assignToGroup.setInt(1, user.getId());
                        assignToGroup.setInt(2, groupId);
                        assignToGroup.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creating group failed, no ID obtained.");
                }
            }

            this.commitTransaction();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.rollbackTransaction();
        }
    }


    @Override
    public void update(GroupDTO groupDTO) {
        String UPDATE = "UPDATE GROUPS SET GROUP_NAME=?, GROUP_DESCRIPTION=? WHERE GROUP_ID=?";
        String DELETE_ASSOCIATIONS = "DELETE FROM USERS_GROUPS WHERE GROUP_ID=?";
        String INSERT_ASSOCIATION = "INSERT INTO USERS_GROUPS(USER_ID, GROUP_ID) VALUES(?,?)"; // Ensure table name is correct

        this.beginTransaction();
        try (PreparedStatement statement = this.getConnection().prepareStatement(UPDATE);
             PreparedStatement deleteAssociations = this.getConnection().prepareStatement(DELETE_ASSOCIATIONS);
             PreparedStatement insert = this.getConnection().prepareStatement(INSERT_ASSOCIATION)) {

            statement.setString(1, groupDTO.getName());
            statement.setString(2, groupDTO.getDescription());
            statement.setInt(3, groupDTO.getId());

            // Execute update
            statement.executeUpdate();

            // Delete existing associations
            deleteAssociations.setInt(1, groupDTO.getId());
            deleteAssociations.executeUpdate();

            // Insert new associations
            for (UserDTO user : groupDTO.getUsers()) {
                insert.setInt(1, user.getId()); // Assuming UserDTO extends BaseDTO and has getId()
                insert.setInt(2, groupDTO.getId());
                insert.executeUpdate();
            }

            this.commitTransaction();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            this.rollbackTransaction();
        }
    }


    @Override
    public void addOrUpdate(GroupDTO dto) {
        String SQL_COUNT = "SELECT count(*) FROM GROUPS WHERE GROUP_ID=?";
        try (PreparedStatement statement = this.getConnection().prepareStatement(SQL_COUNT);) {
            statement.setInt(1, dto.getId());
            ResultSet resultCount = statement.executeQuery();
            boolean resultPresent = resultCount.next();
            if (resultPresent) {
                int count = resultCount.getInt(1);
                if (count > 0) {
                    add(dto);
                } else {
                    update(dto);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(GroupDTO groupDTO) {
        String DELETE_GROUP = "DELETE FROM GROUPS WHERE GROUP_ID=?";
        String DELETE_ASSOCIATIONS = "DELETE FROM USERS_GROUPS WHERE GROUP_ID=?";
        beginTransaction();
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(DELETE_GROUP);
             PreparedStatement deleteAssociations = this.getConnection().prepareStatement(DELETE_ASSOCIATIONS)) {

            deleteAssociations.setInt(1, groupDTO.getId());
            deleteAssociations.execute();
            preparedStatement.setInt(1, groupDTO.getId());
            preparedStatement.executeUpdate();
            int deleteResult = preparedStatement.executeUpdate();
            System.out.println("DELETE groups AFFECTED ROWS: " + deleteResult);
            commitTransaction();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            rollbackTransaction();
        }
    }

    @Override
    public GroupDTO findById(int groupID) {
        String SELECT = "SELECT * FROM GROUPS WHERE GROUP_ID=?";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(SELECT);) {
            preparedStatement.setInt(1, groupID);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean present = resultSet.next();
            if (present) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String desc = resultSet.getString(3);
                GroupDTO group = new GroupDTO();
                group.setId(id);
                group.setName(name);
                group.setDescription(desc);
                return group;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void beginTransaction() {
        try {
            this.getConnection().setAutoCommit(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void commitTransaction() {
        try {
            this.getConnection().commit();
            this.getConnection().setAutoCommit(true);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            this.getConnection().rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        String COUNT = "SELECT count(*) FROM GROUPS";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(COUNT)) {

            ResultSet set = preparedStatement.executeQuery();
            boolean resultPresent = set.next();
            if (resultPresent) {
                return set.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean exists(int groupId) {
        String COUNT = "SELECT count(*) FROM GROUPS WHERE GROUP_ID = ?";
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(COUNT)) {
            preparedStatement.setInt(1, groupId);
            ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
                System.out.println("Group with the provided id number: "+ groupId + " exists!");
                return set.getInt(1) > 0;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}