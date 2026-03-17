package com.vdef.hackathon.jpa;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.security.SecureRandom;

@Entity
@Table(name = "sites")
public class SiteJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String token;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(length = 255)
    private String city;

    @Column(name = "surface_m2", nullable = false)
    private Double surfaceM2;

    @Column(name = "nb_employees", nullable = false)
    private Integer nbEmployees = 0;

    @Column(name = "nb_workstations", nullable = false)
    private Integer nbWorkstations = 0;

    @Column(name = "parking_underground", nullable = false)
    private Integer parkingUnderground = 0;

    @Column(name = "parking_basement", nullable = false)
    private Integer parkingBasement = 0;

    @Column(name = "parking_outdoor", nullable = false)
    private Integer parkingOutdoor = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public SiteJPA() {}

    public static String generateRawToken() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getSurfaceM2() { return surfaceM2; }
    public void setSurfaceM2(Double surfaceM2) { this.surfaceM2 = surfaceM2; }
    public Integer getNbEmployees() { return nbEmployees; }
    public void setNbEmployees(Integer nbEmployees) { this.nbEmployees = nbEmployees; }
    public Integer getNbWorkstations() { return nbWorkstations; }
    public void setNbWorkstations(Integer nbWorkstations) { this.nbWorkstations = nbWorkstations; }
    public Integer getParkingUnderground() { return parkingUnderground; }
    public void setParkingUnderground(Integer parkingUnderground) { this.parkingUnderground = parkingUnderground; }
    public Integer getParkingBasement() { return parkingBasement; }
    public void setParkingBasement(Integer parkingBasement) { this.parkingBasement = parkingBasement; }
    public Integer getParkingOutdoor() { return parkingOutdoor; }
    public void setParkingOutdoor(Integer parkingOutdoor) { this.parkingOutdoor = parkingOutdoor; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
