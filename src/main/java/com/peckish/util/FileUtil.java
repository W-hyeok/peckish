package com.peckish.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component // spring bean으로 등록
@Slf4j
public class FileUtil {

    @Value("${peckish.upload.path}")
    private String uploadPath;

    @PostConstruct // 의존성 주입 이후, 초기화 수행 메서드 위에 부착하는 annotation. 한 번만 수행되는 걸 보장함
    public void init() {
        // 저장할 폴더 객체 File 생성
        File tempDir = new File(uploadPath);

        // 폴더 생성 여부 조건:
        if (!tempDir.exists()) { // 실제 폴더가 존재하지 않을 경우,
            tempDir.mkdir(); // 폴더 생성.
        }
        uploadPath = tempDir.getAbsolutePath();
        log.info("uploadPath: {}", uploadPath);
    }

    // 파일 저장 처리
    public List<String> saveFiles(List<MultipartFile> files) {
        // 매개변수로 받은 List 객체(files)의 null 체크
        if(files == null || files.isEmpty()) {
            return List.of(); // 빈 List 리턴
        }
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String orgName = file.getOriginalFilename();
            String ext = orgName.substring(orgName.lastIndexOf("."));
            String saveName = UUID.randomUUID().toString() + ext; // 저장되는 파일명은 UUID 처리
            Path savePath = Paths.get(uploadPath, saveName); // 경로 생성 (자동으로 /, - 등 부여)
            log.info("save path: {}", savePath);

            // unhandled exception 발생 - try/catch로 감싸기
            try {
                Files.copy(file.getInputStream(), savePath); // 파일 저장처리

                // 썸네일 생성
                String contentType = file.getContentType();
                log.info("썸네일 생성 - contentType: {}", contentType);
                // 이미지 파일인지 확인 - 맞다면 썸네일 추가
                if(contentType != null && contentType.startsWith("image")) {
                    Path thumbPath = Paths.get(uploadPath, "th_" + saveName); // 썸네일 저장 경로 + 파일명 (이름 앞에 th_가 붙으면 썸네일임을 확인)
                    Thumbnails.of(savePath.toFile()) // 원본 파일
                            .size(200, 200) // 파일 크기
                            .toFile(thumbPath.toFile()); // 썸네일 저장
                }
                fileNames.add(saveName); // 이름 배열에 저장된 이름 추가
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileNames;
    }

    // Single 파일 저장 처리 - by Moon
    public String saveFile(MultipartFile fileImg) {

        if(fileImg == null || fileImg.isEmpty()) {
            return null;
        }
        String savedFilename = "";

        String orgName = fileImg.getOriginalFilename();
        String ext = orgName.substring(orgName.lastIndexOf("."));
        String saveName = UUID.randomUUID().toString() + ext; // 저장되는 파일명은 UUID 처리
        Path savePath = Paths.get(uploadPath, saveName); // 경로 생성 (자동으로 /, - 등 부여)
        log.info("save path: {}", savePath);

        // unhandled exception 발생 - try/catch로 감싸기
        try {
            Files.copy(fileImg.getInputStream(), savePath); // 파일 저장처리

            savedFilename = saveName; // 이름 배열에 저장된 이름 추가
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return savedFilename;
    }

    // 파일 조회(파일 정보 리턴) - 이미지 브라우저에 보여주기 위한 파일 리소스 get 메서드
    public ResponseEntity<Resource> getFile(String filename) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
        if(!resource.isReadable()) {
//            resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
            resource = new FileSystemResource(uploadPath + File.separator + "defaultProfile.png");
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 파일 삭제 처리
    public void deleteFile(List<String> filenames) {
        // 조건: 파일명 없거나 비어있으면 실행 안함
        if(filenames == null || filenames.isEmpty()) {
            return;
        }
        filenames.forEach(filename -> {
            Path filePath = Paths.get(uploadPath, filename); // (원본)파일 경로 찾기
            Path thumbPath = Paths.get(uploadPath, "th_" + filename); // 썸네일 경로 찾기
            try {
                // 파일이 존재한다면 삭제처리
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(thumbPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 이미지 파일 한개 삭제 처리  -by Moon
    public void deleteOneFile(String filename) {
        // 조건: 파일명 없거나 비어있으면 실행 안함
        if(filename == null || filename.isEmpty()) {
            return;
        }
        Path filePath = Paths.get(uploadPath, filename); // (원본)파일 경로 찾기
        try {
            // 파일이 존재한다면 삭제처리
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
