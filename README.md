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



























