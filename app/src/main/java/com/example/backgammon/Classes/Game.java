package com.example.backgammon.Classes;

import java.util.LinkedList;

public class Game {

    public Game(int[][][] pos,LinkedList<Integer> ll){
        this.columns = new Column[24];
        for (int i=0;i< pos.length;i++){
            Column column = new Column(pos[i],i+1);
            this.columns[i] = column;
        }
        boardSetUp(ll);
    }
    private void boardSetUp(LinkedList<Integer> ll){
        this.columns[0].addToList(new Piece(ll.get(0),0));
        this.columns[0].addToList(new Piece(ll.get(1),0));
        this.columns[5].addToList(new Piece(ll.get(15),1));
        this.columns[5].addToList(new Piece(ll.get(16),1));
        this.columns[5].addToList(new Piece(ll.get(17),1));
        this.columns[5].addToList(new Piece(ll.get(18),1));
        this.columns[5].addToList(new Piece(ll.get(19),1));
        this.columns[7].addToList(new Piece(ll.get(20),1));
        this.columns[7].addToList(new Piece(ll.get(21),1));
        this.columns[7].addToList(new Piece(ll.get(22),1));
        this.columns[11].addToList(new Piece(ll.get(2),0));
        this.columns[11].addToList(new Piece(ll.get(3),0));
        this.columns[11].addToList(new Piece(ll.get(4),0));
        this.columns[11].addToList(new Piece(ll.get(5),0));
        this.columns[11].addToList(new Piece(ll.get(6),0));
        this.columns[12].addToList(new Piece(ll.get(23),1));
        this.columns[12].addToList(new Piece(ll.get(24),1));
        this.columns[12].addToList(new Piece(ll.get(25),1));
        this.columns[12].addToList(new Piece(ll.get(26),1));
        this.columns[12].addToList(new Piece(ll.get(27),1));
        this.columns[16].addToList(new Piece(ll.get(7),0));
        this.columns[16].addToList(new Piece(ll.get(8),0));
        this.columns[16].addToList(new Piece(ll.get(9),0));
        this.columns[18].addToList(new Piece(ll.get(10),0));
        this.columns[18].addToList(new Piece(ll.get(11),0));
        this.columns[18].addToList(new Piece(ll.get(12),0));
        this.columns[18].addToList(new Piece(ll.get(13),0));
        this.columns[18].addToList(new Piece(ll.get(14),0));
        this.columns[23].addToList(new Piece(ll.get(28),1));
        this.columns[23].addToList(new Piece(ll.get(29),1));

    }
    public Column getAcolumn(int index){
        return this.columns[index];
    }
    private Column[] columns;
}
