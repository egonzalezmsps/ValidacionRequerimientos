package com.coppel.sla.repositories;

import com.coppel.sla.entities.ApiConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiConfigRepository extends JpaRepository<ApiConfigEntity, String> {

}