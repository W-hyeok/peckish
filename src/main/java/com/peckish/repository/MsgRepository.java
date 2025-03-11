package com.peckish.repository;

import com.peckish.domain.Msg;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional
public interface MsgRepository extends JpaRepository<Msg, Long> {

    // roomId로 메시지 리스트 조회 (커스텀 쿼리)
    @Query("SELECT m FROM Msg m WHERE m.ROOM_ID = :roomId AND m.STATUS = 'ACTIVE' ")
    List<Msg> findMsgsByRoomId(Long roomId);


}
