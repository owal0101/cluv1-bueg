package com.shop.repository;

import com.shop.dto.MemberSearchDto;
import com.shop.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * 회원관리 레포지토리
 *
 * @author 구본근
 * @version 1.0
 */
@Tag(name = "회원관리 조회 레포지토리", description = "회원관리 조회")
public interface MemberRepositoryCustom {
    /**
     *
     *
     * @param memberSearchDto 회원 검색 후 담을 dto
     * @param pageable 페이지 기능
     *
     *
     */
    @Operation(summary = "회원관리 조회 메소드", description = "회원관리 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원관리 조회"),
            @ApiResponse(responseCode = "400", description = "회원관리 조회 실패")})
    Page<Member> getAdminMemberPage(@Parameter(description = "회원 검색 dto")MemberSearchDto memberSearchDto, @Parameter(description = "페이지 기능")Pageable pageable);

}
