# Custom Instantiation

Whenever InjectMaid cannot automatically determine how to instantiate a class you can still
register it as a custom type.
Example:
<!---[CodeSnippet](zeroArgsFactory)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withCustomType(RegulatoryDomain.class, () -> RegulatoryDomain.regulatoryDomainFor("European Union"))
        .build();
final RegulatoryDomain regulatoryDomain = injectMaid.getInstance(RegulatoryDomain.class);
```

If you need any dependencies for the custom instantiation just add their types before
the factory method and InjectMaid will provide them for you:

<!---[CodeSnippet](customTypesInline)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withCustomType(AdvancedBookingService.class, FlightStatusService.class, WeatherService.class, UuidService.class,
                (flightStatusService, weatherService, uuidService) -> {
                    final AdvancedBookingService service = new AdvancedBookingService(flightStatusService, weatherService);
                    service.setUuidService(uuidService);
                    return service;
                })
        .build();
final AdvancedBookingService bookingService = injectMaid.getInstance(AdvancedBookingService.class);
```

Alternatively, you can use a fluent builder API:

<!---[CodeSnippet](customTypes)-->
```java
final InjectMaid injectMaid = InjectMaid.anInjectMaid()
        .withCustomType(
                CustomType.customType(AdvancedBookingService.class)
                        .withDependency(FlightStatusService.class)
                        .withDependency(WeatherService.class)
                        .withDependency(UuidService.class)
                        .usingFactory((flightStatusService, weatherService, uuidService) -> {
                            final AdvancedBookingService service = new AdvancedBookingService(flightStatusService, weatherService);
                            service.setUuidService(uuidService);
                            return service;
                        })
        )
        .build();
final AdvancedBookingService bookingService = injectMaid.getInstance(AdvancedBookingService.class);
```
