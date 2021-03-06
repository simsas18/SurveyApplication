package com.javainis.user_management.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_type")
@NamedQueries({
        @NamedQuery(name = "UserType.findType", query = "SELECT ut FROM UserType ut WHERE ut.id = :id")
})
@Getter
@Setter
public class UserType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 10)
    private String name;
}