package codeowlcove.codeowl_twitchplays_backend.AnimalRacing;

public class Animal {
    private String name;
    private int id;
    private AnimalState state;
    private int progress;

    String[] horseNames = {"Shadow", "Max", "Luna", "Bailey", "Duke", "Bella", "Rocky", "Daisy", "Scout", "Coco", "Charlie", "Rosie", "Tucker", "Molly", "Zeus", "Sadie", "Rusty", "Ginger", "Scout", "Willow"};

    public Animal(int id){
        this.name = horseNames[(int) (Math.random() * horseNames.length)];
        this.id = id;
        this.state = AnimalState.IDLE;
        this.progress = 0;
    }

    // Constructor
    public Animal(String name, int id, int progress) {
        this.name = name;
        this.id = id;
        this.state = AnimalState.IDLE;
        this.progress = progress;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public AnimalState getState() {
        return state;
    }

    public void setState(AnimalState state) {
        this.state = state;
    }
}