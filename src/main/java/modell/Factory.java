package modell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

class Production {
    private Optional produce;
    private Optional consume;
    private int duration;

    public Production(Optional<HashMap<String, Integer>> produce, Optional<HashMap<String, Integer>> consume, int duration) {
        if (produce.isPresent()) {
            this.produce = produce;
        }

        if (consume.isPresent()) {
            this.consume = consume;
        }
        this.duration = duration;
    }

    public void setProduce(Optional produce) {
        this.produce = produce;
    }

    public void setConsume(Optional consume) {
        this.consume = consume;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

public class Factory extends Building{

    private String name;
    private String special;
    private ArrayList<Production> productions;
    private Optional storage;
    private int dz;



    public Factory(String name, int width, int depth, String special, ArrayList<Production> productions, Optional<HashMap<String, Integer>> storage, int dz) {
        super(width,depth);
        this.name = name;
        this.special = special;
        this.productions = productions;
        if(storage.isPresent()) {
            this.storage = storage;
        }
        this.dz = dz;
    }
    public void produce(){
        // füge zur hashmap storage <kind, amount> den amount hinzu, der bei productions unter dem
        // kind steht. schauen ob der key (kind) vorhanden ist, wenn nicht, hinzufügen + amount hinzufügen, ansonsten amount erhöhen
    }

    public void consume(){
        //storage: amount der bei productions consume steht abziehen , falls vorhanden
        // wenn storage einen key hat, der übereinstimmt und der value zu einem key größer oder gleich
        // ist, abziehen, ansonsten nichts abziehen
    }



}
