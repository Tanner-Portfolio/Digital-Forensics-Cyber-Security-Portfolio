# SIEM Threat Hunting & Network Reconnaissance

Two exercises in defensive log analysis and incident response tooling: hypothesis-driven threat hunting across indexed log data using Splunk, and structured network reconnaissance using Nmap and the Nmap Scripting Engine.

> **Scope.** This directory covers SIEM log correlation and network reconnaissance. It does not include packet-level (PCAP) analysis or custom IDS/IPS rule development. Both exercises were performed in a controlled lab environment against a provided dataset and a sanctioned public test host.

## Tools

| Tool | Purpose |
|---|---|
| Splunk | Log ingestion, indexing, and query-based correlation |
| Nmap | Host discovery, port enumeration, OS fingerprinting |
| Nmap NSE (`vulners`) | Automated CVE cross-referencing against detected service versions |

## Contents

| Path | Contents |
|---|---|
| [`queries/`](./queries) | Five Splunk detection queries, each with hypothesis and tuning notes |
| [`scans/nmap-commands.md`](./scans/nmap-commands.md) | Annotated Nmap command log with rationale per scan type |
| `Images/` | Query output and terminal screenshots |

---

## Exercise 1 — Hypothesis-Driven Threat Hunting in Splunk

### Objective

Develop and validate detection queries against an indexed log dataset, targeting five distinct adversary behaviours: anomalous authentication timing, authentication failure clusters, post-exploitation tooling installation, credential theft, and container escape.

Each query begins from a stated hypothesis about attacker behaviour rather than from a tool feature. Individual queries with tuning notes are in [`queries/`](./queries).

### Detections developed

**1. Authentication outside business hours** — [`after-hours-logins.spl`](./queries/after-hours-logins.spl)

```spl
index="splunk_data" "login successful"
| eval hour=strftime(_time, "%H")
| where hour<9 OR hour>=18
| table _time, host, user
```

Legitimate activity clusters inside working hours (09:00–18:00). Successful authentication outside that window may indicate credential compromise, an operator in a different timezone, or insider activity.

**2. Failed authentication attempts** — [`failed-logins.spl`](./queries/failed-logins.spl)

```spl
index="splunk_data" "login failed" OR "failed login" OR "authentication failed"
```

Matches multiple string variants because authentication failure is logged differently across sources. Volume and concentration matter more than individual events — isolated failures are user error. (Result screenshot not captured for this query.)

**3. Package installation activity** — [`apt-installs.spl`](./queries/apt-installs.spl)

```spl
index="splunk_data" "apt" AND "install"
```

Installing tooling is a routine post-exploitation step. This is a baselining query: its value is establishing what normal installation activity looks like so that deviations become visible.

**4. Denied access to private key material** — [`private-key-access.spl`](./queries/private-key-access.spl)

```spl
index="splunk_data" "denied" AND ("key" OR "private" OR ".pem")
| table _time, host, _raw
```

Denied reads against private key material warrant review as a possible credential-theft indicator, though a service account failing to read its own key is a common benign source (see Assessment). Matches on denial plus key terms rather than a fixed path, since key material is not always stored where policy says it is.

![Private key access query results — 21 events, all xrdp denied reads](./Images/private-key-access-results.png)

**5. Container escape attempts** — [`chroot-attempts.spl`](./queries/chroot-attempts.spl)

```spl
index="splunk_data" "chroot"
```

In a containerised environment, `chroot` execution can indicate an attempt to break filesystem isolation. Combined with `CAP_SYS_CHROOT`, it is an escape primitive.

![Chroot query results](./Images/chroot_query_results.png)

### Assessment

**Dataset.** These queries were run against a lab Kali environment with logs accumulated over the course — a mix of routine system/service activity and lab-session activity, not a curated intrusion scenario. Findings are framed accordingly.

**chroot query — 97 events, all benign.** Every returned event was `rtkit-daemon: Successfully called chroot`. RealtimeKit calls `chroot` as part of normal operation, so these are expected background activity, not container-escape attempts. The result's value is negative: it establishes what normal `chroot` usage looks like on this host. A genuine hunt would suppress `rtkit-daemon` and alert only on `chroot` from unexpected process contexts.

**Private-key access query — 21 events, service noise.** All 21 were `xrdp` failing to read `/etc/xrdp/key.pem` (`Permission denied`), repeating across a short window. This is consistent with an `xrdp` service reading its own key, not an external actor exfiltrating credentials — a benign source rather than compromise.

**After-hours logins, failed logins, and package installs.** Detection logic for these was written and validated (see [`queries/`](./queries)), but result screenshots were not captured, so no event counts are claimed.

**Takeaway.** The recurring lesson is that a raw keyword match surfaces predominantly benign activity. The analytical work is not writing the query — it is establishing a baseline (here, `rtkit-daemon` and the `xrdp` service context) and filtering it out to isolate anything genuinely anomalous. In this dataset the returned events represent expected background, not a confirmed intrusion.

### Recommendations

The exercise surfaced no live compromise, so these are forward-looking: how to turn each noisy keyword hunt into an operational detection, and the controls that reduce the underlying risk.

