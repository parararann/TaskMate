package com.taskmate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
public class TaskDAO {

    // CREATE
    public void tambahTask(Task task) {

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(task);
            transaction.commit();
            System.out.println("Tugas berhasil ditambahkan!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // READ
    public List<Task> tampilkanSemuaTask() {

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            return session.createQuery(
                    "FROM Task ORDER BY id ASC",
                    Task.class
            ).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // UPDATE SELURUH DATA TUGAS
    public void updateTask(Task updatedTask) {
        Transaction transaction = null;
        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Task task = session.get(
                    Task.class,
                    updatedTask.getId()
            );

            if (task != null) {

                task.setNamaTugas(updatedTask.getNamaTugas());
                task.setDeskripsi(updatedTask.getDeskripsi());
                task.setTenggat(updatedTask.getTenggat());
                task.setStatus(updatedTask.getStatus());

                transaction.commit();

                System.out.println("Tugas berhasil diperbarui!");

            } else {

                transaction.rollback();
                System.out.println("Tugas tidak ditemukan!");
            }

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        }
    }

    // UPDATE STATUS
    public void ubahStatus(Integer id, String status) {

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Task task = session.get(Task.class, id);

            if (task != null) {

                task.setStatus(status);
                transaction.commit();

                System.out.println("Status berhasil diubah!");

            } else {

                transaction.rollback();
                System.out.println("Tugas tidak ditemukan!");
            }

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        }
    }

    // DELETE
    public void hapusTask(Integer id) {

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Task task = session.get(Task.class, id);

            if (task != null) {

                session.remove(task);
                transaction.commit();

                System.out.println("Tugas berhasil dihapus!");

            } else {

                transaction.rollback();
                System.out.println("Tugas tidak ditemukan!");
            }

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        }
    }
}
