package org.example.DTOs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GroupDTO extends BaseDTO {

    private String _name;
    private String _description;
    private List<UserDTO> _users;

    public GroupDTO() {
        _users= new ArrayList<>();
    }

    public GroupDTO(int id, String name, String description) {
        super(id);
        _name = name;
        _description = description;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public List<UserDTO> getUsers() {
        return _users;
    }

    public void setUsers(List<UserDTO> users) {
        _users = users;
    }

    public void addUser(UserDTO user) {
        if (_users == null) {
            _users = new LinkedList<UserDTO>();
        }
        _users.add(user);
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "_name='" + _name + '\'' +
                ", _description='" + _description + '\'' +
                ", _users=" + _users +
                '}';
    }

    public void deleteUser(UserDTO user) {
        if (_users != null) {
            _users.remove(user);
        }
    }
}