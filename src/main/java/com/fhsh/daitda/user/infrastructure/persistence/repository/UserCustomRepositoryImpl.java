package com.fhsh.daitda.user.infrastructure.persistence.repository;

import com.fhsh.daitda.user.application.command.UserSearchCriteria;
import com.fhsh.daitda.user.domain.entity.QUser;
import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.repository.UserCustomRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    protected final JPAQueryFactory queryFactory;

    @Override
    public Page<User> findAll(UserSearchCriteria criteria, Pageable pageable) {
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(criteria.email())) {
            builder.and(user.email.containsIgnoreCase(criteria.email()));
        }
        if (StringUtils.hasText(criteria.name())) {
            builder.and(user.name.containsIgnoreCase(criteria.name()));
        }
        if (criteria.role() != null) {
            builder.and(user.role.eq(criteria.role()));
        }
        if (criteria.status() != null) {
            builder.and(user.status.eq(criteria.status()));
        }
        if (criteria.hubId() != null) {
            builder.and(user.hubId.eq(criteria.hubId()));
        }
        if (criteria.companyId() != null) {
            builder.and(user.companyId.eq(criteria.companyId()));
        }
        if (criteria.createdAtFrom() != null) {
            builder.and(user.createdAt.goe(criteria.createdAtFrom()));
        }
        if (criteria.createdAtTo() != null) {
            builder.and(user.createdAt.loe(criteria.createdAtTo()));
        }

        JPAQuery<User> query = queryFactory
                .selectFrom(user)
                .where(builder);

        for (Sort.Order sortOrder : pageable.getSort()) {
            PathBuilder<User> pathBuilder = new PathBuilder<>(User.class, "user");
            query.orderBy(new OrderSpecifier(
                    sortOrder.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(sortOrder.getProperty())
            ));
        }

        List<User> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
