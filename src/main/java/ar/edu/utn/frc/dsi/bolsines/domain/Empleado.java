package ar.edu.utn.frc.dsi.bolsines.domain;

import jakarta.persistence.*;

@Entity
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String apellido;
    private String nombre;
    private String email;

    @OneToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private ComisionMedica destino;

    protected Empleado() {}
    public Empleado(String apellido, String nombre, String email, Usuario usuario, ComisionMedica destino) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.email = email;
        this.usuario = usuario;
        this.destino = destino;
    }

    public boolean esTuUsuario(Usuario u) {
        return this.usuario != null && u != null && this.usuario.getId().equals(u.getId());
    }

    public ComisionMedica obtenerCM() {
        return this.destino;
    }

    public Long getId() { return id; }
    public String getNombreCompleto() { return apellido + ", " + nombre; }
}
