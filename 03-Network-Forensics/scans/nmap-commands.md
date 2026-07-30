# Nmap Command Log

**Target:** `45.33.32.156` — `scanme.nmap.org`

`scanme.nmap.org` is a host the Nmap project provides explicitly for scanning practice. All activity documented here was against that sanctioned target. No third-party or production system was scanned.

**Date:** February 2026
**Context:** Skills development for incident response triage.

---

## 1. Host discovery

```bash
nmap -sn 45.33.32.156
```

Ping sweep only — no port scan. Establishes whether the host is reachable before spending time on enumeration. In a triage scenario this is the cheapest first question: is the asset even up.

## 2. Stealth SYN scan

```bash
nmap -sS 45.33.32.156
```

Half-open scan. Sends SYN, reads the response, never completes the handshake. Because the TCP connection is never established, it leaves less behind in application-level connection logs than a full connect scan (`-sT`).

Relevant to IR: when triaging a host that may be monitored — or may be under attacker control — reducing your own footprint matters. It also avoids muddying the very logs you may need to analyse afterwards.

Requires raw packet privileges.

## 3. OS fingerprinting

```bash
nmap -O -vv 45.33.32.156
```

TCP/IP stack fingerprinting to infer the operating system, with doubled verbosity to surface the reasoning rather than just the conclusion.

Relevant to IR: the OS determines which artifact locations, log paths, and forensic tooling apply. Knowing it before you connect saves time and avoids running the wrong collection scripts.

Fingerprinting is inferential and can be wrong, particularly behind NAT or a filtering device. Treat the result as a hypothesis.

## 4. Default script set

```bash
nmap --script=default 45.33.32.156
```

Equivalent to `-sC`. Runs the NSE scripts tagged `default` — service banner grabbing, common misconfiguration checks, basic enumeration.

Relevant to IR: fast, broad first pass. Cheap way to find the obvious before committing to targeted testing.

## 5. CVE cross-reference

```bash
nmap --script vulners 45.33.32.156
```

The `vulners` NSE script takes the service versions Nmap detected and queries them against public vulnerability databases, returning matching CVEs with severity scores.

NSE script obtained via `git clone` from the upstream repository.

Relevant to IR: automates a lookup that is slow by hand and time-critical during triage.

**Important limitation:** `vulners` matches on reported version strings. It does not verify exploitability, does not account for backported vendor patches, and will report CVEs against versions that have already been remediated. Output is a starting point for investigation, not a vulnerability assessment. Treating its output as confirmed findings is a common and serious analyst error.

---

## Notes

- No credentialed scanning was performed.
- All scans were point-in-time. A host clean at scan time may not be clean an hour later.
- Raw output screenshot: `../Images/vulners_terminal_output.png`
