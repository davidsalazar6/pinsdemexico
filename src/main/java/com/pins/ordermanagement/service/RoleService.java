package com.pins.ordermanagement.service;

import com.pins.ordermanagement.model.Role;
import com.pins.ordermanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public List<Role> getByRoleName(String roleName){
        return roleRepository.findByRoleName(roleName);
    }
}
