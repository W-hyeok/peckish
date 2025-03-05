package com.peckish.controller;

import com.peckish.dto.PageRequestDTO;
import com.peckish.dto.PageResponseDTO;
import com.peckish.dto.ProductDTO;
import com.peckish.service.ProductService;
import com.peckish.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/products")
@RequiredArgsConstructor // 생성자 자동 주입
public class ProductController {
    private final FileUtil fileUtil;
    private final ProductService productService;

    @PostMapping("/")
    public Map<String, Long> regist(ProductDTO productDTO) {
        log.info("상품 등록 - productDTO: {}", productDTO.toString());

        // 파일 저장 처리
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);
        log.info("파일 업로드 - uploadFileNames: {}", uploadFileNames);

        // DTO에 저장된 파일 이름들 추가
        productDTO.setUploadFileNames(uploadFileNames);

        // service 호출 - DB 저장
        Long pno = productService.regist(productDTO);

        return Map.of("RESULT", pno);
    }




    // 이미지 조회: http://localhost:8080/api/products/view/{확인할 이미지명}.확장자명
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewImage(@PathVariable("filename") String filename) {
        return fileUtil.getFile(filename);
    }

    // 게시글 목록 요청
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')") // 권한 추가 예시
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("/products/list  - pageRequestDTO: {}", pageRequestDTO.toString());
        return productService.getProductList(pageRequestDTO);
    }

    // 상품 1개 조회
    @GetMapping("/{pno}")
    public ProductDTO getProduct(@PathVariable("pno") Long pno) {
        return productService.getProductByPno(pno);
    }

    // 상품 수정
    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable(name = "pno") Long pno, ProductDTO productDTO) {
        log.info("modify - productDTO: {}", productDTO);

        productDTO.setPno(pno);
        ProductDTO oldDTO = productService.getProductByPno(pno); // DB에 저장된 이전 버전 조회
        List<String> oldFilenames = oldDTO.getUploadFileNames(); // 기존 파일명들

        List<MultipartFile> newFiles = productDTO.getFiles(); // 새로 업로드할 파일들 (새로 추가된 것들)
        List<String> newFilenames = fileUtil.saveFiles(newFiles); // 추가로 저장된 새로운 파일명들

        List<String> remainFilenames = productDTO.getUploadFileNames(); // (기존의)유지될 파일명들

        // 유지되는 파일명 목록에, 새로 추가되는 파일명 합치기
        if(newFilenames != null && !remainFilenames.isEmpty()) {
            remainFilenames.addAll(newFilenames);
        }

        // DB 수정처리
        productService.modify(productDTO);

        // 기존에 저장된 파일들 중 삭제파일 찾아 삭제
        if(oldFilenames != null && !oldFilenames.isEmpty()) {
            // 파일명 목록 찾고,
            oldFilenames.stream().filter(filename
                    -> !remainFilenames.contains(filename))
                    .collect(Collectors.toList());
            // 파일 삭제
            fileUtil.deleteFile(remainFilenames);
        }
        return Map.of("수정 결과: ", "성공");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable("pno") Long pno) {
        List<String> filenamesToRemove = productService.getProductByPno(pno).getUploadFileNames();
        productService.remove(pno); // delFlag = true 로 변경
        fileUtil.deleteFile(filenamesToRemove); // 파일(이미지) 삭제
        return Map.of("삭제 결과: ", "성공");
    }
}
