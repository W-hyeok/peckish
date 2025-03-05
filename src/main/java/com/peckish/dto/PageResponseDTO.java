package com.peckish.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
// generic class로 설정 (Xxx<E>)
public class PageResponseDTO<E> {
    private List<E> list;               // content 목록
    private List<Integer> pageNumList;  // 화면에서 뿌려줄 페이지 번호들
    private PageRequestDTO pageRequestDTO;   // 페이지 요청정보: page, size
    private boolean prev, next;              // 다음/이전 페이지 여부
    private long totalCount;                 // 전체 content 개수
    private int prevPage, nextPage, totalPage, currentPage;
    private final int PAGE_NUM_UNIT = 10;    // 한 페이지에 보여줄 페이지 번호 개수

    @Builder(builderMethodName = "all")
    public PageResponseDTO(List<E> list, PageRequestDTO pageRequestDTO, long totalCount) {
        this.list = list;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = totalCount;

        int end = (int)(Math.ceil(pageRequestDTO.getPage()/(double)PAGE_NUM_UNIT)) * PAGE_NUM_UNIT; // 화면상 시작 번호
        int start = end - (PAGE_NUM_UNIT -1); // 화면상 마지막 페이지 번호
        int last = (int)(Math.ceil(totalCount/(double)pageRequestDTO.getSize())); // 전체의 마지막 페이지 번호
        end = end > last ? last : end;

        this.prev = start >1;
        this.next = totalCount > end * pageRequestDTO.getSize();

        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
        if(prev) {
            this.prevPage = start-1;
        }
        if(next) {
            this.nextPage = end+1;
        }
        this.totalPage = pageNumList.size(); // 현재 보고 있는 페이지 번호의 개수
        this.currentPage = pageRequestDTO.getPage();

        System.out.println("pageRequestDTO = " + pageRequestDTO);
        System.out.println("totalCount = " + totalCount);
        System.out.println("prevPage = " + prevPage);
        System.out.println("nextPage = " + nextPage);
        System.out.println("totalPage = " + totalPage);
        System.out.println("currentPage = " + currentPage);
        System.out.println("start = " + start);
        System.out.println("end = " + end);
        System.out.println("last = " + last);
        System.out.println("list = " + list);
    }
}
