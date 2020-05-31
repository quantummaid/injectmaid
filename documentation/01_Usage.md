# Usage

## Maven dependency
```xml
<dependency>
    <groupId>de.quantummaid.injectmaid</groupId>
    <artifactId>injectmaid</artifactId>
    <version>0.0.2</version>
</dependency>
```

## Registering types for injection
<!---[CodeSnippet](basicUsage)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withType(FlightStatusService.class)
        .build();
final FlightStatusService flightStatusService = injectMaid.getInstance(FlightStatusService.class);
```

## Binding an interface to an implementation
<!---[CodeSnippet](bindInterface)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
        .build();
final BookingRepository bookingRepository = injectMaid.getInstance(BookingRepository.class);
```

## Binding to a constant
<!---[CodeSnippet](constants)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withConstant(BookingRepository.class, BOOKING_REPOSITORY)
        .build();
final BookingRepository bookingRepository = injectMaid.getInstance(BookingRepository.class);
```

## Singletons
You can turn any registered type into a singleton by adding `ReusePolicy.SINGLETON` as the last parameter:
<!---[CodeSnippet](singletons)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withType(BookingPolicies.class, ReusePolicy.SINGLETON)
        .build();
final BookingPolicies bookingPolicies = injectMaid.getInstance(BookingPolicies.class);
```
`ReusePolicy.SINGLETON` will register the type as a lazy singleton. If you need to register an
eager singleton, use `ReusePolicy.EAGER_SINGLETON` instead:

<!---[CodeSnippet](eagerSingletons)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withType(BookingPolicies.class, ReusePolicy.EAGER_SINGLETON)
        .build();
final BookingPolicies bookingPolicies = injectMaid.getInstance(BookingPolicies.class);
```
[Read the chapter about scopes](05_Scopes.md) to see how scoped singletons work in InjectMaid.

## External factory

## Modules
You can group common configuration options into a module:
<!---[CodeSnippet](module)-->
```java
public final class BookingModule implements InjectMaidModule {

    @Override
    public void apply(final InjectMaidBuilder builder) {
        builder
                .withType(BookFlightService.class)
                .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
                .withType(BookingPolicies.class, ReusePolicy.SINGLETON);
    }
}
```

It can be applied to an InjectMaid builder like this:
<!---[CodeSnippet](moduleUsage)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withModule(new BookingModule())
        .build();
final BookFlightService bookFlightService = injectMaid.getInstance(BookFlightService.class);
```