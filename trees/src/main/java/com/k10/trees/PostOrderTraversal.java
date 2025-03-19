package com.k10.trees;

public class PostOrderTraversal {

    public static void traverse(Node root) {

        System.out.println("****** POST ORDER *****");
        traverseDown(root);
    }

    private static void traverseDown(Node root) {
        if(root==null){
            return;
        }
        traverseDown(root.getLeft());
        traverseDown(root.getRight());
        System.out.print(root.getValue()+" ");

    }
}
