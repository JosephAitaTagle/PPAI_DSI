package ar.edu.utn.frc.dsi.bolsines.domain;

import jakarta.persistence.*;

@Entity
public class ComisionMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String codigo;
    private String direccion;
    private String email;
    private String telefono;
    private boolean esCMC;

    protected ComisionMedica() {}
    public ComisionMedica(String nombre, String codigo, String direccion, String email, String telefono, boolean esCMC) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
        this.esCMC = esCMC;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getCodigo() { return codigo; }
    public boolean isEsCMC() { return esCMC; }
}
