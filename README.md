# Converter

**컨버터 인터페이스**

```java
package org.springframework.core.convert.converter;

public interface Converter<S, T> {

    T convert(S source);
}
````

**참고**
과거에는 `PropertyEditor` 라는 것으로 타입을 변환했다.

`PropertyEditor` 는 동시성 문제가 있어서 타입 을 변환할 때 마다 객체를 계속 생성해야 하는 단점이 있다.

지금은 `Converter` 의 등장으로 해당 문제들이 해결 되었고, 기능 확장이 필요하면 `Converter` 를 사용하면 된다.

### 숫자에서 문자

```java
 public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}

```

**참고**

스프링은 용도에 따라 다양한 방식의 타입 컨버터를 제공한다.

`Converter` 기본 타입 컨버터

`ConverterFactory` 전체 클래스 계층 구조가 필요할 때 `GenericConverter` 정교한 구현, 대상 필드의 애노테이션 정보 사용 가능

`ConditionalGenericConverter` 특정 조건이 참인 경우에만 실행
자세한 내용은 공식 문서를 참고하자. https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core-convert

**참고**

스프링은 문자, 숫자, 불린, Enum등 일반적인 타입에 대한 대부분의 컨버터를 기본으로 제공한다.

IDE에서 `Converter` , `ConverterFactory` , `GenericConverter` 의 구현체를 찾아보면 수 많은 컨버터를 확인할 수 있다.

### ConversionService

```java
 package org.springframework.core.convert;

import org.springframework.lang.Nullable;

public interface ConversionService {

    boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);

    boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

    <T> T convert(@Nullable Object source, Class<T> targetType);

    Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

**인터페이스 분리 원칙 - ISP(Interface Segregation Principle)**

인터페이스 분리 원칙은 클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야 한다.

`DefaultConversionService` 는 다음 두 인터페이스를 구현했다.

`ConversionService` : 컨버터 사용에 초점

`ConverterRegistry` : 컨버터 등록에 초점

이렇게 인터페이스를 분리하면 컨버터를 사용하는 클라이언트와 컨버터를 등록하고 관리하는 클라이언트의 관심사를 명확하게 분리할 수 있다.

특히 컨버터를 사용하는 클라이언트는 `ConversionService` 만 의존하면 되므로, 컨버터를 어떻게 등록하고 관리하는지는 전혀 몰라도 된다.

결과적으로 컨버터를 사용하는 클라이언트는 꼭 필요한 메서드만 알게된다.

이렇게 인터페이스를 분리하는 것을 `ISP` 라 한다.

### 스프링에 컨버터 등록

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());
    }
}
```

**처리 과정**

`@RequestParam` 은 `@RequestParam` 을 처리하는 `ArgumentResolver` 인 `RequestParamMethodArgumentResolver` 에서
`ConversionService` 를 사용해서 타입을 변환한다.

부모 클래스와 다양한 외부 클래스를 호출하는 등 복잡한 내부 과정을 거치기 때문에 대략 이렇게 처리되는 것으로 이해해도 충 분하다.

만약 더 깊이있게 확인하고 싶으면 `IpPortConverter` 에 디버그 브레이크 포인트를 걸어서 확인해보자.

# Formatter

**웹 애플리케이션에서 객체를 문자로, 문자를 객체로 변환하는 예**

화면에 숫자를 출력해야하는데, `Integer` `String` 출력시점에숫자 `1000` 문자 `"1,000"` 이렇게 1000 단위에 쉼표를 넣어서 출력하거나,

또는 `"1,000"` 라는 문자를 `1000` 이라는 숫자로 변경해야 한다.

날짜 객체를 문자인 `"2021-01-01 10:50:11"` 와 같이 출력하거나 또는 그 반대의 상황

**Converter vs Formatter**

`Converter` 는 범용(객체 객체)

`Formatter` 는 문자에 특화(객체 문자, 문자 객체) + 현지화(Locale) `Converter` 의 특별한 버전

**Formatter 인터페이스**

`String print(T object, Locale locale)` : 객체를 문자로 변경한다.

`T parse(String text, Locale locale)` : 문자를 객체로 변경한다.

```java
public interface Printer<T> {

