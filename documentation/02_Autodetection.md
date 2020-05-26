# How InjectMaid creates instances

InjectMaid can automatically determine how a class will be instantiated.
It will consider public constructors and public static factory methods that are
declared in the class. InjectMaid will recursively detect and register all
classes that are needed to instantiate the class.
Examples for classes that InjectMaid can automatically detect:
 
## Public constructor
<!---[CodeSnippet](publicConstructor)-->
```java
public final class BookingPolicies {
    private final PolicyRepository policyRepository;

    public BookingPolicies(final PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    // ...
}
```

## Public static factory method
<!---[CodeSnippet](staticFactory)-->
```java
public final class BookFlightService {
    private final BookingRepository bookingRepository;

    private BookFlightService(final BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public static BookFlightService bookFlightService(final BookingRepository bookingRepository) {
        return new BookFlightService(bookingRepository);
    }

    // ...
}
```

## Annotations
To facilitate migration from traditional dependency injection frameworks, InjectMaid respects some commonly
used annotations. Note that InjectMaid will **never scan your classpath**. Therefore, it can only consider annotations
on classes it has already detected, either through manual registration or as a dependency of other types.

### `@Inject` and `@Autowired`
A detected class may contain exactly one `@Inject` or `@Autowired` annotation either on a public constructor or a public
static factory method. InjectMaid will then use the annotated constructor or static method to create instances
of that class. Considered are:
- `javax.inject.Inject` (JSR 330 / CDI)
- `com.google.inject.Inject` (Google Guice)
- `org.springframework.beans.factory.annotation.Autowired` (Spring Framework)

Example:
<!---[CodeSnippet](injectAnnotation)-->
```java
public final class FlightStatusService {
    private final UuidService uuidService;

    public FlightStatusService(final UuidService uuidService) {
        this.uuidService = uuidService;
    }

    @Inject
    public static FlightStatusService flightStatusService(final UuidService uuidService) {
        return new FlightStatusService(uuidService);
    }

    // ...
}
```

### `@Singleton`
All detected classes that carry an `@Singleton` annotation are automatically treated as
a (lazy) singleton. Considered are:
- `javax.inject.Singleton` (JSR 330 / CDI)
- `com.google.inject.Singleton` (Google Guice)

Example:
<!---[CodeSnippet](singletonAnnotation)-->
```java
@Singleton
public final class WeatherService {
    // ...
}
```
