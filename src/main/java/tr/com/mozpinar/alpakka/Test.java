package tr.com.mozpinar.alpakka;

public class Test {
    public static void main(String[] args) {
        Review review = Review.newBuilder()
                .setProductName("book")
                .setUsername("mozpinar")
                .setMessage("Exciting!")
                .setRate(4)
                .build();
        System.out.println(review);
    }
}
