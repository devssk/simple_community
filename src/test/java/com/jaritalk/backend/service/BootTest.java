package com.jaritalk.backend.service;

import com.jaritalk.backend.dto.HistoryDto;
import com.jaritalk.backend.dto.PostDto;
import com.jaritalk.backend.entity.AccountType;
import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.PostLike;
import com.jaritalk.backend.entity.User;
import com.jaritalk.backend.exception.CustomException;
import com.jaritalk.backend.repository.PostLikeRepository;
import com.jaritalk.backend.repository.PostRepository;
import com.jaritalk.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Rollback(value = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BootTest {

    @Autowired
    PostService postService;

    @Autowired
    HistoryService historyService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @BeforeAll
    void init() {
        User user1 = createUser(null, "test1", AccountType.REALTOR, "test1", false);
        User user2 = createUser(null, "test2", AccountType.LESSEE, "test2", false);
        User user3 = createUser(null, "test3", AccountType.LESSOR, "test3", false);
        Post post1 = createPost(null, user1, "제목", "내용", false, null);
        Post post2 = createPost(null, user1, "제목입니다.", "내용", false, null);
        Post post3 = createPost(null, user2, "제목~", "내용입니다", false, null);
        Post post4 = createPost(null, user2, "제목", "내용~", false, null);
        Post post5 = createPost(null, user3, "제목123", "12345", false, null);
        Post post6 = createPost(null, user3, "1413413", "5555555", false, null);
        Post post7 = createPost(null, user1, "제목2", "내용2", true, LocalDateTime.now().plusSeconds(1));
        Post post8 = createPost(null, user2, "제목3", "내용3", false, null);
        Post post9 = createPost(null, user3, "제목4", "내용4", false, null);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);
        postRepository.save(post6);
        postRepository.save(post7);
        postRepository.save(post8);
        postRepository.save(post9);

        PostLike postLike1 = createPostLike(post1, user1);
        PostLike postLike2 = createPostLike(post1, user2);
        PostLike postLike3 = createPostLike(post5, user3);
        PostLike postLike4 = createPostLike(post5, user1);
        PostLike postLike5 = createPostLike(post9, user1);

        postLikeRepository.save(postLike1);
        postLikeRepository.save(postLike2);
        postLikeRepository.save(postLike3);
        postLikeRepository.save(postLike4);
        postLikeRepository.save(postLike5);
    }

    @Test
    @DisplayName("글 상세 불러오기")
    void getPostDetailTest01() {
        // given
        Long postId = 1L;

        // when
        PostDto.PostDetailRes result = postService.getPostDetail(postId);

        // then
        assertEquals("공인중개사", result.getAccountType());
    }

    @Test
    @DisplayName("글 상세 불러오기 - 삭제된 글")
    void getPostDetailTest02() {
        // given
        Long postId = 7L;

        // when
        Throwable exception = assertThrows(CustomException.class, () -> {
            postService.getPostDetail(postId);
        });

        // then
        assertEquals("삭제된 글입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("글 목록 불러오기 - 비로그인")
    void getPostListTest01() {
        // given
        MockHttpServletRequest request = createMockHttpServletRequest(null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<PostDto.PostListRes> postList = postService.getPostList(request, pageable);

        // then
        assertEquals(8, postList.getContent().size());
        assertEquals(2, postList.getContent().get(7).getLikeCount());
        assertEquals(2, postList.getContent().get(3).getLikeCount());
        assertEquals(1, postList.getContent().get(0).getLikeCount());
    }

    @Test
    @DisplayName("글 목록 불러오기 - 로그인")
    void getPostListTest02() {
        // given
        MockHttpServletRequest request = createMockHttpServletRequest("Realtor 1");
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<PostDto.PostListRes> postList = postService.getPostList(request, pageable);

        // then
        assertEquals(8, postList.getContent().size());
        assertEquals(true, postList.getContent().get(0).getLikeYn());
        assertEquals(false, postList.getContent().get(1).getLikeYn());
        assertEquals(false, postList.getContent().get(2).getLikeYn());
        assertEquals(true, postList.getContent().get(3).getLikeYn());
        assertEquals(false, postList.getContent().get(4).getLikeYn());
        assertEquals(false, postList.getContent().get(5).getLikeYn());
        assertEquals(false, postList.getContent().get(6).getLikeYn());
        assertEquals(true, postList.getContent().get(7).getLikeYn());
    }

    @Test
    @DisplayName("글 좋아요 - 비로그인")
    void postLikeTest01() {
        // given
        MockHttpServletRequest request = createMockHttpServletRequest(null);

        // when
        Throwable exception = assertThrows(CustomException.class, () -> {
            postService.postLike(request, 2L, true);
        });

        // then
        assertEquals("로그인이 필요한 서비스입니다.", exception.getMessage());

    }

    @Test
    @Rollback
    @DisplayName("글 좋아요 - 로그인")
    void postLikeTest02() {
        // given
        MockHttpServletRequest request = createMockHttpServletRequest("Realtor 1");

        // when
        postService.postLike(request, 2L, true);

        // then

    }

    @Test
    @DisplayName("좋아요 히스토리")
    void getPostLikeHistoryTest01() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<HistoryDto.PostLikeHistoryRes> result = historyService.getPostLikeHistoryList(pageable, null);

        // then
        assertEquals(5, result.getContent().size());
    }

    @Test
    @DisplayName("좋아요 히스토리 - 아이디 지정")
    void getPostLikeHistoryTest02() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<HistoryDto.PostLikeHistoryRes> result = historyService.getPostLikeHistoryList(pageable, userId);

        // then
        assertEquals(3, result.getContent().size());
    }


    @Test
    @DisplayName("글 시간 히스토리")
    void getPostHistoryTest() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<HistoryDto.PostHistoryRes> result = historyService.getPostHistoryList(pageable);

        // then
        assertEquals(9, result.getContent().size());

    }

    private MockHttpServletRequest createMockHttpServletRequest(String Authentication) {
        MockHttpServletRequest result = new MockHttpServletRequest();
        if (Authentication != null) {
            result.addHeader("Authentication", Authentication);
        }
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