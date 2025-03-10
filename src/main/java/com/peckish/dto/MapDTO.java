package com.peckish.dto;

import com.peckish.domain.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    private String status;

    // Entity → DTO
    public MapDTO toMapDTO(Map map) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.now();
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
            mapDTO.setOpen(map.getShop().getShopOwner().isOpen());
//            mapDTO.setStatus(mapDTO.isOpen() ? "opened" : (currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime()) ? "opened" : "closed"));
//            mapDTO.setStatus(currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime()) ? "opened" : (mapDTO.isOpen() ? "opened" : "closed"));
            mapDTO.setStatus(!mapDTO.isOpen() ? "closed" : (currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime()) ? "opened" : "closed"));
        }
        if (!map.getShop().isCertificate()) {
            mapDTO.setCertificate(false);
            mapDTO.setTitle(map.getShop().getShopUser().getTitle());
            mapDTO.setLocation(map.getShop().getShopUser().getLocation());
            mapDTO.setCategory(map.getShop().getShopUser().getCategory());
            mapDTO.openTime = LocalTime.parse(map.getShop().getShopUser().getOpenTime(), formatter);
            mapDTO.closeTime = LocalTime.parse(map.getShop().getShopUser().getCloseTime(), formatter);
            mapDTO.setOpen(map.getShop().getShopUser().isOpen());
            // 제보된 가게의 경우 시간 조건만 맞으면 개점으로 판정.
            mapDTO.setStatus(currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime()) ? "opened" : "closed");
//            mapDTO.setStatus(!mapDTO.isOpen() ? "closed" : (currentTime.isAfter(mapDTO.getOpenTime()) && currentTime.isBefore(mapDTO.getCloseTime()) ? "opened" : "closed"));
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
