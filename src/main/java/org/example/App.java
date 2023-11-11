package org.example;

import org.example.model.Item;
import org.example.model.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {

        Configuration configuration = new Configuration()
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Item.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();

            Person person = session.get(Person.class, 3);
            System.out.println(person);
            List<Item> items = person.getItems();
            System.out.println(items);

            Item item = session.get(Item.class, 5);
            System.out.println(item);
            Person person1 = item.getOwner();
            System.out.println(person1);

            Person person2 = session.get(Person.class, 2);
            Item newItem = new Item(225, "Item from Hibernate", person2);
            person2.getItems().add(newItem);
            session.save(newItem);

            Person person3 = new Person(25, "Test person 2", 25,
                    new ArrayList<Item>());
            Item newItem2 = new Item(23,
                    "Item from Hibernate 2", person3);
            session.save(person3);
            session.save(newItem2);

            //Створює SQL
            Person person4 = session.get(Person.class, 2);
            List<Item> items1 = person4.getItems();
            for(Item item1: items1 ){
                session.remove(item1);
            }
            //Не створює SQL, але необхідно для оновленння кешу
            person4.getItems().clear();

            Person person5 = session.get(Person.class, 2);
            //SQL
            session.remove(person5);
            //Для правильного стану Hibernate-кеша
            person5.getItems().forEach(i -> i.setOwner(null));

            Person person6 = session.get(Person.class, 4);
            Item item3 = session.get(Item.class, 1);
            // Для кеша
            item3.getOwner().getItems().remove(item3);
            //SQL
            item3.setOwner(person6);
            // Для кеша
            person6.getItems().add(item3);

            session.getTransaction().commit();

        } finally {
            sessionFactory.close();
        }
    }
}
