# Technology Stack

**Analysis Date:** 2026-04-21

## Languages

**Primary:**
- Java 8 (1.8) - Main programming language for the entire codebase

## Runtime

**Environment:**
- Java Runtime Environment (JRE) 8+

**Package Manager:**
- Maven 3.x
- Lockfile: Not applicable (Maven uses pom.xml)

## Frameworks

**Core:**
- Java AWT (Abstract Window Toolkit) - Graphics rendering and image manipulation
- Lombok 1.18.30 - Code generation and boilerplate reduction

**Testing:**
- JUnit 4.12 - Unit testing framework

## Key Dependencies

**Critical:**
- `cn.augrain:easy-tool:0.0.2` - Utility library for color and other helper functions
- `org.projectlombok:lombok:1.18.30` - Reduces boilerplate code (getters, setters, etc.)
- `com.google.zxing:core:3.5.3` - QR code generation and processing

**Build/Dev:**
- Maven Compiler Plugin 3.8.1 - Java compilation
- Maven Source Plugin 3.1.0 - Source code packaging
- Maven Javadoc Plugin 3.1.0 - Documentation generation
- Maven GPG Plugin 3.2.7 - Artifact signing
- Sonatype Central Publishing Plugin 0.4.0 - Maven Central deployment

## Configuration

**Build:**
- `pom.xml` - Maven project configuration with Java 1.8 source/target compatibility
- `.idea/` - IntelliJ IDEA IDE configuration files

## Platform Requirements

**Development:**
- Java JDK 8 or higher
- Maven 3.6+
- IntelliJ IDEA (recommended based on IDE files present)

**Production:**
- Java JRE 8+
- Deployed as JAR library to Maven Central

---

*Stack analysis: 2026-04-21*