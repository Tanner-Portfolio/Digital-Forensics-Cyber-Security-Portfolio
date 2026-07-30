# Registry Hive Analysis Command Log

**Target:** `SYSTEM`, `SOFTWARE`, `SAM`, and `NTUSER.DAT` hives recovered from host `WINCLIENTTGT`.
**Tooling:** hives were parsed to flat text (`system.txt`, `software.txt`) — the plugin-name-and-version banners in the output (e.g. `compname v.20090727`, `autostart v.20132603`, `winver v.20081210`, `recentdocs_timeline v.20101112`) match RegRipper's plugin output format, so RegRipper is credited here as the parsing tool. **TODO (Tanner): confirm RegRipper was in fact what generated `system.txt`/`software.txt` — the writeup itself never names the tool, only shows PowerShell queries against its output.** The resulting text dumps were then searched with PowerShell `Select-String`.

---

## 1. Computer name and hostname

```powershell
Select-String computername .\system.txt -Context 10,10
```

```
system.txt:1021:ComputerName   = WINCLIENTTGT
system.txt:1022:TCP/IP Hostname = WINCLIENTTGT
```

## 2. Time zone

```powershell
Select-String timezone .\system.txt -Context 10,10
```

```
system.txt:10464:  TimeZoneKeyName -> UTC
```

## 3. Autostart / persistence key

```powershell
Select-String auto .\software.txt | findstr /i start
Select-String autostart .\software.txt -Context 10,10
Select-String autostart .\software.txt -Context 1,20
```

```
software.txt:51049:soft_run v.20132603
software.txt:51052:Microsoft\Windows\CurrentVersion\Run
software.txt:51054: SecurityHealth = %windir%\system32\SecurityHystray.exe
```

A key named `soft_run` was found under `Microsoft\Windows\CurrentVersion\Run` — the persistence mechanism referenced in the README findings.

## 4. OS version and install date

```powershell
Select-String winver .\software.txt -Context 10,10
```

```
software.txt:55282:ProductName = Windows 10 Enterprise
software.txt:55283:InstallDate = Mon Feb  5 22:05:59 2024
```

## 5. Local accounts (SAM)

No literal command was captured for this step in the source lab writeup — only the resulting account listing (`Administrator [500]`, `Guest [501]`, `Default Account [503]`, `WDAGUtilityAccount [504]`, with `Administrator`'s last logon `Wed Feb 21 17:08:07 2024 Z`). **TODO (Tanner): if you have the actual command/plugin invocation, add it here.**

## 6. NTUSER.DAT — RecentDocs timeline

```
recentdocs_timeline v.20101112
(NTUSER.DAT) Gets contents of user's RecentDocs key and places last-write times into a timeline based on MRUListEx
```

Output:

```
Thu Jan 1 00:00:00 1970  :  Instructions.pdf
Thu Jan 1 00:00:00 1970  :  BitLocker Recovery Key 4E3D7844-CC56-427E-89A1-DCF265F60C16.TXT
Thu Jan 1 00:00:00 1970  :  System and Security
```

All three entries carry the Unix epoch (`Jan 1 1970`) as their timestamp rather than a real access time — see Findings/Assessment in [`../README.md`](../README.md) for why this is read as anti-forensic tampering rather than a parsing artifact.

## Notes

- Screenshots: [`../Images/regripper-computername-output.png`](../Images/regripper-computername-output.png)
- All hive data is coursework-lab evidence; see [`../README.md`](../README.md) for scope.
