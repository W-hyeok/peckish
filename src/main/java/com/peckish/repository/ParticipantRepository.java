package com.peckish.repository;

import com.peckish.domain.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participants, Long> {
    @Query("SELECT p.ROOM_ID, u.profileFilename FROM Participants p " +
            "JOIN Member u ON p.email = u.email " +
            "WHERE p.email = :memberEmail")
    List<Object[]> getRoomIdAndPhotoPathByUserName(@Param("memberEmail") String memberEmail);
}
