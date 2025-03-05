package com.peckish.service;

import com.peckish.domain.Product;
import com.peckish.domain.ProductImage;
import com.peckish.dto.PageRequestDTO;
import com.peckish.dto.PageResponseDTO;
import com.peckish.dto.ProductDTO;
import com.peckish.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Long regist(ProductDTO productDTO) {
        // DTO를 Entity로 변환 후 저장
        Product product = dtoToEntity(productDTO);
        Product savedProduct = productRepository.save(product); // 작성 정보 DB에 저장
        return savedProduct.getPno(); // 저장된 글 고유번호 반환
    }

    @Override
    public PageResponseDTO<ProductDTO> getProductList(PageRequestDTO requestDTO) {
        // 조회할 페이지, 사이즈 정보 설정. - pageable 생성
        // 페이지번호가 0부터 시작하므로 1을 뻄
        Pageable pageable = PageRequest.of(requestDTO.getPage()-1,
                requestDTO.getSize(),
                Sort.by("pno").descending());

        // 상품 목록 조회
        // 여러 Entity를 join하여 한꺼번에 조회하고 반환하기 위해 Object[] 사용.
        // 해당 배열에는 Product, ProductImage - 두 Entity가 담김. 하나는 product로, 다른 하나는 productImage로 쓸 거임.
        Page<Object[]> result = productRepository.selectList(pageable);

        // 조회 결과를 ProductDTO로 변환, 리스트에 담음
        List<ProductDTO> dtoList = result.get().map(arr -> {
            Product product = (Product)arr[0]; // 첫 번째 배열 요소: Product Entity
            ProductImage productImage = (ProductImage)arr[1]; // 두 번째 배열 요소: ProductImage Entity

            // Product → ProductDTO로 변환
            ProductDTO productDTO = ProductDTO.builder() // 빌더 패턴 시작(빌더 객체 생성 시작)
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build(); // 빌터 패턴 종료: ...설정값 바탕으로 ProductDTO 객체 생성(완성)

            // 첫 번째 이미지 파일명 꺼내기
            String imgFilename = productImage.getFilename();
            productDTO.setUploadFileNames(List.of(imgFilename)); // ProductDTO에 이미지 파일명 설정

            return productDTO;
        }).collect(Collectors.toList()); // 결과를 List로 반환

        // 제품 전체 수 계산
        long totalCount = result.getTotalElements();

        // PageResponseDTO 객체 생성하여 리턴
        return PageResponseDTO.<ProductDTO>all()
                .list(dtoList) // 변환된 DTO 리스트
                .totalCount(totalCount) // 전체 제품 수
                .pageRequestDTO(requestDTO) // 요청한 페이지 정보
                .build();
    }

    @Override
    public ProductDTO getProductByPno(Long pno) {
        // pno로 글 1개 조회 (없으면 예외 발생)
        Product product = productRepository.selectOne(pno).orElseThrow();

        // 조회된 Product Entity를 DTO로 변환
        ProductDTO productDTO = entityToDto(product);
        return productDTO;
    }

    @Override
    public void modify(ProductDTO productDTO) {
        // pno로 기존 글 조회 (없으면 예외 발생)
        Product findProduct = productRepository.findById(productDTO.getPno()).orElseThrow();

        // 수정한 내용으로 기존 정보 업데이트
        findProduct.changePname(productDTO.getPname());
        findProduct.changeDesc(productDTO.getPdesc());
        findProduct.changePrice(productDTO.getPrice());
        findProduct.clearImageList(); // 기존 이미지 리스트 제거 (초기화)

        // 새로운 이미지 파일명 목록이 있으면 새 이미지 추가
        List<String> uploadFileNames = productDTO.getUploadFileNames(); // 최종 저장될 이미지 파일명들
        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.stream().forEach(name -> {
                findProduct.addImageByFilename(name); // 파일명으로 이미지 추가
            });
        }
        // 수정된 정보 DB에 저장
        productRepository.save(findProduct);
    }

    @Override
    public void remove(Long pno) {
        // Todo: 이미지 테이블의 레코드도 삭제

        // delFlag를 true로 변경하여 삭제 처리
        productRepository.updateToDelete(pno, true);
    }

    // Product(Entity) → ProductDTO 변환 메서드
    private ProductDTO entityToDto(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .build();

        // 이미지 없으면 바로 리턴
        List<ProductImage> imageList = product.getImageList();
        if(imageList == null || imageList.isEmpty()) {
            return productDTO;
        }
        // 이미지가 있으면 이미지 파일명 리스트를 DTO에 추가
        List<String> filenameList = imageList.stream()
                .map(img -> img.getFilename()).collect(Collectors.toList());
        productDTO.setUploadFileNames(filenameList);
        return productDTO;
    }

    // ProductDTO → Product(Entity) 변환 메서드
    private Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .build();

        // DTO에 포함된 이미지 파일명 목록을 통해 ProductImage(Entity) 추가
        List<String> uploadFileNames = productDTO.getUploadFileNames();
        if(uploadFileNames == null) {
            return product;
        }
        uploadFileNames.stream().forEach(name -> {
            product.addImageByFilename(name);
        });
        return product;
    }
}
