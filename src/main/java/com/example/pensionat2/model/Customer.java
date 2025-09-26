package com.example.pensionat2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
//import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Förnamn får inte vara tomt")
    @Size(max = 50, message = "Förnamn får max vara 50 tecken")
    private String firstName;

    @NotBlank(message = "Efternamn får inte vara tomt")
    @Size(max = 50, message = "Efternamn får max vara 50 tecken")
    private String lastName;

    @NotBlank(message = "E-post får inte vara tomt")
    @Email(message = "E-post måste vara giltig")
    private String email;

    @Pattern(
            regexp = "^\\+?[0-9]{1,4}?[ .-]?[0-9]{6,12}$",
            message = "Telefonnummer är ogiltigt – endast siffror, mellanslag, punkt, bindestreck och eventuellt + i början är tillåtna"
    )private String phone;
}
