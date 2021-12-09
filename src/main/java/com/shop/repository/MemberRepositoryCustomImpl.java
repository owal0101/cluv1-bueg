package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.dto.MemberSearchDto;
import com.shop.entity.Member;
import com.shop.entity.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
/**
 * 회원관리 상속 레포지토리
 *
 * @author 구본근
 * @version 1.0
 */
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    /**
     * 회원 조회 메소드
     *
     * @param searchBy 검색될 항목
     * @param searchQuery 검색 쿼리
     *
     */
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("name", searchBy)) {
            return QMember.member.name.like("%"+ searchQuery+"%");
        } else if(StringUtils.equals("email", searchBy)) {
            return QMember.member.email.like("%"+ searchQuery+"%");
        }

        return null;
    }
    /**
     * 회원 조회 메소드
     *
     * @param memberSearchDto 쿼리 실행 후 받아올 dto
     * @param pageable 페이지 기능
     *
     * @return 쿼리 실행 후 목록 형식의 결과, 페이지 기능, 총 결과 수
     */
    @Override
    public Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable) {
        QueryResults<Member> results = queryFactory
                .selectFrom(QMember.member)
                .where(searchByLike(memberSearchDto.getSearchBy(),memberSearchDto.getSearchQuery()))
                .orderBy(QMember.member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Member> content = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

}