- **Baseline and suppress before alerting.** Both the `chroot` and private-key queries were dominated by known-good sources (`rtkit-daemon`, `xrdp`). A production detection must exclude established baselines and fire only on deviations — `chroot` from an interactive shell or unexpected container, key-read denials from a principal that isn't the owning service.
- **Enforce time-window conditional access.** The after-hours login detection is only actionable if out-of-hours access is genuinely restricted. Pair the query with conditional-access policy so an after-hours success becomes an exception rather than routine noise.
- **Alert on authentication-failure concentration, not volume.** Lockout thresholds plus alerting when failures cluster on a single account or source; a failure spike followed by a success is the signal, not raw failure count.
- **Constrain package installation to change windows.** Treat interactive `apt install` outside an approved maintenance window as reviewable, and watch specifically for known dual-use tooling.
- **Move key material to a secrets manager and drop unnecessary capabilities.** Relocating private keys out of flat files (`/etc/xrdp/key.pem` and similar) into a managed secret store, with read-access alerting, converts the private-key detection from service noise into a high-fidelity signal. On container hosts, dropping `CAP_SYS_CHROOT` where it isn't required removes the escape primitive the `chroot` detection targets.

### Detection engineering notes

Each query in [`queries/`](./queries) carries tuning notes covering expected false positive sources and suggested correlation with the other detections. The most useful pairing is authentication failures followed by a successful after-hours login on the same account — a stronger signal than either detection produces alone.

---

## Exercise 2 — Network Reconnaissance for IR Triage

> **Framing.** This was a structured lab exercise in tooling proficiency, not a live investigation. The target was `45.33.32.156` (`scanme.nmap.org`), a host the Nmap project provides for scanning practice. No production or third-party system was scanned, and no incident was in progress.

### Objective

Build working proficiency with Nmap and NSE as applied to incident response triage: establishing whether a host is reachable, enumerating exposed services, inferring the operating system, and cross-referencing detected versions against known vulnerabilities.

### Methodology

Full annotated command log: [`scans/nmap-commands.md`](./scans/nmap-commands.md)

```bash
nmap -sn 45.33.32.156              # host discovery, no port scan
nmap -sS 45.33.32.156              # stealth SYN — half-open, lighter log footprint
nmap -O -vv 45.33.32.156           # OS fingerprinting, verbose reasoning
nmap --script=default 45.33.32.156 # default NSE script set
nmap --script vulners 45.33.32.156 # CVE cross-reference on detected versions
```

Scan selection reasoning and per-technique limitations are documented in the command log. Two points worth surfacing here:

**`-sS` over `-sT`.** A half-open scan never completes the TCP handshake, leaving less behind in application-level connection logs. When triaging a host that may be monitored or under attacker control, minimising your own footprint matters — and it avoids contaminating logs you may need to analyse later.

**`vulners` output is a lead, not a finding.** The script matches on reported version strings. It does not verify exploitability and does not account for backported vendor patches, so it will report CVEs against versions already remediated. Treating its output as confirmed vulnerabilities is a common and consequential analyst error.

### Output

![Nmap NSE vulners output](./Images/vulners_terminal_output.png)

The scan targeted ports 22 and 80, but the `-p 22,80` argument failed to parse (`Failed to resolve "22,80"` in the output), so Nmap fell back to a default scan. One service was returned:

**Port 22/tcp — OpenSSH 6.6.1p1 (Ubuntu 2ubuntu2.13).** The `vulners` script matched this version against a long list of CVEs — the most severe rated CVSS 10.0, with multiple 9.8 entries (CVE-2023-38408, CVE-2016-1908), CVE-2015-5600 (8.5), and several `*EXPLOIT*`-tagged public exploits.

**Interpreting the result honestly:** the raw CVE list overstates real exposure. `vulners` matches on the reported version string alone and does not verify exploitability. More importantly, the `2ubuntu2.13` suffix indicates an Ubuntu package that backports security fixes while keeping the upstream `6.6.1` version number — so many CVEs flagged against "6.6.1p1" are likely already patched in this build. Confirming actual exposure would mean checking each CVE against the Ubuntu package changelog rather than trusting the version match. This is the practical limit of automated CVE cross-referencing: it produces leads, not a confirmed vulnerability list.

### Reflection

The source lab included written analysis of Nmap's role in incident response. Two points carry forward. First, Nmap's core value in triage is rapid situational awareness: host discovery, service enumeration, and OS fingerprinting quickly establish what an asset runs and where its exposed surface lies — the starting point for scoping an incident (Lyon, 2010). Second, the Nmap Scripting Engine extends this from manual inspection to automated assessment: scripts like `vulners` cross-reference detected versions against CVE databases at a speed manual lookup cannot match, helping responders prioritise findings (Robert, 2025). The caveat above applies throughout — automation accelerates lead generation, but the responder still validates what it surfaces.

---

## Limitations

- No packet-level (PCAP) analysis was performed.
- No custom IDS/IPS rules were developed.
- No host-based forensic collection was performed on the scanned target.
- Scanning was point-in-time and uncredentialed.
- The Splunk dataset was lab-provided rather than drawn from a live environment.

## Sources

- Lyon, G. (2010). *Nmap network scanning: Official Nmap project guide to network discovery and security scanning.* Insecure.Com.
- Robert, A. (2025, October 16). *Nmap incident response.* hoop.dev
