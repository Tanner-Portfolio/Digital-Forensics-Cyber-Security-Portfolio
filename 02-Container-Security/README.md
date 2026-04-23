# Container Security: Vulnerability Verification & Remediation

## 📝 Project Overview
This project simulates a real-world DevSecOps scenario involving the identification, exploitation, and remediation of a critical vulnerability within a containerized application. The focus is on **CVE-2022-22965 (Spring4Shell)**, a Critical Remote Code Execution (RCE) flaw with a CVSS score of 9.8.

## 🏗️ Technical Architecture & Tools
*   **Infrastructure:** Docker, Docker Compose, Vulhub.
*   **Exploitation Tools:** cURL, crafted HTTP payloads.
*   **Vulnerable Asset:** Spring Framework on JDK 9+ running on Apache Tomcat.

## 🎯 Exploitation Methodology
To verify the vulnerability, a localized Vulhub container was spun up. The vulnerability exists in Spring's Data Binding logic, allowing an attacker to leverage the Java platform API to modify the `Tomcat ClassLoader`. 
1.  Injected a crafted payload via `curl` to overwrite the Tomcat logging configuration.
2.  Redirected the log output to write a malicious JSP webshell (`tomcatwar.jsp`) directly into the public web directory.
3.  Achieved root-level Remote Code Execution (RCE).

*(📸 Recommendation: Insert the cURL RCE output screenshot here showing the "root" response)*
`![RCE Root Access Verification](./images/spring4shell-rce.png)`

## 🛡️ Remediation Strategy
Remediation was mapped conceptually due to the image-based nature of the container:
*   **Software Upgrade:** Patching Spring Framework to 5.3.18+ and upgrading Apache Tomcat.
*   **Configuration Hardening:** Implementing a global `WebDataBinder` configuration class to explicitly block/deny fields that reference the vulnerable `class` and `module` properties, effectively closing the data-binding loophole.
