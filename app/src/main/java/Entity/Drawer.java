package Entity;

/**
 * Created by S.R Rain on 1/9/2016.
 */
public class Drawer {
    public String ItemType;
    public String Text;
    public String UserID;
    public int ImageID;

    public Drawer(String Text, int ImageID, String ItemType) {
        this.Text = Text;
        this.ImageID = ImageID;
        this.ItemType = ItemType;
    }

    public Drawer(String Text, String ItemType) {
        this.Text = Text;

        this.ItemType = ItemType;
    }

    public Drawer(String Text, String UserID, String ItemType) {
        this.Text = Text;
        this.UserID = UserID;

        this.ItemType = ItemType;
    }

    public Drawer() {
    }
}
