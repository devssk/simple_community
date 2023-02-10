package com.jaritalk.backend.service;

import com.jaritalk.backend.dto.PostDto;
import com.jaritalk.backend.entity.AccountType;
import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.PostLike;
import com.jaritalk.backend.entity.User;
import com.jaritalk.backend.exception.CustomException;
import com.jaritalk.backend.repository.PostLikeRepository;
import com.jaritalk.backend.repository.PostRepository;
import com.jaritalk.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @InjectMocks
    PostService postService;

    @Nested
    @DisplayName("글 작성")
    class InsertPostTest {

        @Test
        @DisplayName("글 작성 테스트 - 비로그인 사용자")
        void insertPostTest01() {
            // given
            MockHttpServletRequest request = createMockHttpServletRequest(null);

            PostDto.InsertPostReq requestDto = createInsertPostReq("제목", "내용");

            // when
            Throwable exception = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto);
            });

            // then
            assertEquals("로그인이 필요한 서비스입니다.", exception.getMessage());

        }

        @Test
        @DisplayName("글 작성 테스트 - 글 유효성 확인")
        void insertPostTest02() {
            // given
            MockHttpServletRequest request = createMockHttpServletRequest("Realtor 1");

            PostDto.InsertPostReq requestDto1 = createInsertPostReq(null, null);
            PostDto.InsertPostReq requestDto2 = createInsertPostReq("", "");
            PostDto.InsertPostReq requestDto3 = createInsertPostReq("하나", null);
            PostDto.InsertPostReq requestDto4 = createInsertPostReq("하나", "");
            PostDto.InsertPostReq requestDto5 = createInsertPostReq("일", "일");
            PostDto.InsertPostReq requestDto6 = createInsertPostReq("일이삼사오륙칠팔구십십일십이십삼십사십오십", "하나");
            PostDto.InsertPostReq requestDto7 = createInsertPostReq("하나", "하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나" +
                    "하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하나하");

            // when
            Throwable exception1 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto1);
            });
            Throwable exception2 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto2);
            });
            Throwable exception3 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto3);
            });
            Throwable exception4 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto4);
            });
            Throwable exception5 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto5);
            });
            Throwable exception6 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto6);
            });
            Throwable exception7 = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto7);
            });

            // then
            assertEquals("글의 제목을 입력해주세요.", exception1.getMessage());
            assertEquals("글의 제목을 입력해주세요.", exception2.getMessage());
            assertEquals("글의 내용을 입력해주세요.", exception3.getMessage());
            assertEquals("글의 내용을 입력해주세요.", exception4.getMessage());
            assertEquals("글의 제목은 2글자 이상, 20글자 이하이어야 합니다.", exception5.getMessage());
            assertEquals("글의 제목은 2글자 이상, 20글자 이하이어야 합니다.", exception6.getMessage());
            assertEquals("글의 내용은 1글자 이상, 100글자 이하이어야 합니다.", exception7.getMessage());

        }

        @Test
        @DisplayName("글 작성 테스트 - 존재하지 않는 유저")
        void insertPostTest03() {
            // given
            MockHttpServletRequest request = createMockHttpServletRequest("Realtor 1");
            PostDto.InsertPostReq requestDto = createInsertPostReq("제목", "내용");

            Mockito.doReturn(Optional.empty()).when(userRepository).findById(any(Long.class));

            // when
            Throwable exception = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto);
            });

            // then
            assertEquals("존재하지 않는 유저입니다.", exception.getMessage());

        }

        @Test
        @DisplayName("글 작성 테스트 - 탈퇴한 유저")
        void insertPostTest04() {
            // given
            MockHttpServletRequest request = createMockHttpServletRequest("Realtor 1");
            PostDto.InsertPostReq requestDto = createInsertPostReq("제목", "내용");

            User findUser = createUser(1L, "test", AccountType.REALTOR, "test", true);

            Mockito.doReturn(Optional.of(findUser)).when(userRepository).findById(any(Long.class));

            // when
            Throwable exception = assertThrows(CustomException.class, () -> {
                postService.InsertPost(request, requestDto);
            });

            // then
            assertEquals("탈퇴한 유저입니다.", exception.getMessage());

        }

        @Test
        @DisplayName("글 작성 테스트 - 정상")
        void insertPostTest05() {
            // given
            MockHttpServletRequest request = createMockHttpServletRequest("Realtor 1");

            PostDto.InsertPostReq requestDto = createInsertPostReq("제목", "내용");

            User findUser = createUser(1L, "test", AccountType.REALTOR, "test", false);

            Mockito.doReturn(Optional.of(findUser)).when(userRepository).findById(any(Long.class));

            Post savePost = createPost(1L, findUser, requestDto.getTitle(), requestDto.getContent(), false, null);

            Mockito.doReturn(savePost).when(postRepository).save(any(Post.class));

            // when
            Long result = postService.InsertPost(request, requestDto);

            // then
            assertEquals(result, 1L);

        }

    }

    private MockHttpServletRequest createMockHttpServletRequest(String Authentication) {
        MockHttpServletRequest result = new MockHttpServletRequest();
        if (Authentication != null) {
            result.addHeader("Authentication", Authentication);
        }
        return result;
    }

    private PostDto.InsertPostReq createInsertPostReq(String title, String content) {
        PostDto.InsertPostReq result = new PostDto.InsertPostReq();
        result.setTitle(title);
        result.setContent(content);
        return result;
    }

    private PostDto.UpdatePostReq createUpdatePostReq(String title, String content) {
        PostDto.UpdatePostReq result = new PostDto.UpdatePostReq();
        result.setTitle(title);
        result.setContent(content);
        return result;
    }

    private PostDto.PostDetailRes createPostDetailRes(Long postId, Long userId, String nickname, String accountType,
                                                      String title, String content, LocalDateTime createdDate,
                                                      LocalDateTime modifiedDate, LocalDateTime deleteDate) {
        PostDto.PostDetailRes result = new PostDto.PostDetailRes();
        result.setPostId(postId);
        result.setUserId(userId);
        result.setNickname(nickname);
        result.setAccountType(accountType);
        result.setTitle(title);
        result.setContent(content);
        result.setCreatedDate(createdDate);
        result.setModifiedDate(modifiedDate);
        result.setDeleteDate(deleteDate);
        return result;
    }

    private PostDto.PostListRes createPostListRes(Long postId, Long userId, Boolean likeYn, String nickname,
                                                  String accountType, String title, Integer likeCount,
                                                  LocalDateTime createdDate, LocalDateTime modifiedDate) {
        PostDto.PostListRes result = new PostDto.PostListRes();
        result.setPostId(postId);
        result.setUserId(userId);
        result.setLikeYn(likeYn);
        result.setNickname(nickname);
        result.setAccountType(accountType);
        result.setTitle(title);
        result.setLikeCount(likeCount);
        result.setCreatedDate(createdDate);
        result.setModifiedDate(modifiedDate);
        return result;
    }

    private User createUser(Long userId, String nickname, AccountType accountType, String accountId, Boolean quit) {
        return User.builder()
                .userId(userId)
                .nickname(nickname)
                .accountType(accountType)
                .accountId(accountId)
                .quit(quit)
                .build();
    }

    private Post createPost(Long postId, User user, String title, String content, Boolean deleteYn, LocalDateTime deleteDate) {
        return Post.builder()
                .postId(postId)
                .user(user)
                .title(title)
                .content(content)
                .deleteYn(deleteYn)
                .deleteDate(deleteDate)
                .build();
    }

    private PostLike createPostLike(Post post, User user) {
        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }

}