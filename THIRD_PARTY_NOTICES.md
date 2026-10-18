# Credits and distribution

SOQM is an independent, unofficial Minecraft Java Edition modpack by lsanarchist.
It is not an official Minecraft product and is not approved by or associated
with Mojang or Microsoft.

- **Game content baseline:** the user-supplied Questing Mayhem Expert **0.4.6**
  Sources archive, SHA-256
  `5c17c9030296cbbc4ce8a5c0e075c19f10366524a17f95f7fbcbe12c25604061`.
  `soqm.json` records the input; source archives and their historical runtime
  reports are not committed as project artifacts.
- **Third-party mods:** `metadata/mods.lock.json` pins **122** official
  project/file references, versions, hashes and sizes. The original 111 pins
  remain unchanged, with 11 additions. The installable ZIP uses a manifest,
  rather than bundling these mod JARs. Each mod retains its own license;
  an absent license field is unknown, not a grant of redistribution rights.
  See [MODLIST.md](MODLIST.md).
- **Authored compatibility modules:** **10** `qm-*` modules come from the
  supplied baseline: the original eight, `qm-mob-compat-0.4.5` and
  `qm-chest-balance-0.4.6`. Sources, resources and provenance are retained under
  [src/compat/](src/compat/README.md); production binary hashes are pinned
  separately. The original eight include full MIT notices. The two additions
  declare MIT in their mod metadata but did not supply full license/copyright
  notices in their source or JARs; their local `LICENSE` files document this
  limitation rather than inventing a notice. Source provenance is not proof of
  an independently reproduced binary build.
- **Modified structure templates:** nine inherited NBT resources adjust only
  inventories from upstream structures. Their origin is recorded, but their
  redistribution licenses have not been established. They are not covered by
  a blanket SOQM MIT claim; see the [source audit](docs/SOURCE_AUDIT.md).
- **Resource model fixes:** the 0.4.6 baseline corrects references in the
  AppliedE EMC Module and Ex Machinis Flux Hammer models. These remain
  derivatives of their respective upstream resources, not newly claimed
  SOQM artwork. Upstream authors and licenses remain applicable.
- **Craftoria:** an inspiration for curation and usability. Its official
  project [reserves its rights](https://www.curseforge.com/minecraft/modpacks/craftoria/license).
  The two previously identified adapted scripts remain replaced by independent
  SOQM policies using public Curios, KubeJS and LootJS APIs. No Craftoria logo,
  quest book or custom mod is claimed or included. See the
  [historical source audit](docs/SOURCE_AUDIT.md) for that earlier review's scope.
- **Questing Mayhem:** the [original modpack by onemantic](https://www.curseforge.com/minecraft/modpacks/questing-mayhem)
  is credited as the naming inspiration. No affiliation or endorsement is
  claimed. The supplied 1.21.1 baseline is recorded separately from that project.

The [Minecraft Usage Guidelines](https://www.minecraft.net/en-us/usage-guidelines)
and [CurseForge export guidance](https://support.curseforge.com/en/support/solutions/articles/9000198500)
inform the distribution layout. A source commit or a local build is not
CurseForge catalog approval. A future catalog submission may require dedicated
project IDs or approved entries for authored modules.

GitHub is used for version control. Generated installation ZIPs, downloaded
third-party JARs and local reports are not committed. Existing immutable links
to the first public alpha remain historical downloads.

The source audit records specific provenance checks and removals; it does not
certify ownership of every inherited text or asset. The repository license does
not replace upstream licenses. Report a specific attribution or licensing
concern through a GitHub issue.
