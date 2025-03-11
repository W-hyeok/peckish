package com.peckish.service;

import com.peckish.domain.Participants;
import com.peckish.domain.Room;
import com.peckish.domain.ShopOwner;
import com.peckish.dto.RoomDTO;
import com.peckish.dto.ShopOwnerDTO;
import com.peckish.repository.MsgRepository;
import com.peckish.repository.ParticipantRepository;
import com.peckish.repository.RoomRepository;
import com.peckish.repository.ShopOwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

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
    private final MsgRepository msgRepository;
    private final ShopOwnerRepository shopOwnerRepository;

        public Room createRoom(String member1, String member2, Long shopId) {

            // 1. 상점 정보 및 상점 주인 조회
            ShopOwner ownerInfo = shopOwnerRepository.findById(shopId)
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
}

