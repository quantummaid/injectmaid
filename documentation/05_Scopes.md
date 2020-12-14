# Scopes
InjectMaid supports scopes.
To use a scope, you need to create a scope type for it. If you want to create e.g. a request scope, you could
create a `Request` class like this:

<!---[CodeSnippet](request)-->
```java
public final class Request {
    private final String username;

    public Request(final String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
```

The scope type can be any class with any structure you like. 
You can now use this class to define a scope:

<!---[CodeSnippet](scopeDefinition)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withScope(Request.class, scope -> {
            scope.withType(BookFlightService.class);
            scope.withImplementation(BookingRepository.class, InMemoryBookingRepository.class);
        })
        .build();
```


To enter a scope, you need to create an instance of the scope type:

<!---[CodeSnippet](scopes)-->
```java
final Request request1 = new Request("elsa");
final Injector request1Injector = injectMaid.enterScope(Request.class, request1);
final BookFlightService bookFlightService1 = request1Injector.getInstance(BookFlightService.class);

final Request request2 = new Request("olaf");
final Injector request2Injector = injectMaid.enterScope(Request.class, request2);
final BookFlightService bookFlightService2 = request2Injector.getInstance(BookFlightService.class);
```

All types registered inside a scope can get the scope object injected.
The type just needs to declare the scope type as a dependency just like you would declare any other dependency:

<!---[CodeSnippet](scopeObjectInjection)-->
```java
public final class User {
    private final String username;

    private User(final String username) {
        this.username = username;
    }

    public static User fromRequest(final Request request) {
        final String username = request.getUsername();
        return new User(username);
    }

    // ...
}
```


This way, you can easily share scope-specific data like request headers or authentication data
with all objects instantiated inside a scope.

## Singletons in scopes

Singletons are only unique inside the scope they have been defined in.
This way, you can easily create common concepts like request-scoped singletons. 
Example:
<!---[CodeSnippet](scopedSingletons)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        // global singleton:
        .withType(UuidService.class, ReusePolicy.DEFAULT_SINGLETON)
        .withScope(Request.class, scope -> {
            // request-scoped singleton:
            scope.withType(BookingPolicies.class, ReusePolicy.DEFAULT_SINGLETON);
        })
        .build();

final Request request1 = new Request("elsa");
final Injector request1Injector = injectMaid.enterScope(Request.class, request1);
final UuidService uuidService1 = request1Injector.getInstance(UuidService.class);
final BookingPolicies bookingPolicies1 = request1Injector.getInstance(BookingPolicies.class);

final Request request2 = new Request("olaf");
final Injector request2Injector = injectMaid.enterScope(Request.class, request2);
final UuidService uuidService2 = request1Injector.getInstance(UuidService.class);
final BookingPolicies bookingPolicies2 = request2Injector.getInstance(BookingPolicies.class);

// global singletons are the same in both scopes
assert uuidService1 == uuidService2;

// request-scoped singletons are different in each scope
assert bookingPolicies1 != bookingPolicies2;
```
