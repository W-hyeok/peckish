package com.peckish.repository;

import com.peckish.domain.ReviewUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewUserRepository extends JpaRepository<ReviewUser, Long> {
}
