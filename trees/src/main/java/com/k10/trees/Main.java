package com.k10.trees;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Node root = getTree();

        LevelOrderTraversal.levelOrderRec(root);
        LevelOrderTraversal.levelOrderQueue(root);

        InOrderTraversal.traverse(root);
        System.out.println();
        PreOrderTraversal.traverse(root);
        System.out.println();
        PostOrderTraversal.traverse(root);
        System.out.println();
    }

    private static Node getTree() {
        Node root = new Node(10);
        Node left = createNode(11, 13, 14);
        left.getLeft().setLeft(createNode(17, null, 23));
        left.getRight().setRight(createNode(15, 16, null));
        root.setLeft(left);

        Node right = createNode(12, null, 18);
        right.getRight().setLeft(createNode(19, 20, 21));
        right.getRight().getLeft().getRight().setRight(new Node(22));
        root.setRight(right);
        return root;
    }

    private static Node createNode(Integer i, Integer left, Integer right) {
        Node root = new Node(i);
        if (left != null) {
            root.setLeft(new Node(left));
        }
        if (right != null) {
            root.setRight(new Node(right));
        }
        return root;
    }

}

