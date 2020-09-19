package ru.romasini.hibernate.beginner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MainApp {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration()
                .addAnnotatedClass(Buyer.class)
                .addAnnotatedClass(Item.class)
                .addAnnotatedClass(Order.class)
                .buildSessionFactory();

        Session session = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            prepareData(sessionFactory);

            String temp = null;
            while(true) {
                System.out.println("Выполнить операцию:");
                System.out.println("new_i - добавить товар");
                System.out.println("del_i - удалить товар");
                System.out.println("list_i - список товаров");
                System.out.println("new_b - добавить покупателя");
                System.out.println("del_b - удалить покупателя");
                System.out.println("list_b - список покупателей");
                System.out.println("buy - сделать покупку");
                System.out.println("list_i_b - список покупок клиента");
                System.out.println("list_b_i - список клиентов, купивших товар");
                System.out.println("price_b_i - детализация покупок товара и клиента");
                System.out.println("exit - выход");

                temp = bufferedReader.readLine();

                if(temp.equals("exit")){
                    System.out.println("Завершение программы");
                    break;
                }

                if(temp.equals("new_i")){

                    System.out.println("Введите название товара");
                    String title = bufferedReader.readLine();
                    System.out.println("Введите цену товара");
                    BigDecimal price = new BigDecimal(bufferedReader.readLine());
                    Item item = new Item(title, price);

                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    session.saveOrUpdate(item);
                    session.getTransaction().commit();
                    System.out.println("______________");
                }
                if(temp.equals("del_i")){
                    System.out.println("Введите индекс удалямого товара");
                    Long id = Long.parseLong(bufferedReader.readLine());
                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    session.delete(session.get(Item.class, id));
                    session.getTransaction().commit();
                    System.out.println("______________");
                }
                if(temp.equals("list_i")){
                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    List<Item> list_i = session.createQuery("SELECT i FROM Item i", Item.class).getResultList();
                    session.getTransaction().commit();
                    for (Item i:list_i) {
                        System.out.println(i);
                    }
                    System.out.println("______________");
                }

                if(temp.equals("new_b")){
                    System.out.println("Введите имя клиента");
                    String name = bufferedReader.readLine();
                    Buyer buyer = new Buyer(name);

                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    session.saveOrUpdate(buyer);
                    session.getTransaction().commit();
                    System.out.println("______________");
                }
                if(temp.equals("del_b")){
                    System.out.println("Введите индекс удалямого клиента");
                    Long id = Long.parseLong(bufferedReader.readLine());
                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    session.delete(session.get(Buyer.class, id));
                    session.getTransaction().commit();
                    System.out.println("______________");
                }
                if(temp.equals("list_b")){
                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    List<Buyer> list_b = session.createQuery("SELECT b FROM Buyer b", Buyer.class).getResultList();
                    session.getTransaction().commit();
                    for (Buyer b:list_b) {
                        System.out.println(b);
                    }
                    System.out.println("______________");
                }

                if(temp.equals("buy")){
                    System.out.println("Введите индекс клиента");
                    Long id_b = Long.parseLong(bufferedReader.readLine());
                    System.out.println("Введите индекс товара");
                    Long id_i = Long.parseLong(bufferedReader.readLine());

                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    Buyer b = session.get(Buyer.class, id_b);
                    Item i = session.get(Item.class, id_i);
                    Order order = new Order(b, i, i.getPrice());
                    session.saveOrUpdate(order);
                    session.getTransaction().commit();
                    System.out.println("______________");
                }
                if(temp.equals("list_i_b")){
                    System.out.println("Введите индекс клиента, по которому вывести список покупок");
                    Long id_b = Long.parseLong(bufferedReader.readLine());
                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    List<Item> list_i = session.createQuery("SELECT order.item FROM Order order WHERE order.buyer.id=:id", Item.class)
                            .setParameter("id", id_b)
                            .getResultList();
                    session.getTransaction().commit();

                    for (Item i: list_i) {
                        System.out.println(i);
                    }
                    System.out.println("______________");

                }
                if(temp.equals("list_b_i")){
                    System.out.println("Введите индекс товара, по которому получить список покупателей");
                    Long id_i = Long.parseLong(bufferedReader.readLine());
                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    List<Buyer> list_b = session.createQuery("SELECT order.buyer FROM Order order WHERE order.item.id=:id", Buyer.class)
                            .setParameter("id", id_i)
                            .getResultList();
                    session.getTransaction().commit();

                    for (Buyer b: list_b) {
                        System.out.println(b);
                    }
                    System.out.println("______________");
                }

                if(temp.equals("price_b_i")){
                    System.out.println("Введите индекс товара");
                    Long id_i = Long.parseLong(bufferedReader.readLine());
                    System.out.println("Введите индекс клиента");
                    Long id_b = Long.parseLong(bufferedReader.readLine());

                    session = sessionFactory.getCurrentSession();
                    session.beginTransaction();
                    List<Order> list_o = session.createQuery("SELECT order FROM Order order WHERE order.item.id=:id_i AND order.buyer.id=:id_b", Order.class)
                            .setParameter("id_i", id_i)
                            .setParameter("id_b", id_b)
                            .getResultList();
                    session.getTransaction().commit();

                    for (Order o: list_o) {
                        System.out.println(o);
                    }
                    System.out.println("______________");
                }

            }
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            sessionFactory.close();
            if(session!=null){
                session.close();
            }
        }

    }

    private static void prepareData(SessionFactory sessionFactory) throws IOException {

        String sqlQuery = Files.lines(Paths.get("prepare.sql")).collect(Collectors.joining(" "));
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.createNativeQuery(sqlQuery).executeUpdate();
        session.getTransaction().commit();
        session.close();

    }
}
