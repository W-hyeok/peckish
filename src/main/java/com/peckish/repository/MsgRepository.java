package com.peckish.repository;

import com.peckish.domain.Msg;
import com.peckish.domain.MsgStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface MsgRepository extends JpaRepository<Msg, Long> {

    // roomId로 메시지 리스트 조회 (커스텀 쿼리)
    @Query("SELECT m FROM Msg m WHERE m.ROOM_ID = :roomId AND m.STATUS = 'ACTIVE' ")
    List<Msg> findMsgsByRoomId(Long roomId);

    @Query("SELECT COUNT(m) FROM Msg m WHERE m.ROOM_ID = :roomId AND m.STATUS = :unreadStatus AND m.EMAIL <> :ownerEmail")
    Long countUnreadMessages(@Param("roomId") Long roomId,
                             @Param("ownerEmail") String ownerEmail,
                             @Param("unreadStatus") MsgStatus unreadStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Msg m SET m.isRead = true " +
            "WHERE m.ROOM_ID = :roomId " +
            "  AND m.EMAIL <> :email " +
            "  AND m.isRead = false")
    int markMessagesAsRead(@Param("roomId") Long roomId, @Param("email") String email);




}
