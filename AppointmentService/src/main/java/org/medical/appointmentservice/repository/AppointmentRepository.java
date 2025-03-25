package org.medical.appointmentservice.repository;

import jakarta.persistence.criteria.Predicate;
import org.medical.appointmentservice.model.AppointmentEntity;
import org.medical.appointmentservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String>, JpaSpecificationExecutor<AppointmentEntity> {

    @Query("SELECT a FROM AppointmentEntity a WHERE a.id = :appointmentId AND (a.doctorId = :userId OR a.patientId = :userId)")
    AppointmentEntity findByAppointmentIdAndByUserId(String appointmentId, String userId);

    default Page<AppointmentEntity> getAppointments(String id, String orderBy, Pageable pageable) {
        return findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Check if the id is the patient or doctor id
            Predicate patientPredicate = builder.equal(root.get("patientId"), id);
            Predicate doctorPredicate = builder.equal(root.get("doctorId"), id);

            // Combine both predicates with OR condition
            predicates.add(builder.or(patientPredicate, doctorPredicate));

            // Order by appointmentDate
            if ("asc".equalsIgnoreCase(orderBy)) {
                query.orderBy(builder.asc(root.get("appointmentDate")));
            } else if ("desc".equalsIgnoreCase(orderBy)) {
                query.orderBy(builder.desc(root.get("appointmentDate")));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }


    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a WHERE a.doctorId = :doctorId AND a.appointmentDate = :appointmentDate AND a.Status = :status")
    Boolean alreadyTaken(@Param("doctorId") String doctorId, @Param("appointmentDate") LocalDateTime appointmentDate, @Param("status") AppointmentStatus status);

    @Query("SELECT COUNT(a) > 0 FROM AppointmentEntity a WHERE a.patientId = :id AND a.Status = :status")
    Boolean pending(String id, AppointmentStatus status);

    @Query("SELECT COUNT(a) > 4 FROM AppointmentEntity a " +
            "WHERE a.patientId = :id " +
            "AND a.Status = :status " +
            "AND a.appointmentDate >= CURRENT_DATE - :beforeDays day " +
            "AND a.appointmentDate < CURRENT_DATE + :afterDays day")
    Boolean canceled(String id, AppointmentStatus status, int beforeDays, int afterDays);

}
