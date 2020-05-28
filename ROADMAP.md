# Project Roadmap

## alpha
- Improve stability 
- Validate usability in real projects
- Collect feature requests for advanced features
- Integrate with other QuantumMaid projects (probably HttpMaid)

## beta
- Implement features that arise in alpha
- Move documentation to https://quantummaid.de/
- Set InjectMaid as the default DI framework for the QuantumMaid framework
- Build strong test support:
    - Pre-request/per-test injections
    - Integrate with Junit5 and TestNG
    - Integrate with QuantumMaid testing
    - Design patterns for dependency overwrites in an architecturally clean way
- Support for GraalVM
- Evaluate feasibility of transparently replacing reflections with compile-time generated code to
  improve performance (see Google Dagger)

## stable
- Write and actively promote blog posts about InjectMaid, e.g. migration from other DI frameworks
