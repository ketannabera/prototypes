package com.k10.trees;

import java.util.*;

public class LevelOrderTraversal {


    public static void levelOrderQueue(Node root) {
        Queue<Node> queue = new LinkedList<>();
        Map<Integer, List<Integer>> levels = new HashMap<>();
        Queue<Integer> levelQueue = new LinkedList<>();

        queue.offer(root);
        levelQueue.offer(1);
        while(!queue.isEmpty()){
            Node current = queue.poll();
            Integer currIndex = levelQueue.poll();
            levels.computeIfAbsent(currIndex, k-> new ArrayList<>()).add(current.getValue());

            if(current.getLeft()!=null){
                queue.add(current.getLeft());
                levelQueue.offer(currIndex + 1);
            }

            if(current.getRight()!=null){
                queue.add(current.getRight());
                levelQueue.offer(currIndex + 1);
            }

        }

        System.out.println("******** LEVEL ORDER QUEUE ********");
        for(int i=1;i<=levels.size();i++){
            System.out.println("Level : "+ i);
            System.out.println(levels.get(i));
        }

    }

    public static void levelOrderRec(Node root) {
        Map<Integer,List<Integer>> levels = new HashMap<>();
        if(root==null){
            return;
        }

        traverseLevel(root,1,levels);

        System.out.println("******** LEVEL ORDER REC ********");
        for(int i=1;i<=levels.size();i++){
            System.out.println("Level : "+ i);
            System.out.println(levels.get(i));
        }
    }


    private static void traverseLevel(Node root, int i, Map<Integer, List<Integer>> levels) {
        List<Integer> levelValues = levels.getOrDefault(i,new ArrayList<>());
        levelValues.add(root.getValue());
        levels.put(i, levelValues);

        if(root.getLeft()!=null){
            traverseLevel(root.getLeft(), i+1, levels);
        }
        if(root.getRight()!=null){
            traverseLevel(root.getRight(),i+1, levels);
        }
    }

}
