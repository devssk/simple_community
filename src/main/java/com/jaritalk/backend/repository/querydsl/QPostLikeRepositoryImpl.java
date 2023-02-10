package com.jaritalk.backend.repository.querydsl;

import com.jaritalk.backend.entity.PostLike;
import com.jaritalk.backend.entity.QPost;
import com.jaritalk.backend.entity.QPostLike;
import com.jaritalk.backend.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jaritalk.backend.entity.QPost.*;
import static com.jaritalk.backend.entity.QPostLike.*;
import static com.jaritalk.backend.entity.QUser.*;

@Repository
public class QPostLikeRepositoryImpl implements QPostLikeRepository{

    private final EntityManager em;

    private final JPAQueryFactory query;

    public QPostLikeRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<PostLike> findFetchPostLikeAndUser(Pageable pageable, Long userId) {
        List<PostLike> result = query
                .select(postLike)
                .from(postLike)
                .leftJoin(postLike.post, post).fetchJoin()
                .leftJoin(postLike.user, user).fetchJoin()
                .where(eqUserId(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return result;
    }

    @Override
    public Long countPostLikeAndUser(Long userId) {
        Long result = query
                .select(postLike.count())
                .from(postLike)
                .where(eqUserId(userId))
                .fetchOne();
        return result;
    }

    private BooleanExpression eqUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return postLike.user.userId.eq(userId);
    }
}
