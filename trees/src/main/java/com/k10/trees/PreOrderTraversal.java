package com.k10.trees;

public class PreOrderTraversal {
    public static void traverse(Node root) {

        System.out.println("****** PRE ORDER *****");
        traverseDown(root);
    }

    private static void traverseDown(Node root) {
        if(root==null){
            return;
        }
        System.out.print(root.getValue()+" ");
        traverseDown(root.getLeft());
        traverseDown(root.getRight());

    }
}
