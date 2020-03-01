# Wellcome to persistence-core
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat-square&logo=Apache)](http://www.apache.org/licenses/LICENSE-2.0)

This library provide interfaces and generic implementations which can help you
to develop an application persistence layer without take care about common
persistence code, like CRUD operations.

Current versión (1.0-SNAPSHOT) only suport JPA 2.2 and requires Java 8.

# How to use
This is a Maven project and its artifacts are published as GitHub Packages.

In order to use it in your Maven project simply do the following (for use JPA with
Hibernate as provider):

**1 - Configure the repository in your project `pom.xml`**
```xml
<project>
    <repositories>
        <repository>
            <id>github-betanzos</id>
            <name>GitHub Maven Packages from betanzos</name>
            <url>https://maven.pkg.github.com/betanzos/maven</url>
        </repository>
    </repositories>
</project>
```

***Note:*** The previous piece is simplified, your actual file may have more content.

**2 - Configure repository authentication**

***DISCLAIMER:*** Until today (March 01, 2020) GitHub Packages requires authentication for read
packages from both public and private repositories.

Add to your Maven configuration file `settings.xml` a server entry.

```xml
<settings>
    <servers>
        <server>
            <id>github-betanzos</id>
            <username>betanzos</username>
            <!-- Token with read-only permissions -->
            <password>7f7dfc8489e112b30ec71dd0dfe9e1431fb4c92a</password>
        </server>
    </servers>
</settings>
```

***Note:*** The previous piece is simplified, your actual file may have more content.

**3 - Add needed dependencies entries to your project `pom.xml`**
```xml
<dependencies>
    <dependency>
        <groupId>com.github.betanzos</groupId>
        <artifactId>persistence-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.4.12.Final</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.200</version>
    </dependency>
</dependencies>
```

If you want to use a different database change the `h2` dependency for the appropriate
JDBC driver dependency.

**4 - Create the `persistence.xml` file under `src/main/resources/META-INF` directory**
```xml
<persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                                 http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2" xmlns="http://xmlns.jcp.org/xml/ns/persistence">

    <persistence-unit name="test" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:test" />
            <property name="javax.persistence.jdbc.user" value="sa" />
            <property name="javax.persistence.jdbc.password" value="" />
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
            <property name="hibernate.hbm2ddl.auto" value="create-drop" />
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.temp.use_jdbc_metadata_defaults" value="false"/>
        </properties>
    </persistence-unit>

</persistence>
```
In order to use another JPA provider just change `<provider>` tag value.

We use H2 database but you can use any database that support JDBC changing the
value of the properties:
- `javax.persistence.jdbc.driver`
- `javax.persistence.jdbc.url`
- `javax.persistence.jdbc.user`
- `javax.persistence.jdbc.password` 
- `hibernate.dialect` 

**5 - Create an `@Entity` class**
```java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    private Integer age;

    public Person() {
    }
    
    public Person(String fullname, Integer age) {
        this.fullname = fullname;
        this.age = age;
    }

    // GETTERS & SETTERS
}
```

**6 - Create a repository class that extend from `JpaRepository` abstract class**
```java
import com.github.betanzos.persistence.repository.JpaReposytory;

import javax.persistence.EntityManager;

public class PersonRepository extends JpaRepository<Person> {
    public PersonRepository(EntityManager entityManager) {
        super(entityManager);
    }
}
```

**7 - Use your repository**
```java
import com.github.betanzos.persistence.repository.Reposytory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersonRepositoryTest {
    private static EntityManager em;
    private static Repository<Person> repository;

    public static void main(String[] args) {
        // Note "test" is the name of the persistence unit defined in persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
        EntityManager em = emf.createEntityManager();
        
        Repository<Person> repository = new PersonRepository(em);
        
        Person p = new Person(name, age);
        repository.create(p);
    }
}
```

# License
This project is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).