package fbrs.model;

public class EntryType {
    private int id;
    private String name;
    private int category;
    private String shortDesc;

    public EntryType(int id, String name, int category, String shortDesc) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.shortDesc = shortDesc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    @Override
    public String toString() {
        return getName();
    }
}
