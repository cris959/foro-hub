package com.cris959.foro_hub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "Usuario")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para tener el rol disponible al autenticar
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;
}
