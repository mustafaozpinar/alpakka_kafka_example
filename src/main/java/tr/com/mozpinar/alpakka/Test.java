package tr.com.mozpinar.alpakka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Test {
    public static void main(String[] args) throws DecoderException, InvalidProtocolBufferException {
        Review review = Review.newBuilder()
                .setProductName("book")
                .setUsername("mozpinar")
                .setMessage("Exciting!")
                .setRate(4)
                .build();
        System.out.println(review);

        String data = "0A 08 62 6F 6F 6B 2D 32 30 37 12 0C 6D 6F 7A 70 69 6E 61 72 2D 32 30 37 1A 0D 45 78 63 69 \n" +
                "74 69 6E 67 21 2D 32 30 37 20 04 ";
        data = data.replaceAll(" ", "").replaceAll("\n", "");
        byte[] bytes = Hex.decodeHex(data);
        Review review1 = Review.parseFrom(bytes);
        System.out.println(review1.toString());
    }
}
