package com.peckish.service;

import com.peckish.repository.ShopOwnerRepository;
import com.peckish.repository.ShopUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    @Autowired
    private ShopOwnerRepository shopOwnerRepository;

    @Autowired
    private ShopUserRepository shopUserRepository;

    // 매 6시마다 업데이트
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void scheduledUser() { // 유저 부분 데이터 변경용 메서드
        // DB에 저장된 시간값이 String, 양식은 00:00 이므로 LocalTime을 사용해야 함
        LocalTime currentTime = LocalTime.now(); // 현재 시간 객체 생성. 상술했듯 LocalDateTime 말고 LocalTime 써야 함

        // JPA 기본 메서드 findAll() 사용: 전체 데이터 조회 후 반복문 시행. (최적화 위해서는 isExist 조건도 걸어야...?)
        shopUserRepository.findAll().forEach(shopUser -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // 시간(DateTime) 형식 "HH:mm"으로 설정
            LocalTime openTime = LocalTime.parse(shopUser.getOpenTime(), formatter); // 개점 시간 parsing.
            LocalTime closeTime = LocalTime.parse(shopUser.getCloseTime(), formatter); // 페점 시간 parsing.
            log.info("개점시간(유저)!!: {}, 폐점시간(유저)!!: {}", openTime, closeTime); // 로그. 전체 DB의 개점/폐점 시간 데이터 출력됨
            boolean isOpen = currentTime.isAfter(openTime) && currentTime.isBefore(closeTime); // isAfter({parameter})와 isBefore({parameter}) 모두 충족 시 true, 아니면 false
            shopUser.changeIsOpen(isOpen); // DB의 'isOpen' 값을 위의 boolean 변수(isOpen)의 값으로 설정
        });
    }

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void scheduledOwner() {
        LocalTime currentTime = LocalTime.now();

        shopOwnerRepository.findAll().forEach(shopOwner -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime openTime = LocalTime.parse(shopOwner.getOpenTime(), formatter);
            LocalTime closeTime = LocalTime.parse(shopOwner.getCloseTime(), formatter);
            log.info("개점시간(오너)!!: {}, 폐점시간(오너)!!: {}", openTime, closeTime);
            boolean isOpen = currentTime.isAfter(openTime) && currentTime.isBefore(closeTime);
            shopOwner.changeIsOpen(isOpen);
        });
    }

}
