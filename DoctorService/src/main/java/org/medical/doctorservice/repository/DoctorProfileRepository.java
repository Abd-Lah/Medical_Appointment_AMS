package org.medical.doctorservice.repository;

import org.medical.doctorservice.model.DoctorProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfileEntity, String> {
    DoctorProfileEntity getDoctorProfileByDoctorId(String id);
}
