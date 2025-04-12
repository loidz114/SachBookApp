package Model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String price;
    private String imageUrl;
    private int quantity;
    private String category;

    public Book() {
        // Constructor rỗng để Firebase sử dụng
    }

    public Book(int id, String title, String author, String price, String imageUrl, int quantity, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.category = category;  // Khởi tạo thể loại
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {  // Getter cho thể loại
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCategory(String category) {  // Setter cho thể loại
        this.category = category;
    }
}
