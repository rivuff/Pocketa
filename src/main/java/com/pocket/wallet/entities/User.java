package com.pocket.wallet.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "User")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntitty{

    private String name;

    private String email;

}
