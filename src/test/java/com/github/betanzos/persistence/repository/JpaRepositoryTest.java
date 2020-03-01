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

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JpaRepositoryTest {
    private static EntityManager em;
    private static Repository<Person> repository;

    @BeforeClass
    public static void configTest() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        repository = new PersonRepository(em);
    }

    private Person createPerson(String name, int age) {
        Person p = new Person(name, age);
        return repository.create(p);
    }

    @Test
    public void test1Create() {
        Person p = createPerson("Pepe", 30);
        assertNotNull(p.getId());
    }

    @Test
    public void test2FindById() {
        Person p = createPerson("José", 40);
        long id = p.getId();
        em.detach(p);// force to find in database instead in the persistence context
        assertNotNull(repository.findById(id));
    }

    @Test
    public void test3Remove() {
        Person p = createPerson("Juan", 50);
        repository.remove(p);
        assertNull(repository.findById(p.getId()));
    }

    @Test
    public void test4Update() {
        Person p = createPerson("Carlos", 20);

        String newName = "Carlos III";
        Integer newAge = 25;

        p.setFullname(newName);
        p.setAge(25);
        repository.update(p);

        em.detach(p);// force to find in database instead in the persistence context
        Person found = repository.findById(p.getId());

        assertTrue(p != found);
        assertEquals(newName, found.getFullname());
        assertEquals(newAge, found.getAge());
    }

    @Test
    public void test5FindAll() {
        // size must be > 1 because of at least two entities has been saved in database in previous tests
        assertTrue(repository.findAll().size() > 1);
    }

    @Test
    public void test6FindAllAsStrem() {
        // size must be > 1 because of at least two entities has been saved in database in previous tests
        assertTrue(repository.findAllAsStream().count() > 1);
    }
}
