package com.peckish.repository;

import com.peckish.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r " +
            "WHERE r.ROOM_ID " +
            "IN (SELECT p.ROOM_ID FROM Participants p WHERE p.email = :memberEmail)")
    List<Room> getRoomsByUserName(@Param("memberEmail") String memberEmail);

    @Query("select distinct r from Room r " +
            "join Participants p1 on r.ROOM_ID = p1.ROOM_ID " +
            "join Participants p2 on r.ROOM_ID = p2.ROOM_ID " +
            "where (p1.email = :member1 and p2.email = :member2) " +
            "   or (p1.email = :member2 and p2.email = :member1)")
    List<Room> findRoomByMemberEmails(@Param("member1") String member1,
                                          @Param("member2") String member2);

}
