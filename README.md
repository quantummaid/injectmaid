[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=security_rating)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=bugs)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=code_smells)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=sqale_index)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.quantummaid.injectmaid%3Ainjectmaid&metric=coverage)](https://sonarcloud.io/dashboard?id=de.quantummaid.injectmaid%3Ainjectmaid)
[![Last Commit](https://img.shields.io/github/last-commit/quantummaid/injectmaid)](https://github.com/quantummaid/injectmaid)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.injectmaid/injectmaid/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.quantummaid.injectmaid/injectmaid)
[![Code Size](https://img.shields.io/github/languages/code-size/quantummaid/injectmaid)](https://github.com/quantummaid/injectmaid)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Slack](https://img.shields.io/badge/chat%20on-Slack-brightgreen)](https://quantummaid.de/community.html)
[![Gitter](https://img.shields.io/badge/chat%20on-Gitter-brightgreen)](https://gitter.im/quantum-maid-framework/community)
[![Twitter](https://img.shields.io/twitter/follow/quantummaid)](https://twitter.com/quantummaid)

<img src="quantummaid_logo.png" align="left"/>

# InjectMaid
InjectMaid is a lightweight dependency injection framework that does not rely on annotations.

Features:
- Injection via public constructor or static factory method
- Does not call non-public methods nor set non-public fields
- Support for singletons (lazy and eager initialization)
- Generics are fully supported
- Optionally respects `@Inject` and `@Singleton` annotations
- Advanced scoping
- Circular dependency detection
- No expensive classpath scanning

Coming soon:
- Pre-compiled reflections
- Support for GraalVM

Limitations:
- No support for field and setter injection

## Getting started
The InjectMaid documentation can be found [here](./documentation/01_Usage.md).

## Get in touch
Feel free to join us on [Slack](https://quantummaid.de/community.html)
or [Gitter](https://gitter.im/quantum-maid-framework/community) to ask questions, give feedback or just discuss software
architecture with the team behind HttpMaid. Also, don't forget to visit our [website](https://quantummaid.de) and follow
us on [Twitter](https://twitter.com/quantummaid)!

## Frequently asked questions

### Can I use InjectMaid without reflection?
Yes. You can easily [register all types directly](documentation/03_CustomInstantiation.md) and InjectMaid
will not perform a single reflective call.

### Is there anything wrong with annotations?
No. But like any other concept, there are downsides.
InjectMaid leaves the choice whether to use them to you. You can configure InjectMaid
with annotations, but you don't have to.

However, InjectMaid is part of the larger [QuantumMaid application framework](https://quantummaid.de/index.html). QuantumMaid is designed around the concept of
clean architecture [as defined by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html).
Here, developers are encouraged to strictly keep infrastructure code separate from business logic (or *domain code* in Domain-Driven Design):

![Clean Architecture](https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)

This way, infrastructure aspects like databases and public APIs (REST, etc.) are mere replaceable details.
Directly annotating business logic with infrastructure annotations like those of [JAX-RS](https://en.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services),
[CDI](https://docs.oracle.com/javaee/6/tutorial/doc/giwhl.html), [JSON-B](https://javaee.github.io/jsonb-spec/users-guide.html), 
[JSR 303](https://beanvalidation.org/1.0/spec/), [JPA](https://en.wikipedia.org/wiki/Java_Persistence_API), etc.
removes this strict separation.
A common workaround is the creation of wrapper classes whose sole purpose is to carry the
infrastructure annotations (controllers, JSON models, etc.). This might be feasible in some cases but introduces a lot
of boilerplate overhead.

Another downside to classical annotation processing is its implication on application startup time.
A classical JEE container needs to scan all classes in a newly deployed application for annotations to determine its configuration.
This is inherently slow and leads to long initialization phases.
With the introduction of highly competitive serverless hosting options like
[AWS Lambda](https://aws.amazon.com/lambda/), that issue becomes a crucial problem. 
It can be partially solved with compile-time annotation processing. Examples for projects that follow this approach are
[Google Dagger](https://dagger.dev/), [Quarkus](https://quarkus.io/) and [Micronaut](https://micronaut.io/).
QuantumMaid offers an alternative approach by avoiding configuration by annotations altogether.

Here are additional (independent) blogs with a similar point of view:
 - [https://blog.softwaremill.com/the-case-against-annotations-4b2fb170ed67]()
 - [https://medium.com/@vincent.maurin.fr/java-annotations-and-oop-a2633f3692fb]()
 
Another Java application framework that follows an approach without annotations is the [Vlingo Platform](https://vlingo.io/).
It is actively maintained by Vaughn Vernon, the author of the
book [Implementing Domain-Driven Design](https://www.oreilly.com/library/view/implementing-domain-driven-design/9780133039900/). 
