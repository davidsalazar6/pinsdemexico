package com.pins.ordermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "field can't be empty")
    private String username;
    @NotEmpty(message = "field can't be empty")
    @Email
    private String email;
    @NotEmpty(message = "field can't be empty")
    @Size(min = 4, message = "Password should have at least 4 characters")
    private String password;
    private boolean active = true;
    @ManyToMany(fetch = FetchType.EAGER , cascade = {CascadeType.ALL})
    private List<Role> roles = new ArrayList<>();

}