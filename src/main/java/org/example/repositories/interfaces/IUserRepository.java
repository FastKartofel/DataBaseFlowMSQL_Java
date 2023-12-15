package org.example.repositories.interfaces;

import org.example.DTOs.UserDTO;
import org.example.repositories.interfaces.IRepository;

import java.util.List;

public interface IUserRepository extends IRepository<UserDTO> {

    List<UserDTO> findByName(String username);
}