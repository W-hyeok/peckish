package com.peckish.controller.advice;

import com.peckish.util.CustomJWTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

// 예외 발생 시 Map 타입으로 에러 메시지 데이터를 전송
@RestControllerAdvice
public class CustomControllerAdvice {

    // 조회 시 번호(tno)가 없는 경우
    @ExceptionHandler(NoSuchElementException.class) // 예외처리 메서드 위에 부착
    protected ResponseEntity<?> notExist(NoSuchElementException e) {
        String msg = "해당 게시글은 없는데요?"; // 원하는 메시지로 편집 가능
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", msg));
    }

    // 매개값 타입 이상
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handlerIllegalArgumentException(MethodArgumentNotValidException e) {
        String msg = "페이지 번호가 이상한데요?";
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("msg", msg));
    }

    // JWT 예외 발생 처리
    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJwtException(CustomJWTException e) {
        String msg = e.getMessage();
        return ResponseEntity.ok().body(Map.of("error", msg));
    }
}
