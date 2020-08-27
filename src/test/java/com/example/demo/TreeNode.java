package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TreeNode {
    private TreeNode left;
    private TreeNode right;
    private int val;


    public class NeiBu {

    }
}
