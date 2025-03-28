package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.repository;

import com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.entity.DeviceGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceGroupRepositoryImpl implements DeviceGroupRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<DeviceGroup> findActiveGroups() {
        String jpql = "SELECT g FROM DeviceGroup g WHERE g.status = :status";
        return em.createQuery(jpql, DeviceGroup.class)
                .setParameter("status", "ACTIVE")
                .getResultList();
    }
}
