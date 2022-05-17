package com.example.backgammon.Classes;

import java.util.*;

public class Column {
    public Column(){}
    public void addToList(Piece p){
        this.pieces.add(p);
    }

    public LinkedList<Piece> getList() {
        return this.pieces;
    }

    private LinkedList<Piece> pieces = new LinkedList<Piece>();
}
