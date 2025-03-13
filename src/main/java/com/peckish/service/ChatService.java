package com.peckish.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.peckish.domain.Msg;
import com.peckish.domain.MsgStatus;
import com.peckish.domain.Room;
import com.peckish.dto.ChatRoom;
import com.peckish.dto.MsgDTO;
import com.peckish.dto.RoomListDTO;
import com.peckish.repository.ChatRepository;
import com.peckish.repository.MsgRepository;
import com.peckish.util.ChatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@CrossOrigin(origins = "http://localhost:5173")
public class ChatService {

    private final ChatRepository chatRepository;
    private final MsgRepository msgRepository;
    private final PapagoTranslationService translationService;
    private final MsgService msgService;

    ObjectMapper objectMapper = new ObjectMapper();


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

//    public List<RoomListDTO> getChatList(String managerEmail) {
//        List<RoomListDTO> roomList = new ArrayList<>();
//
//        // 모든 채팅방의 ROOM_ID를 가져옵니다.
//        List<Long> roomIds = msgRepository.findDistinctRoomIds();
//
//        for (Long roomId : roomIds) {
//            // 해당 채팅방의 메시지를 최신순으로 가져옵니다.
//            List<Msg> messages = msgRepository.findMessagesByRoomIdOrderByRegDateDesc(roomId);
//            if (messages.isEmpty()) continue;
//            Msg lastMsg = messages.get(0);
//
//            // 사장님이 아닌 사용자가 보낸 UNREAD 메시지 개수를 계산합니다.
//            Long unreadCount = msgRepository.countUnreadMessages(roomId, managerEmail, MsgStatus.UNREAD);
//
//            // 마지막 메시지가 UNREAD 상태이고, 발신자가 사장님이 아닌 경우에만 content에 표시합니다.
//            String contentToDisplay = "";
//            if (lastMsg.getSTATUS() == MsgStatus.UNREAD && !lastMsg.getUSERNAME().equals(managerEmail)) {
//                contentToDisplay = lastMsg.getCONTENT();
//            }
//
//            // 고객의 닉네임을 결정합니다.
//            String userNickname = "";
//            if (!lastMsg.getUSERNAME().equals(managerEmail)) {
//                userNickname = lastMsg.getUSERNAME();
//            } else {
//                // 만약 마지막 메시지가 사장님이 보낸 메시지라면, 최근 고객 메시지를 찾아서 닉네임으로 사용합니다.
//                for (Msg m : messages) {
//                    if (!m.getUSERNAME().equals(managerEmail)) {
//                        userNickname = m.getUSERNAME();
//                        break;
//                    }
//                }
//            }
//
//            // 프로필 사진은 별도의 회원 정보를 조회할 수 있다면 그 값을 사용합니다.
//            // 여기서는 기본값을 사용합니다.
//            String photoPath = "/default.jpg";
//
//            // roomName은 간단하게 고객의 닉네임을 사용하거나 별도의 로직을 적용할 수 있습니다.
//            String roomName = userNickname;
//
//            RoomListDTO dto = new RoomListDTO();
//            dto.setROOM_ID(roomId);
//            dto.setContent(contentToDisplay);
//            dto.setUserNickname(userNickname);
//            dto.setPhotoPath(photoPath);
//            dto.setUnreadCount(unreadCount);
//
//            roomList.add(dto);
//        }
//
//        return roomList;
//    }
}
