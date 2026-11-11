
package com.taskmate;

public class TaskDeleteTest {

    public static void main(String[] args) {

        TaskDAO taskDAO = new TaskDAO();

        System.out.println("=== MENGUJI DELETE TASK ===");

        // Menghapus tugas dengan ID 5
        taskDAO.hapusTask(5);

        // Menampilkan data setelah penghapusan
        System.out.println("\n=== DATA SETELAH DELETE ===");

        for (Task task : taskDAO.tampilkanSemuaTask()) {

            System.out.println("--------------------");
            System.out.println("ID     : " + task.getId());
            System.out.println("Nama   : " + task.getNamaTugas());
            System.out.println("Status : " + task.getStatus());
        }

        HibernateUtil.shutdown();
    }
}
