package com.jaritalk.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 (BadRequest)
    NON_EXISTENT_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    NON_EXISTENT_POST(HttpStatus.BAD_REQUEST, "존재하지 않는 글입니다."),
    DELETED_POST(HttpStatus.BAD_REQUEST, "삭제된 글입니다."),

    REQUIRED_TITLE(HttpStatus.BAD_REQUEST, "글의 제목을 입력해주세요."),
    REQUIRED_CONTENT(HttpStatus.BAD_REQUEST, "글의 내용을 입력해주세요."),
    REQUIRED_LIKE_YN(HttpStatus.BAD_REQUEST, "좋아요 여부가 입력되지 않았습니다."),

    CHECK_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "글의 제목은 2글자 이상, 20글자 이하이어야 합니다."),
    CHECK_CONTENT_LENGTH(HttpStatus.BAD_REQUEST, "글의 내용은 1글자 이상, 100글자 이하이어야 합니다."),

    // 401 (Unauthorized)
    REQUIRED_LOGIN(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
    QUIT_USER(HttpStatus.UNAUTHORIZED, "탈퇴한 유저입니다."),
    NOT_MATCHED_USER_AND_POST(HttpStatus.UNAUTHORIZED, "해당 글을 작성한 유저가 아닙니다.");

    // 403 (Forbidden)

    // 404 (NotFound)

    // 500 (Internal server error)

    private final HttpStatus httpStatus;
    private final String message;
}
