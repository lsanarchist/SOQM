# Credits and distribution

SOQM is an independent, unofficial Minecraft Java Edition modpack by lsanarchist.
It is not an official Minecraft product and is not approved by or associated
with Mojang or Microsoft.

- **Game content baseline:** the user-supplied Questing Mayhem Expert 0.4.4
  Sources archive. Its exact SHA256 is recorded in `soqm.json`; the original
  ZIP and its historical runtime reports are not part of this repository.
- **Third-party mods:** `metadata/mods.lock.json` records all 111 original
  project/file IDs, versions, hashes, sizes and official file pages. The
  installable ZIP contains references in `manifest.json`, not those mod JARs.
  Each mod retains its own license. A missing license field is **unknown**,
  not a grant of redistribution rights. See [MODLIST.md](MODLIST.md).
- **Compatibility modules:** eight small `qm-*` JARs originate in the supplied
  baseline. Their complete MIT notices and recovered source files are in
  `src/compat/`; their exact binary hashes are pinned separately. These are the
  only JARs embedded in the installation archive.
- **Craftoria:** an inspiration for curation and usability. Its official
  project [reserves its rights](https://www.curseforge.com/minecraft/modpacks/craftoria/license).
  Two identified adapted scripts were removed and replaced with SOQM policies
  based on public Curios, KubeJS and LootJS APIs. No Craftoria logo, quest book
  or custom mod is included. See [source audit](docs/SOURCE_AUDIT.md).
- **Questing Mayhem:** the [original modpack by onemantic](https://www.curseforge.com/minecraft/modpacks/questing-mayhem) is credited as the
  naming inspiration. SOQM is an independent homage; no affiliation or
  endorsement is claimed. The supplied 1.21.1 baseline is recorded separately
  from that original project.

The [Minecraft Usage Guidelines](https://www.minecraft.net/en-us/usage-guidelines)
and [CurseForge modpack export guidance](https://support.curseforge.com/en/support/solutions/articles/9000198500)
inform the distribution layout. GitHub publication is not CurseForge catalog
approval. Custom modules may need their own CurseForge project IDs or approved
third-party entries before a catalog submission.

The archive audit establishes file provenance and removes the identified
upstream adapters; it does not certify ownership of every historical text.
Report a specific attribution or licensing concern through a GitHub issue.
