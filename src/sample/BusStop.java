package sample;

public class BusStop {

    private String name;
    private String numLine;
    private String direction;
    private String iconUri;

    public BusStop(String name, String numLine, String direction){
        this.name = name;
        this.numLine = numLine;
        this.direction = direction;
        this.iconUri = "/ressources/busicones/" + this.numLine + ".png";


    }


    public BusStop(String s){
        String[] commandSplit = s.split("/");
        this.name = commandSplit[1];
        this.numLine = commandSplit[2];
        this.direction = commandSplit[3];
        this.iconUri = "/ressources/busicones/" + this.numLine + ".png";

    }

    @Override
    public String toString() {
        return "ADD" + "/"+ name + "/" + numLine + "/" + direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BusStop)) {
            return false;
        }
        BusStop bs = (BusStop) o;
        return name.equals(bs.name) && numLine.equals(bs.numLine) && direction.equals(bs.direction);
    }

    public String getName() {
        return name;
    }
    public String getNumLine() {
        return numLine;
    }
    public String getDirection() {
        return direction;
    }
    public String getIconUri() { return iconUri; }


}
