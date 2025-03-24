package com.peckish.controller;


import com.peckish.domain.Msg;
import com.peckish.domain.Room;
import com.peckish.dto.RoomDTO;
import com.peckish.dto.RoomListDTO;
import com.peckish.service.MsgService;
import com.peckish.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "https://hungrymoment.store"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;
    private final MsgService msgService;

    @GetMapping("/list/{memberEmail}")
    public List<Room> list(@PathVariable("memberEmail") String memberEmail) {
        log.info("list^^^^^^^^^^  :{}", roomService.getMyRooms(memberEmail));
        return roomService.getMyRooms(memberEmail);
    }

    @GetMapping("/listOwner/{ownerEmail}")
    public ResponseEntity<List<RoomListDTO>> getRoomList(@PathVariable String ownerEmail) {
        List<RoomListDTO> roomList = roomService.getRoomList(ownerEmail);
        return ResponseEntity.ok(roomList);
    }

    @GetMapping("/listDetail/{memberEmail}")
    public List<RoomDTO> listDetail(@PathVariable("memberEmail") String memberEmail) {
        return roomService.getRoomDetailsByUserName(memberEmail);
    }


    @GetMapping("/msgs/{room_ID}")
    public List<Msg> getMsgs(@PathVariable("room_ID") Long roomId, HttpServletRequest request) {
        return msgService.getMsgs(roomId);
    }
    @PostMapping("/create")
    public Room createRoom(@RequestBody Map<String, String> body) {
        String member1 = body.get("member1");
        String member2 = body.get("member2");
        String shopId = body.get("shopId");
        log.info("user1 : {}", member1);
        log.info("user2 : {}", member2);
        log.info("shopId : {}", shopId);

        // 방 생성 로직
        Room room = roomService.createRoom(member1, member2, Long.valueOf(shopId));
        return room;
    }


}
