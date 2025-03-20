package singleton;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Setter
class Person{
    private String name;
    private Integer age;

    @Override
    public int hashCode(){
        return this.age;
    }
}
public class HashMapMain {
    public static void main(String[] ar){
        Map<Person,Integer> map = new ConcurrentHashMap<>();

        Person p1 = new Person("Jack",25);
//        Person p2 = new Person("Reacher", 40);
        map.put(p1, 1);
        System.out.println(map.size());

        p1.setAge(45);
        map.put(p1, 2);
        System.out.println(map.size());

        Map<Person, Integer> personIntegerMap = Collections.singletonMap(p1, 1);
        personIntegerMap.putIfAbsent(p1,2);
        System.out.println(personIntegerMap.get(p1));

    }
}
