# Test Support

InjectMaid can help you modify your application's behaviour for tests. There are several ways to
overwrite existing definitions or tweak them to your needs.


## Overwriting definitions

Sometimes you cannot provide test definitions when the application is started.
To still be able to change e.g. a database to a mock implementation, you can re-define
the specific database binding in a separate InjectMaid instance.
You can then overwrite the existing InjectMaid instance with the new one.
After that, whenever the original InjectMaid wants to create an instance, it
will check whether the overwriting InjectMaid instance can provide that instance.
If this is the case, it will delegate the instance creation to the overwriting
InjectMaid instance.

Example:

<!---[CodeSnippet](overwriting)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
        .build();

final BookingRepository notOverwritten = injectMaid.getInstance(BookingRepository.class);

final InjectMaid overwriteInjectMaid = InjectMaid.anInjectMaid()
        .withImplementation(BookingRepository.class, MockedBookingRepository.class)
        .build();
injectMaid.overwriteWith(overwriteInjectMaid);

final BookingRepository overwritten = injectMaid.getInstance(BookingRepository.class);
```

## Intercepting instance creation
When overwriting is not enough, you can register an interceptor.
The interceptor will be called whenever InjectMaid creates an instance.
It can make arbitrary changes to the instance or even change it completely.

Example:

<!---[CodeSnippet](interception)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withImplementation(BookingRepository.class, InMemoryBookingRepository.class)
        .build();

final BookingRepository notIntercepted = injectMaid.getInstance(BookingRepository.class);

injectMaid.addInterceptor(object -> {
    if (object instanceof InMemoryBookingRepository) {
        return new WrappedBookingRepository((BookingRepository) object);
    }
    return object;
});

final BookingRepository intercepted = injectMaid.getInstance(BookingRepository.class);
```
