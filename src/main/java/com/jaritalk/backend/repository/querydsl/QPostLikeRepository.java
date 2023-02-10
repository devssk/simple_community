package com.jaritalk.backend.repository.querydsl;

import com.jaritalk.backend.entity.PostLike;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QPostLikeRepository {
    List<PostLike> findFetchPostLikeAndUser(Pageable pageable, Long userId);
    Long countPostLikeAndUser(Long userId);
}
