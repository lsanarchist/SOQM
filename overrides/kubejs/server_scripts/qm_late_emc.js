// Generated 0.4.3: exact IDs; native serializers and batch outputs retained.
ServerEvents.recipes(function(event) {
  var removeIds = ["appliede:emc_module", "appliede:transmutation_terminal", "projecte:collector_mk1", "projecte:condenser_mk1", "projecte:full_star_tome", "projecte:full_star_tome_alt", "projecte:philosophers_stone", "projecte:philosophers_stone_alt", "projecte:relay_mk1", "projecte:tome", "projecte:tome_alt", "projecte:transmutation_table"];
  removeIds.forEach(function(id) { event.remove({id:id}); });
  var recipes = [
  {
    "id": "kubejs:late_emc/emc_core",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "GCG",
        "MAM",
        "CCC"
      ],
      "key": {
        "G": {
          "item": "botania:gaia_ingot"
        },
        "C": {
          "item": "ad_astra:calorite_ingot"
        },
        "M": {
          "item": "kubejs:mythic_circuit"
        },
        "A": {
          "item": "mekanism:pellet_antimatter"
        }
      },
      "result": {
        "id": "kubejs:emc_core",
        "count": 8
      }
    }
  },
  {
    "id": "kubejs:late_emc/philosophers_stone",
    "recipe": {
      "type": "avaritia:shaped_table",
      "tier": 4,
      "pattern": [
        "NNNNNNNNN",
        "NGGGRGGGN",
        "NGDCKCDGN",
        "NGCMAMCGN",
        "NRKABAKRN",
        "NGCMAMCGN",
        "NGDCKCDGN",
        "NGGGRGGGN",
        "NNNNNNNNN"
      ],
      "key": {
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "G": {
          "item": "minecraft:glowstone"
        },
        "R": {
          "item": "minecraft:redstone_block"
        },
        "D": {
          "item": "minecraft:diamond_block"
        },
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "K": {
          "item": "kubejs:quantum_processor"
        },
        "M": {
          "item": "ae2:singularity"
        },
        "A": {
          "item": "mekanism:ultimate_energy_cube"
        },
        "B": {
          "item": "kubejs:emc_core"
        }
      },
      "result": {
        "id": "projecte:philosophers_stone",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:late_emc/transmutation_table",
    "recipe": {
      "type": "avaritia:shaped_table",
      "tier": 4,
      "pattern": [
        "OOOOOOOOO",
        "OCCDADCCO",
        "OCQRMRQCO",
        "ODRBNBRDO",
        "OAMKPKMAO",
        "ODRBNBRDO",
        "OCQRMRQCO",
        "OCCDADCCO",
        "OOOOOOOOO"
      ],
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "D": {
          "item": "minecraft:diamond_block"
        },
        "A": {
          "item": "ae2:controller"
        },
        "Q": {
          "item": "mekanism:quantum_entangloporter"
        },
        "R": {
          "item": "rftoolsutility:crafter3"
        },
        "M": {
          "item": "ae2:singularity"
        },
        "B": {
          "item": "enderio:advanced_capacitor_bank"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "P": {
          "item": "projecte:philosophers_stone"
        },
        "K": {
          "item": "kubejs:emc_core"
        }
      },
      "result": {
        "id": "projecte:transmutation_table",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:late_emc/collector_mk1",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "GTG",
        "GDG",
        "KFG"
      ],
      "key": {
        "G": {
          "item": "minecraft:glowstone"
        },
        "T": {
          "item": "enderio:advanced_capacitor_bank"
        },
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "F": {
          "item": "extrautilitiesreborn:netherstar_generator"
        },
        "K": {
          "item": "kubejs:emc_core"
        }
      },
      "result": {
        "id": "projecte:collector_mk1",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:late_emc/condenser_mk1",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "O%O",
        "&CD",
        "OKO"
      ],
      "key": {
        "O": {
          "tag": "c:obsidians/normal"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "C": {
          "item": "projecte:alchemical_chest"
        },
        "K": {
          "item": "kubejs:emc_core"
        },
        "%": {
          "item": "mekanism:quantum_entangloporter"
        },
        "&": {
          "item": "ae2:drive"
        }
      },
      "result": {
        "id": "projecte:condenser_mk1",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:late_emc/relay_mk1",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "OSO",
        "ODO",
        "OKO"
      ],
      "key": {
        "O": {
          "tag": "c:obsidians/normal"
        },
        "S": {
          "item": "enderio:advanced_capacitor_bank"
        },
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "K": {
          "item": "kubejs:emc_core"
        }
      },
      "result": {
        "id": "projecte:relay_mk1",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:late_emc/appliede_emc_module",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "%R&",
        "KTS",
        "DRD"
      ],
      "key": {
        "D": {
          "item": "projecte:dark_matter_block"
        },
        "R": {
          "item": "projecte:red_matter_block"
        },
        "T": {
          "item": "projecte:transmutation_tablet"
        },
        "S": {
          "item": "ae2:singularity"
        },
        "K": {
          "item": "kubejs:emc_core"
        },
        "%": {
          "item": "ae2:controller"
        },
        "&": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "result": {
        "id": "appliede:emc_module",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:late_emc/appliede_transmutation_terminal",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:terminal"
        },
        {
          "item": "projecte:transmutation_tablet"
        },
        {
          "item": "ae2:singularity"
        },
        {
          "item": "projecte:red_matter_block"
        },
        {
          "item": "kubejs:emc_core"
        },
        {
          "item": "ae2:crafting_unit"
        },
        {
          "item": "enderio:advanced_capacitor_bank"
        }
      ],
      "result": {
        "id": "appliede:transmutation_terminal",
        "count": 1
      }
    }
  }
];
  recipes.forEach(function(row) { event.custom(row.recipe).id(row.id); });
});
