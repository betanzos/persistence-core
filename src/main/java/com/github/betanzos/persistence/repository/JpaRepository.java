/**
 * Copyright 2019 Eduardo E. Betanzos Morales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.betanzos.persistence.repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class JpaRepository<T> implements Repository<T> {
    private EntityManager em;
    private Class<T> entityType;

    public JpaRepository(final EntityManager entityManager) {
        if (entityManager == null) {
            throw new IllegalArgumentException("'entityManager' can not be null");
        }

        em = entityManager;

        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        entityType = (Class<T>) pt.getActualTypeArguments()[0];
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public T create(final T t) {
        em.getTransaction().begin();
        em.persist(t);
        em.getTransaction().commit();
        return t;
    }

    @Override
    public void remove(final T t) {
        em.getTransaction().begin();
        em.remove(em.merge(t));
        em.getTransaction().commit();
    }

    @Override
    public T findById(final Object id) {
        return em.find(entityType, id);
    }

    @Override
    public T update(final T t) {
        em.getTransaction().begin();
        T updated = em.merge(t);
        em.getTransaction().commit();
        return updated;
    }

    @Override
    public List<T> findAll() {
        return findAllAsStream().collect(Collectors.toList());
    }

    @Override
    public Stream<T> findAllAsStream() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(entityType);

        Root<T> root = criteriaQuery.from(entityType);
        criteriaQuery.select(root);

        TypedQuery<T> query = em.createQuery(criteriaQuery);
        return query.getResultStream();
    }
}
