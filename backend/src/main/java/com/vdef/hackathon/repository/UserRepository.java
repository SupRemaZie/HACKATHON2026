package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserJPA, UUID>
{

}
