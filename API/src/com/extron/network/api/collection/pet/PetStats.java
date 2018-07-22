package com.extron.network.api.collection.pet;

import com.extron.network.api.utils.JsonContainer;

public class PetStats {

    private int age;
    private int health;
    private int hunger;
    private int happiness;
    public PetStats(JsonContainer data) {
        this.age = data.getInt("age",0);
        this.health = data.getInt("health",50);
        this.hunger = data.getInt("hunger",10);
        this.happiness = data.getInt("happiness",30);
    }

    public int getAge() {
        return age;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getHealth() {
        return health;
    }

    public int getHunger() {
        return hunger;
    }

    public JsonContainer saveToJson() {
        JsonContainer json = new JsonContainer();
        json.set("age",age);
        json.set("health",health);
        json.set("hunger",hunger);
        json.set("happiness",happiness);
        return json;
    }
}
