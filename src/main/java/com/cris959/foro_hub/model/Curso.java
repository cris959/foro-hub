package com.cris959.foro_hub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cursos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre", "categoria"}, name = "unique_nombre_categoria")
})
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    private String categoria;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
