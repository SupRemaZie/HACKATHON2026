package com.vdef.hackathon.dto;

import java.util.UUID;

public class SiteDTO {

    private UUID id;
    private UUID createdBy;
    private String name;
    private String address;
    private String city;
    private Double surfaceM2;
    private Integer nbEmployees;
    private Integer nbWorkstations;
    private Integer parkingUnderground;
    private Integer parkingBasement;
    private Integer parkingOutdoor;
    private Double energyKwhYear;
    private String energySource;

    public SiteDTO() {}

    public SiteDTO(UUID id, UUID createdBy, String name, String address, String city,
                   Double surfaceM2, Integer nbEmployees, Integer nbWorkstations,
                   Integer parkingUnderground, Integer parkingBasement, Integer parkingOutdoor,
                   Double energyKwhYear, String energySource) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
        this.address = address;
        this.city = city;
        this.surfaceM2 = surfaceM2;
        this.nbEmployees = nbEmployees;
        this.nbWorkstations = nbWorkstations;
        this.parkingUnderground = parkingUnderground;
        this.parkingBasement = parkingBasement;
        this.parkingOutdoor = parkingOutdoor;
        this.energyKwhYear = energyKwhYear;
        this.energySource = energySource;
    }
}