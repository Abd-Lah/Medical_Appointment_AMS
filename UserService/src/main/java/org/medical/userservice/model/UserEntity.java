package org.medical.userservice.model;


import jakarta.persistence.*;
import lombok.*;
import org.medical.userservice.dto.request.UserRequest;
import org.medical.userservice.dto.response.DoctorProfileDtoResponse;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Transient
    private DoctorProfileDtoResponse doctorProfile;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(name = "first_name", nullable=false)
    private String firstName;

    @Column(name = "last_name", nullable=false)
    private String lastName;

    @Column(name = "city", nullable=false)
    private String city;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;  // PATIENT, DOCTOR, ADMIN



    public UserEntity(String email, String password, String firstName, String lastName, String phoneNumber, String city, RoleEnum role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.role = role;
        this.active = (role == RoleEnum.PATIENT);
    }

    public void updateUserProfile(UserRequest user) {
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setPhoneNumber(user.getPhone());
        this.setCity(user.getCity());
    }
}
