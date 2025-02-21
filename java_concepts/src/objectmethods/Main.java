package objectmethods;

public class Main {
    public static void main(String[] arg){
//        Person p1 = new Person("Ketan",25);
//        Person p2 = new Person(new String("Ketan"), 25);
//        System.out.println(p1.equals(p2));

        PersonRecord pr1 = new PersonRecord("Ketan",25);
        PersonRecord pr2 = new PersonRecord(new String("Ketan"), 25);
        System.out.println(pr1.equals(pr2));

        String s1 = new String("Jack");
        String s2 = new String("Jack");

        System.out.println(s1.equals(s2)); // Because of String interning
    }
}
