package Entity;

/**
 * Created by iBaax on 2/8/16.
 */
public class Dictunary {

    public int ID;
    public String Title;
    public String Name;
    public String ShortCuts;
    public String URL;
    public boolean IsSelected = true;

    public Dictunary() {

    }

    public Dictunary(int ID, String Title, boolean IsSelected) {
        this.ID = ID;
        this.Title = Title;
        this.IsSelected = IsSelected;

    }

    public Dictunary(int ID, String Title, String Name) {
        this.ID = ID;
        this.Title = Title;
        this.Name = Name;
    }

    public Dictunary(int ID, String Title, String ShortCuts, String Name) {
        this.ID = ID;
        this.Title = Title;
        this.ShortCuts = ShortCuts;
        this.Name = Name;
    }
}


