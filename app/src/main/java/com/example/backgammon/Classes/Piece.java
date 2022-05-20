package com.example.backgammon.Classes;

public class Piece {
    public Piece(int ID, int TYPE){
        /*
        this function is the constructor of this class
        param: ID : int , TYPE : int
         */
        this.id = ID;
        this.type = TYPE;
    }

    public int getId() {
        /*
        this function will return the id of a piece
        param: none
        return: int
         */
        return id;
    }

    public int getType() {
        /*
        this function will return the type of a piece
        param: none
        return: int
         */
        return type;
    }

    private int id;
    private int type;
}
