package hello.typeconverter.formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("parse number, locale : {} {} ", text, locale);

        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        return numberFormat.parse(text);
    }


    @Override
    public String print(Number object, Locale locale) {
        log.info("print number, locale : {} {} ", object, locale);
        NumberFormat instance = NumberFormat.getInstance(locale);
        return instance.format(object);
    }
}
