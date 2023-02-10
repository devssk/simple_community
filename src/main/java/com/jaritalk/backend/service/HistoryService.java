package com.jaritalk.backend.service;

import com.jaritalk.backend.dto.HistoryDto;
import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.PostLike;
import com.jaritalk.backend.repository.PostLikeRepository;
import com.jaritalk.backend.repository.PostRepository;
import com.jaritalk.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

    private final PostLikeRepository postLikeRepository;

    private final PostRepository postRepository;

    public Page<HistoryDto.PostLikeHistoryRes> getPostLikeHistoryList(Pageable pageable, Long userId) {
        List<PostLike> postLikeList = postLikeRepository.findFetchPostLikeAndUser(pageable, userId);

        List<HistoryDto.PostLikeHistoryRes> result = postLikeList.stream().map(HistoryDto.PostLikeHistoryRes::new).collect(Collectors.toList());

        Long count = postLikeRepository.countPostLikeAndUser(userId);

        return new PageImpl<>(result, pageable, count);
    }

    public Page<HistoryDto.PostHistoryRes> getPostHistoryList(Pageable pageable) {
        List<Post> postList = postRepository.findFetchPostList(pageable, null);

        List<HistoryDto.PostHistoryRes> result = postList.stream().map(HistoryDto.PostHistoryRes::new).collect(Collectors.toList());

        Long count = postRepository.countFetchPostList(null);

        return new PageImpl<>(result, pageable, count);
    }

}
