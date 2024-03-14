package model;

/**
 * @author Martin Ramonda
 */
public class Category {
    private String catName;
    private int catId;

    public Category() {
    }

    public Category(String catName, int catId) {
        this.catName = catName;
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }
}