    String print(T object, Locale locale);
}


public interface Parser<T> {

    T parse(String text, Locale locale) throws ParseException;
}


public interface Formatter<T> extends Printer<T>, Parser<T> {}
```

## 포맷터를 지원하는 컨버젼 서비스

포맷터를 지원하는 컨버전 서비스를 사용하면 컨버전 서비스에 포맷터를 추가할 수 있다.

내부에서 어댑터 패턴을 사용 해서 `Formatter` 가 `Converter` 처럼 동작하도록 지원한다.

`FormattingConversionService` 는 포맷터를 지원하는 컨버전 서비스이다.

`DefaultFormattingConversionService` 는 `FormattingConversionService` 에 기본적인 통화, 숫자 관 련 몇가지 기본 포맷터를 추가해서 제공한다.

**DefaultFormattingConversionService 상속 관계**

`FormattingConversionService` 는 `ConversionService` 관련 기능을 상속받기 때문에 결과적으로 컨버터도 포맷터도 모두 등록할 수 있다.

그리고 사용할 때는 `ConversionService` 가 제공하는 `convert` 를 사용하면 된다.

추가로 스프링 부트는 `DefaultFormattingConversionService` 를 상속 받은 `WebConversionService` 를 내부에서 사용한다.

**등록**

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        //registry.addConverter(new StringToIntegerConverter());
        //registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new IpPortToStringConverter());
        registry.addConverter(new StringToIpPortConverter());

        registry.addFormatter(new MyNumberFormatter());
    }
}

```

## 스프링에서 제공하는 기본 포맷터

스프링은 자바에서 기본으로 제공하는 타입들에 대해 수 많은 포맷터를 기본으로 제공한다.

그러나 포맷터는 기본 형식이 지정되어 있기 때문에, 객체의 각 필드마다 다른 형식으로 포맷을 지정하기는 어렵다.

스프링은 이런 문제를 해결하기 위해 애노테이션 기반으로 원하는 형식을 지정해서 사용할 수 있는 매우 유용한 포맷터 두 가지를 기본으로 제공한다.

`@NumberFormat` : 숫자 관련 형식 지정 포맷터 사용, `NumberFormatAnnotationFormatterFactory`

`@DateTimeFormat` : 날짜 관련 형식 지정 포맷터 사용, `Jsr310DateTimeFormatAnnotationFormatterFactory`

```java

@Data
static class Form {

    @NumberFormat(pattern = "###,###")
    private Integer number;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
}
```

**주의!**

메시지 컨버터( `HttpMessageConverter` )에는 컨버전 서비스가 적용되지 않는다.

특히 객체를 JSON으로 변환할 때 메시지 컨버터를 사용하면서 이 부분을 많이 오해하는데, `HttpMessageConverter` 의 역할은 HTTP 메시지 바디의 내용을 객체로 변환하거나 객체를 HTTP 메시지
바디에 입력하는 것이다.

예를 들어서 JSON을 객체로 변환하는 메시지 컨버터는 내부에서 Jackson 같은 라이브러리를 사용한다.

객체를 JSON으로 변환한다면 그 결과는 이 라이브러리에 달린 것이다.

따라서 JSON 결과로 만들어지는 숫자나 날짜 포맷을 변경하고 싶으면 해당 라이브러리가 제공하는 설정을 통해서 포맷을 지정해야 한다.

결과적으로 이것은 컨 버전 서비스와 전혀 관계가 없다.

컨버전 서비스는 `@RequestParam` , `@ModelAttribute` , `@PathVariable` , 뷰 템플릿 등에서 사용할 수 있다.















