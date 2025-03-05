package com.peckish.controller;

import com.peckish.dto.MapDTO;
import com.peckish.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {
    private final MapService mapService;
    // 맵 목록 조회
//    @GetMapping
//    public List<MapDTO> mapList() {
//        log.info("mapList");
//        return mapService.findAll();
//    }

    // 맵 하나 조회
    @GetMapping("/map/{sid}")
    public MapDTO getMap(@PathVariable("sid") int sid) {
        return mapService.getMap(sid);
    }

    // 맵 목록 필터링 조회
    // 필터: 카테고리(all, bread, snack, s.p, h.t) / 인증(0(모두), 1(제보), 2(인증)) / 영업(0(모두), 1(영업 중))
    @GetMapping("/{krCate}/{cert}/{open}")
    public List<MapDTO> mapList(@PathVariable("krCate") String krCate, @PathVariable("cert") int cert, @PathVariable("open") int open) {

        // 카테고리 한글 -> 영어 변환용 변수
        String category = "";
        log.info("목록 조회(controller) - category, cert, open: {}, {}, {}", krCate, cert, open);

        // 카테고리 없음
        if(Objects.equals(krCate, "all")) {
            if(cert == 0) {
                // 카테고리 없음, 구분 없음, 준비 중 포함 == 필터 없는 상태
                if(open == 0) {
                    log.info("findAll");
                    return mapService.findAll();
                    // 카테고리 없음, 구분 없음, "영업 중"
                } else if (open == 1) {
                    log.info("findAllByOpen");
                    return mapService.findAllByOpen();
                }
            }
            // 카테고리 없음, "구분"
            if(cert != 0) {
                // 카테고리 없음, "구분", 준비중 포함
                if(open == 0) {
                    log.info("findAllByRole");
                    return mapService.findAllByRole(cert);
                    // 카테고리 없음, "구분", "영업 중"
                } else if (open == 1) {
                    log.info("findByRoleWithOpen");
                    return mapService.findByRoleWithOpen(cert);
                }
            }
            // 카테고리가 "all"은 아닌데 "필터 초기화"인 경우
        } else if (Objects.equals(krCate, "필터 초기화")) { // 필터 초기화 시 cate, open 무시하고 findAll 실행
            log.info("findAll, cert: {}, {}", krCate, cert);
            return mapService.findAll();
        }

        // "카테고리" (카테고리 값이 "all"이 아니고, "필터 초기화"도 아닌 경우)
        if(!Objects.equals(krCate, "all") && !Objects.equals(krCate, "필터 초기화")) {
            // 카테고리 영문(db값)으로 변환
            if(Objects.equals(krCate, "붕어빵")) {
                category = "bread";
            } else if(Objects.equals(krCate, "분식")) {
                category = "snack";
            } else if (Objects.equals(krCate, "군고구마")) {
                category = "sweetPotato";
            } else if (Objects.equals(krCate, "호떡")) {
                category = "hotteok";
            }
            log.info("변환된 카테고리 값 - findByCate: {}", category);

            // "카테고리", 구분 없음
            if(cert == 0) {
                // "카테고리", 구분 없음, 준비중 포함
                if(open == 0) {
                    log.info("findAllByCate");
                    return mapService.findAllByCate(category);
                    // "카테고리", 구분 없음, "영업 중"
                } else if(open == 1) {
                    log.info("findByCateWithOpen");
                    return mapService.findByCateWithOpen(category);
                }
            }
            // "카테고리", "구분"
            if(cert != 0) {
                // "카테고리", "구분", 준비중 포함
                if(open == 0) {
                    return mapService.findByCateWithRole(category, cert);
                }
                // "카테고리", "구분", "영업 중"
                if(open == 1) {
                    return mapService.findByCateWithRoleAndOpen(category, cert);
                }
            }
        }

        return null;
    }
}

