package com.cris959.foro_hub.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Perfil")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "perfiles")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private PerfilNombre nombre;
}
