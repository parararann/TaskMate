package com.taskmate;

public class TaskUpdateTest {

    public static void main(String[] args) {

        TaskDAO taskDAO = new TaskDAO();

        System.out.println("=== MENGUJI UPDATE TASK ===");

        // Mengubah status tugas ID 5
        taskDAO.ubahStatus(5, "Sedang Dikerjakan");

        // Menampilkan data setelah update
        System.out.println("\n=== DATA SETELAH UPDATE ===");

        for (Task task : taskDAO.tampilkanSemuaTask()) {

            System.out.println("--------------------");
            System.out.println("ID     : " + task.getId());
            System.out.println("Nama   : " + task.getNamaTugas());
            System.out.println("Status : " + task.getStatus());
        }

        HibernateUtil.shutdown();
    }
}
