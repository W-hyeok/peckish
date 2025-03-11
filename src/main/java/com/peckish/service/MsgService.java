package com.peckish.service;

import com.peckish.domain.Msg;
import com.peckish.repository.MsgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MsgService {

    private final MsgRepository msgRepository;

    // 메시지 리스트 가져오기
    public List<Msg> getMsgs(Long roomId) {
        return msgRepository.findMsgsByRoomId(roomId);
    }

    // 메시지 저장
    public Msg save(Msg msg) {
        return msgRepository.save(msg);
    }


}
