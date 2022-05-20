package com.example.backgammon.Classes;

import java.util.LinkedList;

public class Game {

    public Game(int[][][] pos,LinkedList<Integer> ll){
        /*
        this function is the constructor of this class and it will set up columns
        param: pos : int[][][], ll : LinkedList<Integer>
         */
        this.columns = new Column[24];
        for (int i=0;i< pos.length;i++){
            Column column = new Column();
            this.columns[i] = column;
        }
        boardSetUp(ll);
    }
    public boolean checkIfColumnIsEmpty(int colNum){
        /*
        this function will check if a column has no pieces
        param: colNum : int
        return: boolean
         */
        return !(this.columns[colNum-1].getList().size() > 0);
    }
    public Piece move(int colNum1, int colNum2){
        /*
        this function will move a piece from one column to another
        param: colNum1 : int , colNum2 : int
        return: Piece
         */
        Piece p = this.columns[colNum1-1].getList().getLast();
        this.columns[colNum1-1].getList().removeLast();
        this.columns[colNum2-1].getList().add(p);
        return p;
    }
    private void boardSetUp(LinkedList<Integer> ll){
        /*
        this function will set the board in the way it should look like.
        param: ll : LinkedList<Integer>
        return: void
         */
        this.columns[0].addToList(new Piece(ll.get(0),1));
        this.columns[0].addToList(new Piece(ll.get(1),1));
        this.columns[5].addToList(new Piece(ll.get(15),0));
        this.columns[5].addToList(new Piece(ll.get(16),0));
        this.columns[5].addToList(new Piece(ll.get(17),0));
        this.columns[5].addToList(new Piece(ll.get(18),0));
        this.columns[5].addToList(new Piece(ll.get(19),0));
        this.columns[7].addToList(new Piece(ll.get(20),0));
        this.columns[7].addToList(new Piece(ll.get(21),0));
        this.columns[7].addToList(new Piece(ll.get(22),0));
        this.columns[11].addToList(new Piece(ll.get(2),1));
        this.columns[11].addToList(new Piece(ll.get(3),1));
        this.columns[11].addToList(new Piece(ll.get(4),1));
        this.columns[11].addToList(new Piece(ll.get(5),1));
        this.columns[11].addToList(new Piece(ll.get(6),1));
        this.columns[12].addToList(new Piece(ll.get(23),0));
        this.columns[12].addToList(new Piece(ll.get(24),0));
        this.columns[12].addToList(new Piece(ll.get(25),0));
        this.columns[12].addToList(new Piece(ll.get(26),0));
        this.columns[12].addToList(new Piece(ll.get(27),0));
        this.columns[16].addToList(new Piece(ll.get(7),1));
        this.columns[16].addToList(new Piece(ll.get(8),1));
        this.columns[16].addToList(new Piece(ll.get(9),1));
        this.columns[18].addToList(new Piece(ll.get(10),1));
        this.columns[18].addToList(new Piece(ll.get(11),1));
        this.columns[18].addToList(new Piece(ll.get(12),1));
        this.columns[18].addToList(new Piece(ll.get(13),1));
        this.columns[18].addToList(new Piece(ll.get(14),1));
        this.columns[23].addToList(new Piece(ll.get(28),0));
        this.columns[23].addToList(new Piece(ll.get(29),0));
    }

    public LinkedList<Piece> getBLueEaten() {
        /*
        this function will return a list with the blue pieces that have been eaten
        param: none
        return: LinkedList<Piece>
         */
        return BLueEaten;
    }

    public LinkedList<Piece> getRedEaten() {
        /*
        this function will return a list with the red pieces that have been eaten
        param: none
        return: LinkedList<Piece>
         */
        return RedEaten;
    }

    public Column getAcolumn(int index){
        /*
        this function will return a column
        param: index : int
        returnL Column
         */
        return this.columns[index-1];
    }

    private Column[] columns;
    private LinkedList<Piece> RedEaten = new LinkedList<>();
    private LinkedList<Piece> BLueEaten = new LinkedList<>();
}
