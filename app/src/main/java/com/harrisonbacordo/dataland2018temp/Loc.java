package com.harrisonbacordo.dataland2018temp;

import java.util.ArrayList;
import java.util.Objects;

public class Loc {

    private double longCoord;
    private double latCoord;
    private ArrayList<Loc> neighbours;
    private boolean neighCheck;

    public Loc(double longCoord, double latCoord){
        this.longCoord = longCoord;
        this.latCoord = latCoord;
        this.neighbours = new ArrayList<>();
        this.neighCheck = true;
    }

    public double getLongCoord() {
        return longCoord;
    }

    public boolean isNeighCheck() {
        return neighCheck;
    }

    public void setNeighCheck(boolean neighCheck) {
        this.neighCheck = neighCheck;
    }

    public void setLongCoord(double longCoord) {
        this.longCoord = longCoord;
    }

    public double getLatCoord() {
        return latCoord;
    }

    public void setLatCoord(double latCoord) {
        this.latCoord = latCoord;
    }

    public ArrayList<Loc> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<Loc> neighbours) {
        this.neighbours = neighbours;
    }

    public void addNeighbour(Loc l){
        this.neighbours.add(l);
    }

    public int neighbourCount(){
        return this.neighbours.size();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Loc)) return false;
//        Loc loc = (Loc) o;
//        return Double.compare(loc.getLongCoord(), getLongCoord()) == 0 &&
//                Double.compare(loc.latCoord, latCoord) == 0;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getLongCoord(), latCoord);
//    }
}
