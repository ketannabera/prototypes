package objectmethods;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Person {

    private String name;
    private Integer age;
    @Override
    public int hashCode(){
        return this.age;
    }

//    @Override
//    public boolean equals(Object obj){
//        if(obj==null){
//            return false;
//        }
//        if(this==obj){
//            return true;
//        }
//        if(getClass() == obj.getClass()){
//            return Objects.equals(this.age, ((Person) obj).age) && Objects.equals(this.name, ((Person) obj).name);
//        }
//        return false;
//    }
}
