package com.peckish.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.peckish.domain.Msg;
import com.peckish.domain.MsgStatus;
import com.peckish.domain.Room;
import com.peckish.dto.ChatRoom;
import com.peckish.dto.MsgDTO;
import com.peckish.repository.ChatRepository;
import com.peckish.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@CrossOrigin(origins = "http://localhost:5173")
public class ChatService {

    private final ChatRepository chatRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PapagoTranslationService translationService;
    private final MsgService msgService;
    public List<Room> findAll() {
        return chatRepository.findAll();
    }

    public Room findRoomById(Long roomId) {
        return chatRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
    }

    public ChatRoom findChatRoomById(Long roomId) {
        Room room = findRoomById(roomId);
        return convertToChatRoom(room);
    }


    public void handleAction(Long roomId, WebSocketSession session, MsgDTO msgDTO)  {
        log.info("handle action START");
        ChatRoom chatRoom = findChatRoomById(roomId);

        // 사용자가 채팅방에 입장할 때 혹시 두번뜨면 여기 지우기.
        if (isEnterRoom(msgDTO)) {
            if (session.getAttributes().get("hasEntered") == null) {
                chatRoom.join(roomId, session); // 세션을 채팅방에 추가
                session.getAttributes().put("hasEntered", true); // 입장 여부 플래그 설정
                msgDTO.setCONTENT("채팅방이 개설되었습니다.");  // 입장 메시지 설정
            }
        }else {
            // 다른 메시지 처리 로직
        }

        // 메시지 객체 생성
        TextMessage textMessage = ChatUtil.Chat.resolveTextMessage(msgDTO);
        log.info("textMessage########## : {}", textMessage.getPayload());

        // 메시지 전송
        chatRoom.sendMessage(roomId, session, textMessage);  // 세션을 포함하여 메시지를 전송

        MsgDTO chatMsg = null;
        try {
            chatMsg =objectMapper.readValue(textMessage.getPayload(), MsgDTO.class);
            log.info("chatMsg {}", chatMsg);
        }catch (Exception e) {}


        log.info("MEssageTTTTT {}", chatMsg.getEmail());
        // 기본 Msg 객체 생성
        Msg msg = new Msg();
        msg.setROOM_ID(chatMsg.getROOM_ID());
        msg.setCONTENT(chatMsg.getCONTENT());
        msg.setUSERNAME(chatMsg.getEmail());
        String msgType = String.valueOf(chatMsg.getMessageType());
        msg.setSTATUS(msgType.equals("TALK") ? MsgStatus.ACTIVE : MsgStatus.INACTIVE);
        msg.setREG_DATE(chatMsg.getReg_date());

        // 언어 감지 후 번역 및 각 언어로 저장
        String content = chatMsg.getCONTENT();

        // 한국어 번역
        String translatedKo = translationService.translateText(content, "ko");
        msg.setKO(translatedKo);

        // 영어 번역
        String translatedEn = translationService.translateText(content, "en");
        msg.setEN(translatedEn);

        // 일본어 번역
        String translatedJa = translationService.translateText(content, "ja");
        msg.setJA(translatedJa);

        // 중국어 번역
        String translatedCh = translationService.translateText(content, "zh-CN");
        msg.setCH(translatedCh);


        log.info("msg123 : {}",msg);
        msgService.save(msg);
    }

    private boolean isEnterRoom(MsgDTO msgDTO) {
        log.info("MsgDTO.getMessageType() {}", msgDTO.getMessageType());
        List<Msg> is_first = msgService.getMsgs(msgDTO.getROOM_ID());

        return is_first.isEmpty();
    }

    private ChatRoom convertToChatRoom(Room room) {
        return ChatRoom.of(room.getROOM_ID(), room.getROOM_NAME());
    }
}
