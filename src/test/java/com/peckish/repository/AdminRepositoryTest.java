package com.peckish.repository;

import com.peckish.domain.Member;
import com.peckish.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    public void test() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("email").descending());
        Page<Member> memberUserWithPaging = adminRepository.getMemberUserWithPaging(Role.OWNER, pageable);
        memberUserWithPaging.getContent().forEach(member -> {
            System.out.println(member);
            System.out.println("member.getRoleList() = " + member.getRoleList());
        });
        System.out.println(memberUserWithPaging.getTotalElements());
        System.out.println(memberUserWithPaging.getTotalPages());

    }

}