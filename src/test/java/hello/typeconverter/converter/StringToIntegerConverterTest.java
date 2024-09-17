package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class StringToIntegerConverterTest {

    @Test
    void convert() {

        final StringToIntegerConverter converter = new StringToIntegerConverter();

        System.out.println(converter.convert("123"));

        assertThat(converter.convert("123")).isEqualTo(123);

        final IntegerToStringConverter converter2 = new IntegerToStringConverter();

        System.out.println(converter2.convert(123));

        assertThat(converter2.convert(123)).isEqualTo("123");
    }


    @Test
    void StringToIpPortConverter() {
        StringToIpPortConverter converter = new StringToIpPortConverter();

        assertThat(converter.convert("127.0.0.1:8080")).isEqualTo(new IpPort("127.0.0.1", 8080));
    }


    @Test
    void PortConverter() {
        IpPortToStringConverter converter = new IpPortToStringConverter();

        assertThat(converter.convert(new IpPort("127.0.0.1", 8080))).isEqualTo("127.0.0.1:8080");
    }
}