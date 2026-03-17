package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.SiteJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<SiteJPA, Long> {
    List<SiteJPA> findByCreatedBy(Long userId);
    Optional<SiteJPA> findByToken(String token);
}
