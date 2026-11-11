package com.taskmate;

import org.hibernate.Session;

public class HibernateTest {

    public static void main(String[] args) {

        System.out.println("Menguji koneksi Hibernate...");

        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            session.doWork(connection -> {
                System.out.println(
                    "Koneksi Hibernate berhasil!"
                );

                System.out.println(
                    "Database: " + connection.getCatalog()
                );
            });

        } catch (Exception e) {

            System.out.println(
                "Koneksi Hibernate gagal!"
            );

            e.printStackTrace();

        } finally {
            HibernateUtil.shutdown();
        }
    }
}
