package com.example.backgammon.Classes;

import java.util.*;

public class Column {
    public void addToList(Piece p){
        /*
        this function will add a piece to the column's list
        param: p : Piece
        return: void
         */
        this.pieces.add(p);
    }

    public LinkedList<Piece> getList() {
        /*
        this function will return the column's list
        param: p : Piece
        return: LinkedList<Piece>
         */
        return this.pieces;
    }

    private LinkedList<Piece> pieces = new LinkedList<Piece>();
}
