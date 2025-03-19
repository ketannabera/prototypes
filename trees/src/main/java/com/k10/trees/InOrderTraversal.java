package com.k10.trees;

public class InOrderTraversal {
    public static void traverse(Node root) {

        System.out.println("****** IN ORDER *****");
        traverseDown(root);
    }

    private static void traverseDown(Node root) {
        if(root==null){
            return;
        }
        traverseDown(root.getLeft());
        System.out.print(root.getValue()+" ");
        traverseDown(root.getRight());

    }
}
