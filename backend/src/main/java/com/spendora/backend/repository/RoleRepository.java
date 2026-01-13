package com.spendora.backend.repository;

import com.spendora.backend.entity.Role;
import com.spendora.backend.entity.util.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName roleName);
}

