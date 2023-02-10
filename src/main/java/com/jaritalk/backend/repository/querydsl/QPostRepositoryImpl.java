package com.jaritalk.backend.repository.querydsl;

import com.jaritalk.backend.entity.Post;
import com.jaritalk.backend.entity.QPost;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.jaritalk.backend.entity.QPost.*;
import static com.jaritalk.backend.entity.QUser.*;

@Repository
public class QPostRepositoryImpl implements QPostRepository{

    private final EntityManager em;

    private final JPAQueryFactory query;

    public QPostRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Post> findFetchPostById(Long postId) {
        Post result = query
                .select(QPost.post)
                .from(QPost.post)
                .leftJoin(QPost.post.user, user).fetchJoin()
                .where(QPost.post.postId.eq(postId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<Post> findFetchPostList(Pageable pageable, Boolean deleteYn) {
        List<Post> result = query
                .select(post)
                .from(post)
                .leftJoin(post.user, user).fetchJoin()
                .where(eqDeleteYn(deleteYn))
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return result;
    }

    @Override
    public Long countFetchPostList(Boolean deleteYn) {
        Long result = query
                .select(post.count())
                .from(post)
                .where(eqDeleteYn(deleteYn))
                .fetchOne();
        return result;
    }

    private BooleanExpression eqDeleteYn(Boolean deleteYn) {
        if (deleteYn == null) {
            return null;
        }
        if (deleteYn) {
            return post.deleteYn.eq(true);
        } else {
            return post.deleteYn.eq(false);
        }
    }
}
