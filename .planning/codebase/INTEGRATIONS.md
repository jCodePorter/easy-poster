# External Integrations

**Analysis Date:** 2026-04-21

## APIs & External Services

**Graphics & Imaging:**
- Java AWT (Abstract Window Toolkit) - Built-in Java graphics library for image rendering
- `javax.imageio.ImageIO` - Image reading and writing capabilities

**QR Code Generation:**
- Google ZXing ("Zebra Crossing") 3.5.3 - QR code and barcode generation library
  - SDK/Client: `com.google.zxing:core`
  - Usage: QR code element generation within posters

## Data Storage

**Databases:**
- None - This is a graphics library, not a data storage system

**File Storage:**
- Local filesystem - For loading source images and saving generated posters
- Image formats: PNG, JPEG, and other formats supported by Java ImageIO

**Caching:**
- None - No caching layer implemented

## Authentication & Identity

**Auth Provider:**
- Not applicable - Library has no authentication requirements

## Monitoring & Observability

**Error Tracking:**
- None - Custom exception handling with `PosterException`

**Logs:**
- No logging framework detected - Uses basic Java exception handling

## CI/CD & Deployment

**Hosting:**
- Maven Central Repository - For library distribution

**CI Pipeline:**
- Not detected in codebase (but implied by Maven Central deployment setup)
- Distribution configured via `pom.xml` with Sonatype OSSRH

## Environment Configuration

**Required env vars:**
- `JAVA_HOME` - For Javadoc generation (referenced in Maven config)
- `JAVA_HOME/bin/javadoc` - Path to Javadoc executable

**Secrets location:**
- GPG signing keys (for Maven Central deployment) - Configured via Maven GPG plugin

## Webhooks & Callbacks

**Incoming:**
- None - This is a library, not a service

**Outgoing:**
- None - No HTTP clients or external API calls

## Internal Dependencies

**Custom Utility Library:**
- `cn.augrain:easy-tool:0.0.2` - Internal utility library providing color support and other helpers
  - Location: Referenced in `pom.xml`
  - Usage: `cn.augrain.easy.tool.support.ColorUtils` for color operations

---

*Integration audit: 2026-04-21*