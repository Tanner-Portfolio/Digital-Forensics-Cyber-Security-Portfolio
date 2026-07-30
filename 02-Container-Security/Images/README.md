Image references for 02-Container-Security

| File | Contents |
|---|---|
| `vulhub-docker-compose-pull.png` | `docker-compose up -d` pulling Vulhub's pre-built `spring/CVE-2022-22965` image |
| `dataBinder-config-injection-response.png` | Browser confirmation the vulnerable Spring app is reachable and responding (`/name=Bob&age=25`) |
| `curl-tomcat-config-injection.png` | First-stage `curl` payload overwriting the Tomcat access-log config to plant `tomcatwar.jsp` |
| `Spring4ShellSuccess.png` | Second-stage `curl` request to `tomcatwar.jsp` returning `root` — confirmed RCE |
| `conceptual-webdatabinder-fix.jpg` | Conceptual `BinderControllerAdvice.java` fix (never built/tested — see [`../reports/remediation-attempt.md`](../reports/remediation-attempt.md)) |
| `expected-post-fix-behavior-untested.jpg` | Lab author's own predicted ("Expected Failed output") post-patch response — not an observed test result |

