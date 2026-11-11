package com.taskmate;

import java.util.List;

public class TaskCRUDTest {

    public static void main(String[] args) {

        TaskDAO taskDAO = new TaskDAO();

        try {

            // CREATE: Menambahkan tugas baru
            System.out.println("=== MENAMBAHKAN TUGAS ===");

            Task task = new Task();

            task.setNamaTugas("Belajar Hibernate");
            task.setDeskripsi(
                    "Menguji integrasi Hibernate dengan MySQL"
            );
            task.setTenggat("2026-11-15");
            task.setStatus("Belum Dikerjakan");

            taskDAO.tambahTask(task);

            // READ: Menampilkan seluruh tugas
            System.out.println("\n=== DAFTAR SEMUA TUGAS ===");

            List<Task> daftarTask =
                    taskDAO.tampilkanSemuaTask();

            for (Task t : daftarTask) {

                System.out.println("--------------------");
                System.out.println("ID       : " + t.getId());
                System.out.println("Nama     : " + t.getNamaTugas());
                System.out.println("Deskripsi: " + t.getDeskripsi());
                System.out.println("Tenggat  : " + t.getTenggat());
                System.out.println("Status   : " + t.getStatus());
            }

        } finally {

            HibernateUtil.shutdown();

        }
    }
}