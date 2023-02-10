package com.jaritalk.backend.service;

import com.jaritalk.backend.dto.PostDto;
import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.PostLike;
import com.jaritalk.backend.entity.User;
import com.jaritalk.backend.exception.CustomException;
import com.jaritalk.backend.exception.ErrorCode;
import com.jaritalk.backend.repository.PostLikeRepository;
import com.jaritalk.backend.repository.PostRepository;
import com.jaritalk.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostLikeRepository postLikeRepository;

    private final UserRepository userRepository;

    @Transactional
    public Long InsertPost(HttpServletRequest request, PostDto.InsertPostReq requestDto) {
        Long userId = checkLogin(request, true);

        checkTitle(requestDto.getTitle());
        checkContent(requestDto.getContent());

        User findUser = checkUser(userId);

        Post post = Post.builder()
                .user(findUser)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .deleteYn(false)
                .build();

        Post savePost = postRepository.save(post);


        return savePost.getPostId();
    }

    public PostDto.PostDetailRes getPostDetail(Long postId) {
        Post findPost = checkPost(postId);

        return new PostDto.PostDetailRes(findPost);
    }

    public Page<PostDto.PostListRes> getPostList(HttpServletRequest request, Pageable pageable) {
        Long userId = checkLogin(request, false);

        List<Post> postList = postRepository.findFetchPostList(pageable, false);

        List<PostDto.PostListRes> result = postList.stream().map(p -> {
            PostDto.PostListRes temp = new PostDto.PostListRes(p);
            if (userId != null) {
                Optional<PostLike> postLike = p.getPostLikeList().stream().filter(pl -> pl.getUser().getUserId() == userId).findAny();
                if (postLike.isPresent()) {
                    temp.setLikeYn(true);
                }
            }
            return temp;
        }).collect(Collectors.toList());

        Long count = postRepository.countFetchPostList(false);

        return new PageImpl<>(result, pageable, count);
    }

    @Transactional
    public Long updatePost(HttpServletRequest request, Long postId, PostDto.UpdatePostReq requestDto) {
        Long userId = checkLogin(request, true);

        checkTitle(requestDto.getTitle());
        checkContent(requestDto.getContent());

        User findUser = checkUser(userId);

        Post findPost = checkPost(postId);

        if (findPost.getUser() != findUser) {
            throw new CustomException(ErrorCode.NOT_MATCHED_USER_AND_POST);
        }

        if (!findPost.getTitle().equals(requestDto.getTitle())) {
            findPost.updateTitle(requestDto.getTitle());
        }

        if (!findPost.getContent().equals(requestDto.getContent())) {
            findPost.updateContent(requestDto.getContent());
        }

        return findPost.getPostId();
    }

    @Transactional
    public void deletePost(HttpServletRequest request, Long postId) {
        Long userId = checkLogin(request, true);

        User findUser = checkUser(userId);

        Post findPost = checkPost(postId);

        if (findPost.getUser() != findUser) {
            throw new CustomException(ErrorCode.NOT_MATCHED_USER_AND_POST);
        }

        findPost.deletePost();
    }

    @Transactional
    public void postLike(HttpServletRequest request, Long postId, Boolean likeYn) {
        Long userId = checkLogin(request, true);

        User findUser = checkUser(userId);

        Post findPost = checkPost(postId);

        if (likeYn == null) {
            throw new CustomException(ErrorCode.REQUIRED_LIKE_YN);
        }

        Optional<PostLike> findPostLike = postLikeRepository.findByPostAndUser(findPost, findUser);

        if (likeYn) {
            if (!findPostLike.isPresent()) {
                findPost.updatePostLikeList(new PostLike(findPost, findUser));
            }
        } else {
            if (findPostLike.isPresent()) {
                postLikeRepository.delete(findPostLike.get());
            }
        }
    }

    private Long checkLogin(HttpServletRequest request, Boolean requiredLoginYn) {
        Long userId = null;

        String authentication = request.getHeader("Authentication");
        if (authentication != null) {
            String[] split = authentication.split(" ");
            userId = Long.parseLong(split[1]);
        }

        if (requiredLoginYn) {
            if (userId == null) {
                throw new CustomException(ErrorCode.REQUIRED_LOGIN);
            }
        }

        return userId;
    }

    private User checkUser(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new CustomException(ErrorCode.NON_EXISTENT_USER);
        }
        if (findUser.get().getQuit()) {
            throw new CustomException(ErrorCode.QUIT_USER);
        }
        return findUser.get();
    }

    private Post checkPost(Long postId) {
        Optional<Post> findPost = postRepository.findFetchPostById(postId);
        if (!findPost.isPresent()) {
            throw new CustomException(ErrorCode.NON_EXISTENT_POST);
        }
        if (findPost.get().getDeleteYn()) {
            throw new CustomException(ErrorCode.DELETED_POST);
        }
        return findPost.get();
    }

    private void checkTitle(String title) {
        if (title == null || title.equals("")) {
            throw new CustomException(ErrorCode.REQUIRED_TITLE);
        }
        if (title.length() < 2 || title.length() > 20) {
            throw new CustomException(ErrorCode.CHECK_TITLE_LENGTH);
        }
    }

    private void checkContent(String content) {
        if (content == null || content.equals("")) {
            throw new CustomException(ErrorCode.REQUIRED_CONTENT);
        }
        if (content.length() < 1 || content.length() > 100) {
            throw new CustomException(ErrorCode.CHECK_CONTENT_LENGTH);
        }
    }

}
