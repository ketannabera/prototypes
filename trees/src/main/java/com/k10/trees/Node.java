package com.k10.trees;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    private Integer value;
    private Node left;
    private Node right;
    public Node(Integer value){
        this.value = value;
    }
}
