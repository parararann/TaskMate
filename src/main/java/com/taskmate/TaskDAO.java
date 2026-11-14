package com.taskmate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public class TaskDAO {
    @PersistenceContext
    private EntityManager entityManager;
    // CREATE
    @Transactional
    public void tambahTask(Task task) {
        entityManager.persist(task);
        System.out.println("Tugas berhasil ditambahkan!");
    }
    // READ
    @Transactional(readOnly = true)
    public List<Task> tampilkanSemuaTask() {
        return entityManager.createQuery(
                "FROM Task ORDER BY id ASC",
                Task.class
        ).getResultList();
    }
    // UPDATE SELURUH DATA TUGAS
    @Transactional
    public void updateTask(Task updatedTask) {
        Task task = entityManager.find(
                Task.class,
                updatedTask.getId()
        );
        if (task != null) {
            task.setNamaTugas(updatedTask.getNamaTugas());
            task.setDeskripsi(updatedTask.getDeskripsi());
            task.setTenggat(updatedTask.getTenggat());
            task.setStatus(updatedTask.getStatus());
            System.out.println("Tugas berhasil diperbarui!");
        } else {
            System.out.println("Tugas tidak ditemukan!");
        }
    }
    // UPDATE STATUS
    @Transactional
    public void ubahStatus(Integer id, String status) {
        Task task = entityManager.find(Task.class, id);
        if (task != null) {
            task.setStatus(status);
            System.out.println("Status berhasil diubah!");
        } else {
            System.out.println("Tugas tidak ditemukan!");
        }
    }
    // DELETE
    @Transactional
    public void hapusTask(Integer id) {
        Task task = entityManager.find(Task.class, id);
        if (task != null) {
            entityManager.remove(task);
            System.out.println("Tugas berhasil dihapus!");
        } else {
            System.out.println("Tugas tidak ditemukan!");
        }
    }
}