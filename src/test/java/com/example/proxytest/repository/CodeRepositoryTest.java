package com.example.proxytest.repository;

import com.example.proxytest.domain.City;
import com.example.proxytest.domain.City2;
import com.example.proxytest.domain.Code;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
class CodeRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    CodeRepository codeRepository;

    @Autowired
    CityRepository cityRepository;

    Code me, parentCode, grandParentCode;

    @BeforeEach
    void setUp() {
        this.grandParentCode = Code.builder()
                .label("grandParentCode")
                .codeId("codeId_01")
                .build();
        this.parentCode = Code.builder()
                .label("parentCode")
                .codeId("codeId_02")
                .build();
        this.me = Code.builder()
                .label("me")
                .codeId("codeId_03")
                .build();
    }

    @Test
    @DisplayName("연관관계가 PrimaryKey가 아니라면 Proxy 참조 저장할때 null을 반환 (하이버네티으 6.5.3에서 해결)")
    void 연관관계가_primary_key가_아니라면_proxy_참조_저장할때_null을_반환() {

        // given
        em.persist(grandParentCode);

        parentCode.changeParent(grandParentCode);
        em.persist(parentCode);

        me.changeParent(parentCode);
        em.persist(me);

        em.flush();
        em.clear();

        // when
        Optional<Code> findCode = codeRepository.findById(me.getKey());

        // then
        City city = City.builder()
                .label("city")
                .code(findCode.get().getParentCode().getParentCode())
                .build();

        em.persist(city);
    }

    @Test
    @DisplayName("연관관계가 PrimaryKey라면 Proxy 참조일때 키 값을 반환")
    void 연관관계가_PrimaryKey라면_Proxy_참조일때_키_값을_반환() {

        // given
        em.persist(grandParentCode);

        parentCode.changeParent(grandParentCode);
        em.persist(parentCode);

        me.changeParent(parentCode);
        em.persist(me);

        em.flush();
        em.clear();


        // when
        Optional<Code> findCode = codeRepository.findById(me.getKey());

        // then
        City2 city = City2.builder()
                .label("city")
                .code(findCode.get().getParentCode().getParentCode())
                .build();

        em.persist(city);

        City2 findCity = em.find(City2.class, city.getId());

        assertThat(city).isEqualTo(findCity).isSameAs(findCity);
    }

}