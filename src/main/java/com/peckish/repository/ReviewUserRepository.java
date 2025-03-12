package com.peckish.repository;

import com.peckish.domain.ReviewUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewUserRepository extends JpaRepository<ReviewUser, Long> {



}
