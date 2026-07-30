Image References for 01-Endpoint-Forensics

| File | Contents |
|---|---|
| `volatility-windowsinfo-output.png` | Volatility 3 `windows.info` output — machine identification |
| `process-tree-ncat-reverse-shell.png` | Volatility 3 process-tree output showing the `cmd.exe` / `ncat.exe` / `cmd.exe` chain (see PID discrepancy note in [`../scans/volatility-ram-analysis.md`](../scans/volatility-ram-analysis.md)) |
| `bitlocker-recovery-key.png` | Recovered BitLocker recovery-key file contents |
| `regripper-computername-output.png` | Registry hive text dump, `Select-String computername` query result |
| `muicache-bdeunlock-usage.png` | MUICache registry artifact showing `bdeunlock.exe` execution |
| `threat-actor-instructions-email.png` | Recovered `Instructions.pdf` — the phishing/tasking email from the threat actor to the subject |
| `camouflaged-png-discovery.png` | `find \| file \| grep "PNG"` output — extension-mismatched PNG files |
| `hidden-flag-files-recovery.png` | `find` + `cat` output for hidden dot-directory files |
| `exiv2-metadata-output.png` | `exiv2 *` output — Exif comment/hash extraction from carved images |
| `recovered-image-1.png`, `recovered-image-2.png`, `recovered-image-4.png`, `recovered-image-5.png` | The carved images themselves (Image #1, #2, #4, #5), each displaying its own embedded label and hash |

