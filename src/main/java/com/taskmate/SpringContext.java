package com.taskmate;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class SpringContext {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml")) {
            TaskDAO taskDAO = context.getBean(TaskDAO.class);
            System.out.println("================================");
            System.out.println("PENGUJIAN DELETE TASK");
            System.out.println("================================");
            Integer idTask = 8;
            taskDAO.hapusTask(idTask);
            System.out.println("PENGUJIAN DELETE SELESAI!");
        } catch (Exception e) {
            System.err.println("PENGUJIAN DELETE GAGAL!");
            e.printStackTrace();
        }
    }
}
