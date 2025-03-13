package com.peckish.repository;

import com.peckish.domain.Msg;
import com.peckish.domain.MsgStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface MsgRepository extends JpaRepository<Msg, Long> {

    // roomId로 메시지 리스트 조회 (커스텀 쿼리)
    @Query("SELECT m FROM Msg m WHERE m.ROOM_ID = :roomId AND m.STATUS = 'ACTIVE' ")
    List<Msg> findMsgsByRoomId(Long roomId);

    @Query("SELECT COUNT(m) FROM Msg m WHERE m.ROOM_ID = :roomId AND m.STATUS = :unreadStatus AND m.USERNAME <> :ownerEmail")
    Long countUnreadMessages(@Param("roomId") Long roomId,
                             @Param("ownerEmail") String ownerEmail,
                             @Param("unreadStatus") MsgStatus unreadStatus);



//    // 1. 모든 채팅방의 ROOM_ID 목록을 조회 (중복 제거)
//    @Query("SELECT DISTINCT m.ROOM_ID FROM Msg m")
//    List<Long> findDistinctRoomIds();
//
//    // 2. 특정 ROOM_ID의 메시지들을 최신순(REG_DATE 내림차순)으로 조회
//    @Query("SELECT m FROM Msg m WHERE m.ROOM_ID = :roomId ORDER BY m.REG_DATE DESC")
//    List<Msg> findMessagesByRoomIdOrderByRegDateDesc(@Param("roomId") Long roomId);
//
//    // 3. 특정 ROOM_ID에서, 사장님(매개변수 managerEmail)이 아닌 사용자가 보낸 UNREAD 메시지 개수를 계산
//    @Query("SELECT COUNT(m) FROM Msg m WHERE m.ROOM_ID = :roomId AND m.STATUS = :unreadStatus AND m.USERNAME <> :managerEmail")
//    Long countUnreadMessages(@Param("roomId") Long roomId,
//                             @Param("managerEmail") String managerEmail,
//                             @Param("unreadStatus") MsgStatus unreadStatus);
}
