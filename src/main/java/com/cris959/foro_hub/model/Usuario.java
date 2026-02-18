package com.cris959.foro_hub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_perfiles",
               joinColumns = @JoinColumn(name = "usuario_id"),
               inverseJoinColumns = @JoinColumn(name = "perfil_id"))
    private List<Perfil> perfiles = new ArrayList<>();
}
