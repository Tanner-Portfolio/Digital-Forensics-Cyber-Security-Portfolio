# Linux File Recovery Command Log

**Target:** disk image mounted at `/mnt/linux_image`, plus block device `dev/loop0` for the carving step.
**Environment:** Kali/root shell, `dfcs635-tsmith` session.

---

## Part 1 — Camouflaged files (extension mismatch)

```bash
find . -type f -exec file {} + | grep "PNG"
```

Output:

```
./var/log/eruces.log:              PNG image data, 297 x 207, 8-bit/color RGBA, non-interlaced
./usr/bin/sha1337sum:              PNG image data, 297 x 194, 8-bit/color RGBA, non-interlaced
./usr/share/pixmaps/debian-logo.png: PNG image data, 48 x 48, 8-bit colormap, non-interlaced
```

`find` recurses the mounted image; `file` inspects each file's magic bytes regardless of extension; `grep "PNG"` filters to true PNG content. This surfaced two files with non-image extensions (`.log`, and a fake `sha1337sum` binary name) that are actually PNG images.

Screenshot: [`../Images/camouflaged-png-discovery.png`](../Images/camouflaged-png-discovery.png)

Hashes were verified with `md5sum` against each discovered file:

| File | MD5SUM |
|---|---|
| `./var/log/eruces.log` | `4a3ad8ee3ebbfc8f0f8b1999a5b5d2ac` |
| `./usr/bin/sha1337sum` | `2fb309663ed77f2dcdea50801aba895e` |
| `./usr/share/pixmaps/debian-logo.png` | `602d21e07cf1463644a219849a53fb3b` |

## Part 2 — Hidden files (dot-directories)

```bash
find . -type f -path "*/.*/*"
```

Output:

```
./root/.../dfd07bf02.txt
./var/opt/help/.../0a1a0acb9.txt
./usr/share/mosaics/.../5dfa4aaad.txt
```

Contents were read with `cat` and hashed with `md5sum`:

| File | MD5SUM | Flag value |
|---|---|---|
| `./root/.../dfd07bf02.txt` | `08ea886a3f93082887c85ca84c2b87ac` | `28373b1aaa43681b582d7b915de3f1a7` |
| `./usr/share/mosaics/.../5dfa4aaad.txt` | `466d9d9cca6fcba730c0cfb9ed55a06d` | `a1e40388a41f49af8912433e6260d608` |
| `./var/opt/help/.../0a1a0acb9.txt` | `9c12183331091fafdd2d11aad106b0f4` | `63d58a055afae9fa2bec19e36160231f` |

Screenshot: [`../Images/hidden-flag-files-recovery.png`](../Images/hidden-flag-files-recovery.png)

## Part 3 — Deleted/carved images (`scalpel`, `hexdump`, `dd`)

The source lab writeup describes this step only in prose, with no literal command captured:

> "To recover deleted files, scalpel was run against `dev/loop0`. The recovery produced several corrupted JPEG files where multiple images were merged into a single file. ... The hexdump utility was used to find the hexadecimal offsets of the embedded JPEG headers (`ff d8`). The dd command was then used to split the merged files into individual, viewable images."

**TODO (Tanner) — required, not filled in:** the `scalpel` invocation (config file / target device / output dir), the `hexdump` command used to locate the `ff d8` offsets, and the `dd` `skip=`/`count=`/`bs=` parameters used to split each merged file are not in the source document. If you still have shell history or notes from this lab, add the literal commands here. Per the hard rule for this repo, no plausible-sounding version of these commands should be invented to fill the gap.

What the process recovered: five distinct JPEG/PNG images, each carrying a plaintext label and hash burned into the image content itself (`Image #1` through `Image #5`), confirming the carving reconstructed the files correctly rather than producing corrupted output.

## Part 4 — Metadata extraction (`exiv2`)

```bash
exiv2 *
```

Run from `~/final_output`, against the carved images plus the two extension-mismatched files recovered in Part 1.

Screenshot: [`../Images/exiv2-metadata-output.png`](../Images/exiv2-metadata-output.png)

| Image filename | MD5SUM | Exif comment |
|---|---|---|
| `00000004.jpg` | `60d41684320a07760e5b1f7812cda406` | `Image #1: 2d853eb08c4e31574f9b29474d0c9f5b` |
| `recovered_image_C.jpg` | `72f088f991e342b62a3c93f71015432d` | `Image #2: 34a8851d5f3584341e70a5c29628c21f` |
| `00000005.png` | `602d21e07cf1463644a219849a53fb3b` | *No Exif data found in the file* |
| `recovered_image_A.jpg` | `391e8de1cff3eb24912c13f1507a749f` | `Image #4: 5543eda2e225a60028c4e286b7bf1b20` |
| `recovered_image_B.jpg` | `f9aaf7dbec2350ba92cf0e079e95a436` | `Image #5: 586d8c432ebefd3a72e35bc1fe0a062e` |

Note: `00000005.png` matches the MD5 of `./usr/share/pixmaps/debian-logo.png` from Part 1 — it's the Debian stock logo, not evidentiary content, and carries no Exif comment.

## Notes

- All hashes and Exif comment values above are transcribed directly from the source lab document's output tables; none were recomputed or inferred.
- This is coursework-lab evidence against a provided disk image, not a live case.
