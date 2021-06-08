package generic;

import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start");
        List<? super Animal> animals = new LinkedList<Animal>();
        animals.add(new Cat());
        animals.add(new Dog());
        System.out.println(animals);

        Animal a = (Animal) animals.get(0);
        System.out.println(a);
    }
}

@ToString
class Animal {

}

@ToString
class Cat extends Animal {

}

@ToString
class Dog extends Animal {

}
