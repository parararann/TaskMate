package com.taskmate;
import jakarta.persistence.*;
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama_tugas", length = 150, nullable = false)
    private String namaTugas;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "tenggat", length = 30)
    private String tenggat;

    @Column(name = "status", nullable = false)
    private String status;

    // Constructor
    public Task() {
    }

    // Getter dan Setter ID
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter dan Setter Nama Tugas
    public String getNamaTugas() {
        return namaTugas;
    }

    public void setNamaTugas(String namaTugas) {
        this.namaTugas = namaTugas;
    }

    // Getter dan Setter Deskripsi
    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // Getter dan Setter Tenggat
    public String getTenggat() {
        return tenggat;
    }

    public void setTenggat(String tenggat) {
        this.tenggat = tenggat;
    }

    // Getter dan Setter Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
