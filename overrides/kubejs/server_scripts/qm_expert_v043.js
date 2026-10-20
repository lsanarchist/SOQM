// Generated 0.4.3: exact IDs; native serializers and batch outputs retained.
ServerEvents.recipes(function(event) {
  var removeIds = ["ad_astra:jet_suit", "ae2:network/blocks/io_condenser", "avaritia:infinity_boots", "avaritia:infinity_bow", "avaritia:infinity_chestplate", "avaritia:infinity_helmet", "avaritia:infinity_pants", "avaritia:infinity_pickaxe", "avaritia:infinity_shield", "avaritia:infinity_sword", "enderio:vacuum_chest", "extrautilitiesreborn:chiken_wing_ring_rcp", "extrautilitiesreborn:netherstar_generator_rcp", "mekanism:jetpack", "mekanism:jetpack_armored", "mekanism:module_jetpack_unit", "projecte:collector_mk2", "projecte:collector_mk3", "projecte:condenser_mk2", "projecte:relay_mk2", "projecte:relay_mk3", "projecte:transmutation_tablet"];
  removeIds.forEach(function(id) { event.remove({id:id}); });
  var recipes = [
  {
    "id": "kubejs:expert_v043/ae2/network/blocks/io_condenser",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "item": "minecraft:iron_block"
        },
        "G": {
          "item": "sophisticatedstorage:gold_to_diamond_tier_upgrade"
        },
        "B": {
          "item": "ae2:quartz_glass"
        },
        "C": {
          "item": "mekanism:advanced_energy_cube"
        },
        "P": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "IGI",
        "BCB",
        "IPI"
      ],
      "result": {
        "count": 1,
        "id": "ae2:condenser"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/enderio/vacuum_chest",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:chests"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "P": {
          "tag": "c:gems/pulsating_crystal"
        },
        "%": {
          "item": "mob_grinding_utils:absorption_hopper"
        },
        "&": {
          "item": "minecraft:iron_block"
        },
        "?": {
          "item": "minecraft:iron_block"
        }
      },
      "pattern": [
        "%&?",
        "ICI",
        "IPI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:vacuum_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/mekanism/jetpack",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_tin"
        },
        "S": {
          "item": "kubejs:propulsion_turbine"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        }
      },
      "pattern": [
        "SCS",
        "ITI",
        " I "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:jetpack"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/mekanism/jetpack_armored",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:dusts/diamond"
        },
        "I": {
          "item": "mekanism:block_bronze"
        },
        "P": {
          "item": "mekanism:jetpack"
        },
        "S": {
          "tag": "c:storage_blocks/steel"
        },
        "%": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "#%#",
        "ISI",
        " P "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:jetpack_armored"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/mekanism/module_jetpack_unit",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": [
          {
            "item": "mekanism:jetpack"
          },
          {
            "item": "mekanism:jetpack_armored"
          }
        ],
        "A": {
          "tag": "c:alloys/elite"
        },
        "H": {
          "tag": "c:pellets/polonium"
        },
        "P": {
          "item": "mekanism:module_base"
        },
        "%": {
          "item": "kubejs:propulsion_turbine"
        },
        "&": {
          "item": "kubejs:propulsion_turbine"
        }
      },
      "pattern": [
        "%#&",
        "APA",
        "HHH"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:module_jetpack_unit"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/extrautilitiesreborn/chiken_wing_ring_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "aba",
        "b%b",
        "cbc"
      ],
      "key": {
        "a": {
          "item": "extrautilitiesreborn:golden_feather"
        },
        "b": {
          "item": "extrautilitiesreborn:enchanted_ingot"
        },
        "c": {
          "item": "extrautilitiesreborn:redstone_crystal"
        },
        "%": {
          "item": "kubejs:propulsion_turbine"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:chicken_wing_ring",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v043/ad_astra/jet_suit",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "kubejs:propulsion_turbine"
        },
        "C": {
          "item": "ad_astra:etrionic_capacitor"
        },
        "E": {
          "item": "ad_astra:calorite_engine"
        },
        "N": {
          "item": "ad_astra:netherite_space_suit"
        },
        "T": {
          "item": "ad_astra:calorite_tank"
        },
        "%": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "#%#",
        "TNT",
        "CEC"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:jet_suit"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/projecte/transmutation_tablet",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "item": "projecte:dark_matter_block"
        },
        "S": {
          "tag": "c:stones"
        },
        "T": {
          "item": "projecte:transmutation_table"
        },
        "%": {
          "item": "ae2:controller"
        },
        "&": {
          "item": "kubejs:quantum_processor"
        },
        "?": {
          "item": "mekanism:ultimate_energy_cube"
        }
      },
      "pattern": [
        "D%D",
        "&T?",
        "DSD"
      ],
      "result": {
        "count": 1,
        "id": "projecte:transmutation_tablet"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/extrautilitiesreborn/netherstar_generator_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "GSG",
        "NCN",
        "BRB"
      ],
      "key": {
        "G": {
          "item": "extrautilitiesreborn:furnace_generator"
        },
        "S": {
          "item": "minecraft:wither_skeleton_skull"
        },
        "N": {
          "item": "minecraft:nether_star"
        },
        "C": {
          "item": "extrautilitiesreborn:furnace_generator"
        },
        "B": {
          "item": "minecraft:iron_block"
        },
        "R": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:netherstar_generator",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v043/projecte/collector_mk2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "item": "minecraft:glowstone"
        },
        "P": {
          "item": "projecte:collector_mk1"
        },
        "U": {
          "item": "projecte:dark_matter"
        },
        "%": {
          "item": "mekanism:elite_energy_cube"
        }
      },
      "pattern": [
        "%UG",
        "GPG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "projecte:collector_mk2"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/projecte/relay_mk2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "O": {
          "tag": "c:obsidians/normal"
        },
        "P": {
          "item": "projecte:relay_mk1"
        },
        "U": {
          "item": "projecte:dark_matter"
        },
        "%": {
          "item": "enderio:advanced_capacitor_bank"
        }
      },
      "pattern": [
        "%UO",
        "OPO",
        "OOO"
      ],
      "result": {
        "count": 1,
        "id": "projecte:relay_mk2"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/projecte/collector_mk3",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "item": "minecraft:glowstone"
        },
        "P": {
          "item": "projecte:collector_mk2"
        },
        "U": {
          "item": "projecte:red_matter"
        },
        "%": {
          "item": "mekanism:ultimate_energy_cube"
        }
      },
      "pattern": [
        "%UG",
        "GPG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "projecte:collector_mk3"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/projecte/relay_mk3",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "O": {
          "tag": "c:obsidians/normal"
        },
        "P": {
          "item": "projecte:relay_mk2"
        },
        "U": {
          "item": "projecte:red_matter"
        },
        "%": {
          "item": "enderio:advanced_capacitor_bank"
        }
      },
      "pattern": [
        "%UO",
        "OPO",
        "OOO"
      ],
      "result": {
        "count": 1,
        "id": "projecte:relay_mk3"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/projecte/condenser_mk2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "projecte:condenser_mk1"
        },
        "D": {
          "item": "projecte:dark_matter_block"
        },
        "R": {
          "item": "projecte:red_matter_block"
        },
        "%": {
          "item": "ae2:molecular_assembler"
        },
        "&": {
          "item": "mekanism:quantum_entangloporter"
        }
      },
      "pattern": [
        "%&R",
        "DCD",
        "RDR"
      ],
      "result": {
        "count": 1,
        "id": "projecte:condenser_mk2"
      }
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_pickaxe",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "A": {
          "item": "avaritia:crystal_matrix"
        },
        "B": {
          "item": "avaritia:neutron"
        },
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "%": {
          "item": "mekanism:meka_tool"
        },
        "&": {
          "item": "mekanism:digital_miner"
        }
      },
      "pattern": [
        "%IIIIII&B",
        "    IIAA ",
        "     AXAI",
        "     AAII",
        "    N  II",
        "   N    I",
        "  N     I",
        " N      I",
        "A        "
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_pickaxe"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_sword",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "A": {
          "item": "avaritia:crystal_matrix"
        },
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "%": {
          "item": "mekanism:meka_tool"
        },
        "&": {
          "item": "immersiveengineering:railgun"
        }
      },
      "pattern": [
        "%&     II",
        "      III",
        "     III ",
        "  C III  ",
        "CCAIII   ",
        " CAXI    ",
        "  NAAC   ",
        " N CC    ",
        "A   C    "
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_sword"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_bow",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "A": {
          "item": "avaritia:crystal_matrix"
        },
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "P": {
          "item": "avaritia:neutron_pile"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "%": {
          "item": "immersiveengineering:railgun"
        },
        "&": {
          "item": "mekanism:laser"
        }
      },
      "pattern": [
        "%&    III",
        "  AAIINNP",
        " AXA   C ",
        " AA   C  ",
        " I   C   ",
        " I  C    ",
        "IN C     ",
        "INC      ",
        "IP       "
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_bow"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_helmet",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "%": {
          "item": "mekanism:mekasuit_helmet"
        },
        "&": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "%&NNNNN  ",
        " NIIIIIN ",
        " N XIX N ",
        " NIIIIIN ",
        " NIIIIIN ",
        " NI I IN ",
        "         ",
        "         ",
        "         "
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_helmet"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_chestplate",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "X": {
          "item": "avaritia:crystal_matrix"
        },
        "%": {
          "item": "mekanism:mekasuit_bodyarmor"
        },
        "&": {
          "item": "ad_astra:jet_suit"
        }
      },
      "pattern": [
        "%NN&  NN ",
        "NNN   NNN",
        "NNN   NNN",
        " NIIIIIN ",
        " NIIXIIN ",
        " NIIIIIN ",
        " NIIIIIN ",
        " NIIIIIN ",
        "  NNNNN  "
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_chestplate"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_pants",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "%": {
          "item": "mekanism:mekasuit_pants"
        },
        "&": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "NNNNNNNNN",
        "NIIIXIIIN",
        "NINNXNNIN",
        "NIN%& NIN",
        "NCN   NCN",
        "NIN   NIN",
        "NIN   NIN",
        "NIN   NIN",
        "NNN   NNN"
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_pants"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_boots",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "%": {
          "item": "mekanism:mekasuit_boots"
        },
        "&": {
          "item": "kubejs:propulsion_turbine"
        }
      },
      "pattern": [
        "%NNN&NNN ",
        " NIN NIN ",
        " NIN NIN ",
        "NNIN NINN",
        "NIIN NIIN",
        "NNNN NNNN"
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_boots"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/avaritia/infinity_shield",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "A": {
          "item": "avaritia:crystal_matrix"
        },
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "D": {
          "item": "avaritia:diamond_lattice"
        },
        "I": {
          "item": "avaritia:infinity_ingot"
        },
        "M": {
          "item": "avaritia:neutron"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "S": {
          "item": "minecraft:shield"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "%": {
          "item": "mekanism:ultimate_energy_cube"
        },
        "&": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "%MNNCNNM&",
        " NCCDCCN ",
        " NSIIISN ",
        " NIAAAIN ",
        " NIAXAIN ",
        " NIAAAIN ",
        " NSIIISN ",
        " NCCDCCN ",
        " MNNCNNM "
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_shield"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert_v043/propulsion_turbine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "FLF",
        "DCD",
        "STS"
      ],
      "key": {
        "F": {
          "item": "ad_astra:fan"
        },
        "L": {
          "item": "immersiveengineering:coil_mv"
        },
        "D": {
          "item": "kubejs:electromechanical_drive"
        },
        "C": {
          "item": "ad_astra:engine_frame"
        },
        "S": {
          "item": "mekanism:block_tin"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        }
      },
      "result": {
        "id": "kubejs:propulsion_turbine",
        "count": 2
      }
    }
  }
];
  recipes.forEach(function(row) { event.custom(row.recipe).id(row.id); });
});
