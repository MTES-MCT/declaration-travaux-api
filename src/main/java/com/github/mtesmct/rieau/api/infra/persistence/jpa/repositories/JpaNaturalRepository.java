package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;

@Transactional(readOnly = true)
public class JpaNaturalRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements NaturalRepository<T, ID> {
    private final EntityManager entityManager;

    public JpaNaturalRepository(JpaEntityInformation<T, ID> entityInformation,
            EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }
    @Override
    public Optional<T> findBySimpleNaturalId(ID naturalId) {
        Optional<T> entity = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(this.getDomainClass())
                .loadOptional(naturalId);
        return entity;
    }
}