package org.example.repositories.interfaces;

import org.example.DTOs.GroupDTO;

import java.util.List;

public interface IGroupRepository extends IRepository<GroupDTO> {

    List<GroupDTO> findByName(String name);

    void add(GroupDTO groupDTO);

    void update(GroupDTO groupDTO);

    void delete(GroupDTO groupDTO);

    GroupDTO findById(int groupID);
}
