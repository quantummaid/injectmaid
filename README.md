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