package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

class ConversionServiceTest {

    @Test
    void convert() {

        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(new StringToIntegerConverter());
        defaultConversionService.addConverter(new IntegerToStringConverter());
        defaultConversionService.addConverter(new StringToIpPortConverter());
        defaultConversionService.addConverter(new IpPortToStringConverter());


        Integer result = defaultConversionService.convert("12", Integer.class);

        assertThat(result).isEqualTo(12);
        assertThat(defaultConversionService.convert(12, String.class)).isEqualTo("12");

        IpPort ipPort = defaultConversionService.convert("127.0.0.1:8080", IpPort.class);

        assertThat(ipPort.getIp()).isEqualTo("127.0.0.1");
        assertThat(ipPort.getPort()).isEqualTo(8080);
    }
}
