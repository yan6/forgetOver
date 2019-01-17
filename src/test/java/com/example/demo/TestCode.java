package com.example.demo;

import java.util.BitSet;

public class TestCode {
    private BitSet used = new BitSet();

    public TestCode(String str) {
        for (int i = 0; i < str.length(); i++)
            used.set(str.charAt(i));  // set bit for char
    }

    public String toString() {
        used.set(100000);
        String desc = "[";
        int size = used.size();
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            if (used.get(i))
                desc += (char) i;
        }
        return desc + "]";
    }

    public static void main(String args[]) {
        TestCode w = new TestCode("java WhichChars");
        System.out.println(w);
    }
}
