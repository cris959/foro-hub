package com.cris959.foro_hub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity(name = "Usuario")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@SQLDelete(sql = "UPDATE usuarios SET activo = 0 WHERE id = ?") // Opcional: automatiza el borrado lógico
@Where(clause = "activo = 1") // Filtra automáticamente todos los SELECT
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String email;

    private String password;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true; // Por defecto está activo

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para tener el rol disponible al autenticar
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;
}
