package com.example.backgammon.Classes;

public class Piece {
    public Piece(int ID, int TYPE){
        this.id = ID;
        this.type = TYPE;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    private int id;
    private int type;
}
