# Remediation Attempt — Conceptual Only

## Why this remediation was never actually applied or tested

Vulhub's `spring/CVE-2022-22965` lab provides a pre-built vulnerable image via `docker-compose.yml` — there is no application source code included, and no vulnerable Dockerfile was written for this lab (the image comes pre-built from Vulhub). Patching the `WebDataBinder` logic requires editing the Spring application's source and rebuilding it, which was not possible against this image. The fix in [`BinderControllerAdvice.java`](./BinderControllerAdvice.java) was therefore written as a **conceptual** remediation — it documents the standard fix for CVE-2022-22965, but it was never compiled, built into an image, or run.

## Steps as documented (not executed beyond step 1)

1. `docker-compose down` — stop the vulnerable container.
2. Add `BinderControllerAdvice.java` to the application source, denying binding of `class.*`, `Class.*`, `*.class.*`, `*.Class.*` properties.
3. Recompile / rebuild the Docker image with the new class included.
4. `docker-compose up -d` — restart with the patched image.

Steps 2–4 were not performed — there was no access to the application source to actually add the class, rebuild, or restart against a patched image.

## Expected (unverified) post-fix behavior

The lab writeup includes a screenshot labeled by its own author as **"Expected Failed output"** — i.e., a prediction of what re-running the exploit against a patched instance *should* produce, not an observed result from an actual test:

```
$ curl -i -s -k -X GET 'http://10.15.29.148:8080/?class.module.classLoader.resources.context.parent.pipeline.first.pattern=...'

HTTP/1.1 400 Bad Request
Content-Type: text/html;charset=utf-8

<html>
  <body>
    ...
    <h1>HTTP Status 400 – Bad Request</h1>
    <p><b>Message</b> Invalid property 'class.module.classLoader.resources...'</p>
    <p><b>Description</b> The request was rejected because the field is explicitly disallowed by the WebDataBinder configuration.</p>
  </body>
</html>
```

Screenshot: [`../Images/expected-post-fix-behavior-untested.jpg`](../Images/expected-post-fix-behavior-untested.jpg)

**This output was never actually produced by running the exploit against a patched container.** It is included here for completeness because it's part of the source lab document, but it should not be cited as verified remediation testing — see the Limitations section in [`../README.md`](../README.md).
