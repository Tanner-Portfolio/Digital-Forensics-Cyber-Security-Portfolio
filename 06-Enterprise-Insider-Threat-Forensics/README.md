# Enterprise Insider Threat Investigation

## Overview
This repository contains the forensic documentation and analysis reports from a digital forensics investigation into an insider threat and data exfiltration incident. 

## Tools
*   **Autopsy (v4.21.0):** File system analysis, raw data carving, ingest tracking.
*   **Volatility (v2.6):** Volatile memory analysis and kernel block identification.
*   **Bulk Extractor:** Cryptographic key carving from corrupted RAM.
*   **FTK Imager:** Creation of forensically sound bit-stream images and hash validation.

## Artifacts
1.  **Investigator Working Notes:** Detailed timeline of evidence acquisition and analysis.
2.  **Evidence Control Log & Chain of Custody:** Formal documentation demonstrating strict adherence to NIST digital evidence preservation guidelines.
3.  **Final Forensic Report:** Executive summary and technical breakdown of the incident, including the successful recovery of AES-256 master keys from corrupted volatile memory.

## Methodology
*   **Data Carving:** Bypassed severe memory corruption (page faults/smearing) by pivoting to raw data carving, successfully extracting VeraCrypt AES-256 master keys from the memory dump.
*   **Timeline Reconstruction:** Utilized Windows Prefetch, LNK files, and Registry Hives (`MountedDevices`) to prove the suspect actively mounted encrypted volumes (Drives K: and L:) outside of standard operating hours.
*   **Cryptographic Identification:** Identified high-entropy 10MB containers disguised as `.txt` files, correlating with active `VeraCrypt.exe` execution in RAM.

*Note: All data provided in this repository has been anonymized for portfolio purposes.*
