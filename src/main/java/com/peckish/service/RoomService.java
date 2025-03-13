package com.peckish.service;

import com.peckish.controller.formatter.LocalDateFormatter;
import com.peckish.controller.formatter.LocalDateTimeFormatter;
import com.peckish.domain.*;
import com.peckish.dto.RoomDTO;
import com.peckish.dto.RoomListDTO;
import com.peckish.repository.MsgRepository;
import com.peckish.repository.ParticipantRepository;
import com.peckish.repository.RoomRepository;
import com.peckish.repository.ShopOwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@CrossOrigin(origins = "http://localhost:5173")
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ParticipantRepository participantsRepository;
    private final MsgRepository msgRepository;

        public Room createRoom(String member1, String member2, Long shopId) {

            // 1. 상점 정보 및 상점 주인 조회
            ShopOwner ownerInfo = shopOwnerRepository.findByShop_ShopId(shopId)
                    .orElseThrow(() -> new EntityNotFoundException("ShopOwner not found for shopId: " + shopId));


            // 2. 이미 해당 멤버들로 생성된 방이 있는지 확인
            List<Room> existingRoom = roomRepository.findRoomByMemberEmails(member1, member2);

            if(!existingRoom.isEmpty()){
                // 첫번째 대화방을 반환
                return existingRoom.get(0);
            }

            // 3. 방 생성
            Room room = new Room();
            room.setROOM_LIMIT(2L);
            room.setROOM_NAME(ownerInfo.getTitle());
            room.setREG_DATE(LocalDateTime.now());
            room.setTYPE(RoomType.PRIVATE);
            room = roomRepository.save(room);  // 방 저장

            // 4. 두 사용자 방에 참가시킴
            Participants participant1 = new Participants();
            participant1.setROOM_ID(room.getROOM_ID());
            participant1.setEmail(member1);
            participantRepository.save(participant1);

            Participants participant2 = new Participants();
            participant2.setROOM_ID(room.getROOM_ID());
            participant2.setEmail(member2);
            participantRepository.save(participant2);

            return room;  // 생성된 방 반환
        }
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getMyRooms(String memberEmail) {
        return  roomRepository.getRoomsByUserName(memberEmail);
    }
    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }
    public List<RoomDTO> getRoomDetailsByUserName(String memberEmail) {
        List<Object[]> results = participantRepository.getRoomIdAndPhotoPathByUserName(memberEmail);
        return results.stream().map(result -> {
            Long roomId = (Long) result[0]; // 첫 번째 요소는 ROOM_ID
            String photoPath = (String) result[1]; // 두 번째 요소는 photo_path

            return new RoomDTO(
                    roomId,
                    photoPath
            );
        }).collect(Collectors.toList());
    }

    public List<RoomListDTO> getRoomList(String ownerEmail) {
        // 사장님(채팅방의 주체)이 참여한 참가자 행들을 조회
        List<Participants> ownerParticipants = participantsRepository.findByEmail(ownerEmail);
        List<RoomListDTO> result = new ArrayList<>();

        for (Participants ownerParticipant : ownerParticipants) {
            Long roomId = ownerParticipant.getROOM_ID();
            Optional<Room> roomOpt = roomRepository.findById(roomId);
            if (!roomOpt.isPresent()) {
                continue;
            }
            Room room = roomOpt.get();

            // 해당 채팅방에서 사장님을 제외한 다른 참가자의 닉네임과 최신 메시지(content) 조회
            List<Object[]> queryResult = participantsRepository.findUserNicknameAndLatestMessageByRoomId(roomId, ownerEmail);
            String userNickname = "";
            String latestContent = "";
            if (!queryResult.isEmpty()) {
                Object[] row = queryResult.get(0);
                userNickname = row[0] != null ? row[0].toString() : "";
                latestContent = row[1] != null ? row[1].toString() : "";
            }

            // MsgRepository를 통해 해당 채팅방의 안읽은 메시지 개수를 조회
            Long unreadCount = msgRepository.countUnreadMessages(roomId, ownerEmail, MsgStatus.ACTIVE);

            RoomListDTO roomListDTO = new RoomListDTO();
            roomListDTO.setROOM_ID(room.getROOM_ID());
            roomListDTO.setContent(latestContent);
            roomListDTO.setUnreadCount(unreadCount);
            roomListDTO.setUserNickname(userNickname);

            result.add(roomListDTO);
        }
        return result;
    }
}

