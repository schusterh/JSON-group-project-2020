package modell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * This class represents factories. A factory is not a part of any buildmenu.
 * Every factory type gets built once onto the map at the start of the game.
 */
public class Factory extends Building{

    private String name;
    private String special;
    private ArrayList<Production> productions;
    private HashMap<String, Integer> storage;
    private HashMap<String, Integer> currentStorage;
    private int dz;
    private String prodMessage;


    /**
     * Instantiates a new Factory.
     *
     * @param name        the name
     * @param width       the width
     * @param depth       the depth
     * @param special     the special
     * @param productions the productions
     * @param storage     the storage
     * @param dz          the dz
     */
    public Factory(String name, int width, int depth, String special, ArrayList<Production> productions, Optional<HashMap<String, Integer>> storage, int dz) {
        super(width,depth, name);
        this.name = name;
        this.special = special;
        this.productions = productions;
        this.storage = storage.orElseGet(HashMap::new);
        this.dz = dz;
        this.currentStorage = new HashMap<>();
    }

    public String getName() { return name; }

    /**
     * Produce.
     */
    public void produce(Station near){
        if (near != null) System.out.println("Near station: " + near);
        // Einmal durch alle Produktionen durchiterieren
        for (Production production : productions) {
            //Wir betrachten nun eine Production. Zuerst müssen wir überprüfen, ob wir genug im storage haben,
            //um die Produktion ausführen zu können.

            boolean requirementsChecked = false;

            requirementsChecked = production.consume
                .map(this::consume)
                .orElseGet(() -> { return true; });

            if (requirementsChecked) {
                production.produce.ifPresent(products -> {
                    for (Map.Entry<String, Integer> product : products.entrySet()) {
                        if (near != null) {
                            System.out.println("Prodution added to Station!");
                            near.addGoods(new GoodsBundle(product.getKey(), product.getValue(),null));
                        }
                        this.prodMessage = "Production running!";
                    }
                });
            }
            else this.prodMessage = "Keine Ressourcen :(";
        }
        // füge zur hashmap storage <kind, amount> den amount hinzu, der bei productions unter dem
        // kind steht. schauen ob der key (kind) vorhanden ist, wenn nicht, hinzufügen + amount hinzufügen, ansonsten amount erhöhen
    }

    /**
     * Consume boolean.
     *
     * @param consumeRequirements the consume requirements
     * @return the boolean
     */
    public boolean consume(HashMap<String, Integer> consumeRequirements){
        //storage: amount der bei productions consume steht abziehen , falls vorhanden
        // wenn storage einen key hat, der übereinstimmt und der value zu einem key größer oder gleich
        // ist, abziehen, ansonsten nichts abziehen
        boolean requirementsChecked = true;

        if (!currentStorage.isEmpty()) {
            for (Map.Entry<String, Integer> requirement : consumeRequirements.entrySet()) {
                if (!currentStorage.containsKey(requirement.getKey()) || currentStorage.get(requirement.getKey()) < requirement.getValue()) {
                    System.out.println("Factory " + name + " missing " + requirement.getKey());
                    requirementsChecked = false;
                }
            }

            if (requirementsChecked) {
                for (Map.Entry<String, Integer> consumeEntity : consumeRequirements.entrySet()) {
                    currentStorage.put(consumeEntity.getKey(), currentStorage.get(consumeEntity.getKey()) - consumeEntity.getValue());
                }
            }
        } else {
            requirementsChecked = false;
        }
        return requirementsChecked;
    }

    /**
     * Gets storage.
     *
     * @return the storage
     */
    public HashMap<String, Integer> getStorage() {
        return storage;
    }

    /**
     * Gets current storage.
     *
     * @return the current storage
     */
    public HashMap<String, Integer> getCurrentStorage() {
        return currentStorage;
    }

    /**
     * Gets productions.
     *
     * @return the productions
     */
    public ArrayList<Production> getProductions() {
        return productions;
    }

    /**
     * Gets prod message.
     *
     * @return the prod message
     */
    public String getProdMessage() {
        return prodMessage;
    }
}
