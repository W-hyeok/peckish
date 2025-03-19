package com.peckish.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.peckish.domain.Msg;
import com.peckish.domain.MsgStatus;
import com.peckish.domain.Participants;
import com.peckish.domain.Room;
import com.peckish.dto.ChatRoom;
import com.peckish.dto.MsgDTO;
import com.peckish.repository.ChatRepository;
import com.peckish.repository.MsgRepository;
import com.peckish.repository.ParticipantRepository;
import com.peckish.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@CrossOrigin(origins = "http://localhost:5173")
public class ChatService {

    private final MsgRepository msgRepository;
    private final ChatRepository chatRepository;
    private final MsgService msgService;

    ObjectMapper objectMapper = new ObjectMapper();
    private final ParticipantRepository participantRepository;


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
        }catch (Exception e) {
            log.error("JSON 파싱 오류: {}", e.getMessage(), e);
        }


        // 기본 Msg 객체 생성
        Msg msg = new Msg();
        msg.setROOM_ID(chatMsg.getROOM_ID());
        msg.setCONTENT(chatMsg.getCONTENT());
        msg.setEMAIL(chatMsg.getEmail());
        String msgType = String.valueOf(chatMsg.getMessageType());
        msg.setSTATUS(msgType.equals("TALK") ? MsgStatus.ACTIVE : MsgStatus.INACTIVE);
        msg.setREG_DATE(chatMsg.getReg_date());

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

    public int markMessagesAsRead(Long roomId, String email) {
        return msgRepository.markMessagesAsRead(roomId, email);
    }

    public List<String> getParticipantsEmails(Long roomId, String senderEmail) {
        List<Participants> participantList = participantRepository.findByROOM_ID(roomId);
        return participantList.stream()
                .map(Participants::getEmail)
                .filter(email -> !email.equals(senderEmail))
                .collect(Collectors.toList());
    }

    // 지정된 방에서 recipientEmail이 읽지 않은 메시지 개수를 반환
    public int getUnreadCount(Long roomId, String recipientEmail) {
        return msgRepository.countUnreadMessagesByRoomAndRecipient(roomId, recipientEmail);
    }

}
