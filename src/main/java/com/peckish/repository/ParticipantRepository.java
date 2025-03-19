package com.peckish.repository;

import com.peckish.domain.Participants;
import com.peckish.dto.RoomListDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participants, Long> {
    @Query("SELECT p.ROOM_ID, m.profileFilename, " +
            "  (SELECT COUNT(msg) FROM Msg msg " +
            "   WHERE msg.ROOM_ID = p.ROOM_ID " +
            "     AND msg.isRead = false " +
            "     AND msg.EMAIL <> :memberEmail) " +
            "FROM Participants p JOIN Member m ON p.email = m.email " +
            "WHERE p.ROOM_ID IN (SELECT p2.ROOM_ID FROM Participants p2 WHERE p2.email = :memberEmail) " +
            "  AND p.email <> :memberEmail")
    List<Object[]> getRoomIdPhotoAndUnreadCountByUserName(@Param("memberEmail") String memberEmail);

    // 사장님(즉, ownerEmail)이 참여한 모든 참가자 행을 조회
    List<Participants> findByEmail(String email);

    // 특정 방(roomId)에서 사장님을 제외한 다른 참가자의 닉네임 조회
    @Query(value = "SELECT m.nickname, " +
            "       (SELECT msg.content FROM msg msg WHERE msg.room_id = :roomId ORDER BY msg.reg_date DESC LIMIT 1) AS latestContent " +
            "FROM participants p " +
            "JOIN member m ON p.email = m.email " +
            "WHERE p.room_id = :roomId AND p.email <> :ownerEmail",
            nativeQuery = true)
    List<Object[]> findUserNicknameAndLatestMessageByRoomId(@Param("roomId") Long roomId,
                                                            @Param("ownerEmail") String ownerEmail);

}
