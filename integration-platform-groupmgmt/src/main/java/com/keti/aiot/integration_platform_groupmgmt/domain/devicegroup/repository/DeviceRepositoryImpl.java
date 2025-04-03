package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto.DeviceSearchCondition;
import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.Device;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Device> searchByConditions(DeviceSearchCondition condition, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Device> cq = cb.createQuery(Device.class);
        Root<Device> root = cq.from(Device.class);

        List<Predicate> predicates = buildPredicates(condition, cb, root);
        cq.where(predicates.toArray(new Predicate[0]));

        // 페이징 쿼리 실행
        TypedQuery<Device> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Device> content = query.getResultList();

        // 총 개수 쿼리
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Device> countRoot = countQuery.from(Device.class);
        List<Predicate> countPredicates = buildPredicates(condition, cb, countRoot);

        countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(DeviceSearchCondition condition, CriteriaBuilder cb, Root<Device> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(condition.getDevId())) {
            predicates.add(cb.like(cb.lower(root.get("devId")), "%" + condition.getDevId().toLowerCase() + "%"));
        }

        if (StringUtils.hasText(condition.getDevEui())) {
            predicates.add(cb.like(cb.lower(root.get("devEui")), "%" + condition.getDevEui().toLowerCase() + "%"));
        }

        if (StringUtils.hasText(condition.getNcId())) {
            predicates.add(cb.like(cb.lower(root.get("ncId")), "%" + condition.getNcId().toLowerCase() + "%"));
        }

        if (StringUtils.hasText(condition.getDescription())) {
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + condition.getDescription().toLowerCase() + "%"));
        }

        if (condition.getCreatedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), condition.getCreatedAtFrom()));
        }

        if (condition.getCreatedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), condition.getCreatedAtTo()));
        }

        if (condition.getModifiedAtFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("modifiedAt"), condition.getModifiedAtFrom()));
        }

        if (condition.getModifiedAtTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("modifiedAt"), condition.getModifiedAtTo()));
        }

        if (condition.getLastSeenFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("lastSeen"), condition.getLastSeenFrom()));
        }

        if (condition.getLastSeenTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("lastSeen"), condition.getLastSeenTo()));
        }

        return predicates;
    }
}
