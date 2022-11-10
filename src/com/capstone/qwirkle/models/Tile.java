package com.capstone.qwirkle.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Tile implements Serializable {
    private static final long serialVersionUID = 70L;
    public enum Colour{ PURPLE,BLUE,GREEN,YELLOW,ORANGE,RED}

    public enum Shape{  SQUARE, CIRCLE, STAR, DIAMOND, CROSS, CLUB;}

    public enum State{ IN_HAND,PLACED,PLACING,IN_BAG,SWAPPING;
    }

    public int col,row;
    public Shape Shape;
    public Colour Colour;
    public State state;
    public ArrayList<Tile> verticalLine = new ArrayList<>();
    public ArrayList<Tile> horizontalLine = new ArrayList<>();
    public boolean isSelected = false;
    public boolean checkNS = false;
    public boolean checkEW = false;
    public boolean counted = false;
    public boolean swapped = false;

    public boolean isSwapped() {
        return swapped;
    }

    public void setSwapped(boolean swapped) {
        this.swapped = swapped;
    }

    public Tile(Colour colour, Shape shape){
        this.Colour = colour;
        this.Shape = shape;
    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }

    public boolean isCounted() {
        return counted;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Tile.Shape getShape() {
        return Shape;
    }

    public void setShape(Tile.Shape shape) {
        Shape = shape;
    }

    public Tile.Colour getColour() {
        return Colour;
    }

    public void setColour(Tile.Colour colour) {
        Colour = colour;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setLinesToNull() {
        horizontalLine = null;
        verticalLine = null;
    }
}
