package com.k10.misc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Iterators {

    public void failFast() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            //iterator.remove(); // Will fail if iterator.next() not called. But its not CME
            list.remove(1); // This will give error while doing iterator.next()
        }
        for (Integer i = 0; i < list.size(); i++) {
            list.remove(i); // Will not fail as it removes Object
        }

        for (int i : list) {
            list.remove(i); // Will fail as its index based removal
        }

        System.out.print(list);
    }

    public void failFastMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 1);
        map.put("2", 1);
        map.put("3", 1);
        map.put("4", 1);

//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//
//            map.remove(entry.getKey());  // Modifying map while iterating
//
//        }

        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();

//        while (iterator.hasNext()) {
////            map.put("D", 4);// Modifying map while iterating
//            iterator.next(); //This is where exception is thrown, not at above statement.
//        }

        while (iterator.hasNext()){
            iterator.next();
            iterator.remove(); // This is safe
        }

        System.out.print(map);
    }

    public void failSafe() {
        List<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            list.add(5);
            System.out.print(iterator.next());
        }
        for (Integer i = 0; i < list.size(); i++) {
            list.remove(i);
        }

        System.out.print(list);

        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("1", 1);
        map.put("2", 1);
        map.put("3", 1);
        map.put("4", 1);


    }
}
