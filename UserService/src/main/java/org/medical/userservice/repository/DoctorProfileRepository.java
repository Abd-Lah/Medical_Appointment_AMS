package org.medical.userservice.repository;

import org.medical.userservice.model.DoctorProfileEntity;
import org.medical.userservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfileEntity, String> {
    DoctorProfileEntity getDoctorProfileByDoctor(UserEntity user);
}
