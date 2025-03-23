package com.peckish.dto;

import com.peckish.domain.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class MapDTO {
    private Long mapId;
    private String title;
    private double lat;
    private double lng;
    private Long shopId;
    private String category;
    private boolean isOpen;
    private String Location;
    private boolean certificate;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String days;
    private String status;
    private String filename;

    // Entity → DTO
    public MapDTO toMapDTO(Map map) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.now();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        String currentDate = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        MapDTO mapDTO = new MapDTO();
        mapDTO.setMapId(map.getMapId());
        mapDTO.setShopId(map.getShop().getShopId());
        mapDTO.setLat(map.getLat());
        mapDTO.setLng(map.getLng());
        // certificate = true → owner
        if(map.getShop().isCertificate()) {
            mapDTO.setCertificate(true);
            mapDTO.setTitle(map.getShop().getShopOwner().getTitle());
            mapDTO.setLocation(map.getShop().getShopOwner().getLocation());
            mapDTO.setCategory(map.getShop().getShopOwner().getCategory());
            mapDTO.setOpenTime(LocalTime.parse(map.getShop().getShopOwner().getOpenTime(), formatter));
            mapDTO.setCloseTime(LocalTime.parse(map.getShop().getShopOwner().getCloseTime(),formatter));
            mapDTO.setFilename(map.getShop().getShopOwner().getFilename());
            mapDTO.setOpen(map.getShop().getShopOwner().isOpen());
//            mapDTO.setStatus(!mapDTO.isOpen() ? "closed" : (!(currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime())) ? "closed" : "opened"));
            // (시간 조건) 및 isOpen 둘 중 하나라도 open이면 opened
            mapDTO.setDays(map.getShop().getShopOwner().getDays());
            if((currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime())) || mapDTO.isOpen() && mapDTO.days.contains(currentDate)) {
                mapDTO.setStatus("opened");
            } else {
                mapDTO.setStatus("closed");
            }
        } else {
            mapDTO.setCertificate(false);
            mapDTO.setTitle(map.getShop().getShopUser().getTitle());
            mapDTO.setLocation(map.getShop().getShopUser().getLocation());
            mapDTO.setCategory(map.getShop().getShopUser().getCategory());
            mapDTO.openTime = LocalTime.parse(map.getShop().getShopUser().getOpenTime(), formatter);
            mapDTO.closeTime = LocalTime.parse(map.getShop().getShopUser().getCloseTime(), formatter);
            mapDTO.setFilename(map.getShop().getShopUser().getFilename());
            mapDTO.setOpen(map.getShop().getShopUser().isOpen());
            mapDTO.setDays(map.getShop().getShopUser().getDays());
            // 제보된 가게의 경우 시간 조건만 맞으면 개점으로 판정.
            mapDTO.setStatus(currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime()) && mapDTO.days.contains(currentDate) ? "opened" : "closed");
        }
        return mapDTO;
    }

    // DTO -> Entity
    public Map toEntity() {
        Map map = Map.builder()
                .mapId(this.mapId)
                .title(this.title)
                .lat(this.lat)
                .lng(this.lng)
                .build();
        return map;
    }
}
