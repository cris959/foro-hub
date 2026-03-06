package com.cris959.foro_hub.model;
/*
Copyright 2026 Christian Garay

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity(name = "Usuario")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@SQLDelete(sql = "UPDATE usuarios SET activo = 0 WHERE id = ?") // Opcional: automatiza el borrado lógico
@Where(clause = "activo = 1") // Filtra automáticamente todos los SELECT
public class Usuario implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Tomamos el nombre del perfil (ej: ADMIN) y lo convertimos en autoridad
        // Spring Security suele esperar el prefijo "ROLE_"
        return List.of(new SimpleGrantedAuthority(perfil.getNombre().name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
