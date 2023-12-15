package org.example;

import org.example.DTOs.GroupDTO;
import org.example.DTOs.UserDTO;
import org.example.repositories.implementation.GroupRepositoryImpl;
import org.example.repositories.implementation.UserRepositoryImpl;


import java.sql.Connection;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        Connection connection = DataBaseConnection.getConnection();

        UserRepositoryImpl userRepository = new UserRepositoryImpl(connection);

        GroupRepositoryImpl groupRepository = new GroupRepositoryImpl(connection);

        /* FIRST PART
        //adding users to the repository
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setLogin("Grzegorz");
        userDTO1.setPassword("staryZabijaka#");
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setLogin("Stefan");
        userDTO2.setPassword("staruszka21");
        UserDTO userDTO3 = new UserDTO();
        userDTO3.setLogin("Adam");
        userDTO3.setPassword("password3");
        UserDTO userDTO4 = new UserDTO();
        userDTO4.setLogin("Oliwia");
        userDTO4.setPassword("kwiatek32");
        UserDTO userDTO5 = new UserDTO();
        userDTO5.setLogin("Anastazja");
        userDTO5.setPassword("żyrafabeznogi");
        UserDTO userDTO6 = new UserDTO();
        userDTO6.setLogin("Bartosz");
        userDTO6.setPassword("passss");
        UserDTO userDTO7 = new UserDTO();
        userDTO7.setLogin("Alicja");
        userDTO7.setPassword("Gana");

        userRepository.add(userDTO1);
        System.out.println("Added user: " + userDTO1.getLogin());

        userRepository.add(userDTO2);
        System.out.println("Added user: " + userDTO2.getLogin());

        userRepository.add(userDTO3);
        System.out.println("Added user: " + userDTO3.getLogin());

        userRepository.add(userDTO4);
        userRepository.add(userDTO5);
        userRepository.add(userDTO6);
        userRepository.add(userDTO7);
        */

        /* SECOND PART
        //adding groups to repository
        GroupDTO groupA = new GroupDTO();
        groupA.setName("GroupA");
        groupA.setDescription("MANAGER");
        GroupDTO groupB = new GroupDTO();
        groupB.setName("GroupB");
        groupB.setDescription("EMPLOYEE");
        GroupDTO groupC = new GroupDTO();
        groupC.setName("SOMETHING");
        groupC.setDescription("DISTRIBUTER");
        GroupDTO groupD = new GroupDTO();
        groupD.setName("STUDENT");
        groupD.setDescription("SOME GROUP");
        GroupDTO groupE = new GroupDTO();
        groupE.setName("GroupE");
        groupE.setDescription("STAFF");

        groupRepository.add(groupA);
        groupRepository.add(groupB);
        groupRepository.add(groupC);
        groupRepository.add(groupD);
        groupRepository.add(groupE);
        */


        /* THIRD PART
        //ASSOCIATIVE ENTITY
        UserDTO newUser = new UserDTO();
        newUser.setLogin("Gracjan");
        newUser.setPassword("Ola");
        System.out.println("adding new user to database");

        GroupDTO groupR = new GroupDTO();
        groupR.setName("GRUPA DO ŁĄCZENIA");
        System.out.println("adding new group to database");

        groupR.setId(1);
        groupR.setDescription("łączenie z grupą users");

        newUser.addGroup(groupR);
        userRepository.addOrUpdate(newUser);
        groupRepository.addOrUpdate(groupR);
        System.out.println("successfully assigned the user to the group");
        */

        /* FORTH PART
        UserDTO newUser1 = new UserDTO();
        newUser1.setLogin("Zdzisław");
        newUser1.setPassword("Zgrywus#");
        UserDTO newUser2 = new UserDTO();
        newUser2.setLogin("Trybus");
        newUser2.setPassword("Łapa#");
        UserDTO newUser3 = new UserDTO();
        newUser3.setLogin("Pelikan");
        newUser3.setPassword("Cezac#");
        System.out.println("added some new users to the database");

        GroupDTO groupRA = new GroupDTO();
        groupRA.setName("GroupA");
        groupRA.setId(4);
        groupRA.setDescription("In this group user Zdzisław is supposed to be located");
        GroupDTO groupW = new GroupDTO();
        groupW.setName("GroupB");
        groupW.setId(5);
        groupW.setDescription("In this group Trybus and Pelikan are supposed to be located");

        newUser1.addGroup(groupRA);
        newUser2.addGroup(groupW);
        newUser3.addGroup(groupW);

        userRepository.addOrUpdate(newUser1);
        userRepository.addOrUpdate(newUser2);
        userRepository.addOrUpdate(newUser3);

        groupRepository.addOrUpdate(groupRA);
        groupRepository.addOrUpdate(groupW);

         */

        /* PART FOUR
        //testing the methods created in the GroupRepositoryImpl and UserRepositoryImpl

        * */

        //getCount() method in UserRepositoryImpl and GroupRepositoryImpl works!
        System.out.println(userRepository.getCount());
        System.out.println(groupRepository.getCount());

        //delete() method in UserRepositoryImpl and GroupRepositoryImpl works!
        //findById(int idNumber) method also works!
        //groupRepository.delete(groupRepository.findById(6));
        //userRepository.delete(userRepository.findById(4));

        //exists(int idNumber) in UserRepositoryImpl and GroupRepositoryImpl works!
        userRepository.exists(1);
        groupRepository.exists(6);

    }
}
