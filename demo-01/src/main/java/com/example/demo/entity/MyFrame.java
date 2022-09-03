package com.example.demo.entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MyFrame extends JFrame {
    JButton button;
    public MyFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        button=new JButton("Select File");
        this.pack();
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent evt){

        JFileChooser jFileChooser=new JFileChooser();
        jFileChooser.showSaveDialog(null);
    }
}
