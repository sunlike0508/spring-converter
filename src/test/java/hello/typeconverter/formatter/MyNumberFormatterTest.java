package hello.typeconverter.formatter;

import java.text.ParseException;
import java.util.Locale;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class MyNumberFormatterTest {

    MyNumberFormatter myNumberFormatter = new MyNumberFormatter();


    @Test
    void format() throws ParseException {

        Number number = myNumberFormatter.parse("100,000", Locale.KOREA);

        assertThat(number).isEqualTo(100000L);
    }


    @Test
    void pirnt() {
        String result = myNumberFormatter.print(1000, Locale.KOREA);

        assertThat(result).isEqualTo("1,000");
    }
}