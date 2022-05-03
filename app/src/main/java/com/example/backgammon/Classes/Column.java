package com.example.backgammon.Classes;

import java.util.*;

public class Column {
    public Column(int [][] pos,int column_number){

    }
    public void addToList(Piece p){
        this.ll.add(p);
    }

    public LinkedList<Piece> getList() {
        return this.ll;
    }

    private LinkedList<Piece> ll = new LinkedList<Piece>();
    private int type;
    private int[][] pos;
    private int column_number;

}
