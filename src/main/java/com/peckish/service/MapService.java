package com.peckish.service;

import com.peckish.domain.Map;
import com.peckish.dto.MapDTO;
import com.peckish.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MapService {
    private final MapRepository mapRepository;


    // 맵 하나 조회
    public MapDTO getMap(int sid) {
        Map map = mapRepository.getMap(sid);
        return new MapDTO().toMapDTO(map);
    }

    // 카테고리 없음 + 인증/제보 구분 없음 = 기본 메서드(findAll()) 사용
    public List<MapDTO> findAll() {
        List<MapDTO> allMapDTOs = new ArrayList<>();
        List<Map> maps = mapRepository.findAll();
        for (Map map : maps) {
            MapDTO mapDTO = new MapDTO().toMapDTO(map);
            allMapDTOs.add(mapDTO);
        }
        return allMapDTOs;
    }

    // 카테고리 있음 + 구분 없음 = findAllByCate
    public List<MapDTO> findAllByCate(String cate) {
        log.info("MapService - findAllByCate: {}", cate);
        List<MapDTO> allMapDTOs = new ArrayList<>();
        List<Map> maps = mapRepository.findAllByCate(cate);
        for(Map map : maps) {
            MapDTO mapDTO = new MapDTO().toMapDTO(map);
            log.info(mapDTO.toString());
            allMapDTOs.add(mapDTO);
        }
        return allMapDTOs;
    }

    // 맵 정보 수정
    public void modifyMap(MapDTO mapDTO) {
        log.info("MapService - modifyMap: {}", mapDTO.toString());
        Long sid = mapDTO.getShopId(); // mapDTO에서 shopId 꺼내기
        double lat = mapDTO.getLat(); // 위도 꺼내기
        double lng = mapDTO.getLng(); // 경도 꺼내기
        mapRepository.modifyMap(sid, lat, lng);
    }

//    // 카테고리 없음, "영업 중"
//    public List<MapDTO> findAllByOpen() {
//        log.info("MapService - findAllByOpen");
//        List<MapDTO> allMapDTOs = new ArrayList<>();
//        List<Map> maps = mapRepository.selectAllByOpen(true);
//        for(Map map : maps) {
//            MapDTO mapDTO = new MapDTO().toMapDTO(map);
//            allMapDTOs.add(mapDTO);
//        }
//        return allMapDTOs;
//    }
//
//    // 카테고리 없음 + 구분 있음
//    public List<MapDTO> findAllByRole(int cert) {
//        List<MapDTO> allMapDTOs = new ArrayList<>();
//        // 제보된 것만 조회
//        if(cert == 1) {
//            List<Map> maps = mapRepository.findAllByUser(false);
//            log.info("findByUser: {}", cert);
//            for(Map map : maps) {
//                log.info(map.getShop().toString());
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//                log.info("findByUser - allMapDTOs: {}", allMapDTOs);
//            }
//            // 인증된 것만 조회
//        } else if(cert == 2) {
//            List<Map> maps = mapRepository.findAllByOwner(true);
//            log.info("findByOwner: {}", cert);
//            for(Map map : maps) {
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//                log.info("findByOwner - allMapDTOs: {}", allMapDTOs);
//            }
//        }
//        log.info("findAllByRole: {}", allMapDTOs.toString());
//        return allMapDTOs;
//    }
//
//    // "구분", "영업 중"
//    public List<MapDTO> findByRoleWithOpen(int cert) {
//        List<MapDTO> allMapDTOs = new ArrayList<>();
//        if(cert == 1) {
//            List<Map> maps = mapRepository.findAllByUserWithOpen(false, true);
//            for(Map map : maps) {
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//            }
//        }
//        if(cert == 2) {
//            List<Map> maps = mapRepository.findAllByOwnerWithOpen(true, true);
//            for(Map map : maps) {
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//            }
//        }
//        return allMapDTOs;
//    }
//
//    // 카테고리 있음 + 구분 있음
//    public List<MapDTO> findByCateWithRole(String cate, int cert) {
//        log.info("category, cert: {}, {}", cate, cert);
//        List<MapDTO> allMapDTOs = new ArrayList<>();
//
//        if(cert == 1) {
//            List<Map> maps = mapRepository.findByCategoryWithUser(cate, false);
//            for(Map map : maps) {
//                log.info("map(cert 1): {}", map.toString());
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//            }
//        } else if (cert == 2) {
//            List<Map> maps = mapRepository.findByCategoryWithOwner(cate, true);
//            for(Map map : maps) {
//                log.info("map(cert 2): {}", map.toString());
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//            }
//        }
//        log.info("allMapDTOs: {}", allMapDTOs.toString());
//        return allMapDTOs;
//    }
//
//    // "카테고리" + "영업 중"
//    public List<MapDTO> findByCateWithOpen(String cate) {
//        List<MapDTO> allMapDTOs = new ArrayList<>();
//        List<Map> maps = mapRepository.findByCateWithOpen(cate, true);
//        for(Map map : maps) {
//            MapDTO mapDTO = new MapDTO().toMapDTO(map);
//            allMapDTOs.add(mapDTO);
//        }
//        return allMapDTOs;
//    }
//
//    // "카테고리" + "구분" + "영업 중"
//    public List<MapDTO> findByCateWithRoleAndOpen(String cate, int cert) {
//        List<MapDTO> allMapDTOs = new ArrayList<>();
//        if(cert == 1) {
//            List<Map> maps = mapRepository.findByCateWithUserAndOpen(cate, false, true);
//            for(Map map : maps) {
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//            }
//        } else if (cert == 2) {
//            List<Map> maps = mapRepository.findByCateWithOwnerAndOpen(cate, true, true);
//            for(Map map : maps) {
//                MapDTO mapDTO = new MapDTO().toMapDTO(map);
//                allMapDTOs.add(mapDTO);
//            }
//        }
//        return allMapDTOs;
//    }
}
