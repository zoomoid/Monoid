package tests;

import net.beadsproject.beads.core.AudioContext;
import synth.controller.Keys;

import java.util.Scanner;

public class KeyboardTest {

    public static void main(String[] args) {
        Keys keys =  new Keys();
        Scanner sc = new Scanner(System.in);
        String in = sc.nextLine();
        while(!in.equals("e")) {
            in = sc.nextLine();
        }
    }
}
