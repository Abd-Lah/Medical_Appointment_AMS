package org.medical.userservice.repository;

import jakarta.persistence.criteria.*;
import org.medical.userservice.model.RoleEnum;
import org.medical.userservice.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);

    default Page<UserEntity> getUsersBySpec(String firstName, String lastName, String city ,RoleEnum role, Pageable pageable) {
        return findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add filtering conditions for firstName, lastName, and city
            if (firstName != null && !firstName.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
            }

            if (lastName != null && !lastName.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
            }

            if (city != null && !city.isEmpty()) {
                predicates.add(builder.like(builder.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }

            // Filter for active and non-deleted users
            predicates.add(builder.equal(root.get("role"), role));
            predicates.add(builder.equal(root.get("active"), true));
            predicates.add(builder.equal(root.get("deleted"), false));

            // Apply the predicates to the query
            query.where(builder.and(predicates.toArray(new Predicate[0])));

            // Apply sorting from Pageable
            pageable.getSort();
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                Path<Object> path = root.get(order.getProperty());
                if (order.isAscending()) {
                    orders.add(builder.asc(path));
                } else {
                    orders.add(builder.desc(path));
                }
            });
            query.orderBy(orders);

            // Return the query
            return query.getRestriction();
        }, pageable);
    }

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'PATIENT' AND u.id = :id")
    UserEntity getPatient(@Param("id") String id);
}
