package com.raxrot.back.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 3,message = "street name must be at least 3 character")
    private String street;

    @NotBlank
    @Size(min = 5,message = "buildingName must be at least 5 character")
    private String buildingName;

    @NotBlank
    @Size(min = 4,message = "city must be at least 4 character")
    private String city;

    @NotBlank
    @Size(min = 2,message = "state must be at least 2 character")
    private String state;

    @NotBlank
    @Size(min = 2,message = "country must be at least 2 character")
    private String country;

    @NotBlank
    @Size(min = 5,message = "pincode must be at least 5 character")
    private String pincode;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user)id")
    private User user;

    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
