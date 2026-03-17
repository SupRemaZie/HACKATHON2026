package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.EmissionFactorJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmissionFactorRepository extends JpaRepository<EmissionFactorJPA, UUID> {
    List<EmissionFactorJPA> findByCategory(String category);
    Optional<EmissionFactorJPA> findByAdemeId(String ademeId);
    List<EmissionFactorJPA> findByMaterialNameContainingIgnoreCase(String name);
    boolean existsByMaterialNameAndYear(String materialName, Integer year);
}
