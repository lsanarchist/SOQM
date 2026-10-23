// Generated 0.4.2: exact recipe IDs, native serializers and components preserved.
ServerEvents.recipes(function (event) {
  var removeIds = ["enderio:alloy_smelter", "exdeorum:diamond_mesh", "exdeorum:diamond_mesh_from_golden_mesh", "exdeorum:golden_mesh", "exdeorum:golden_mesh_from_iron_mesh", "exdeorum:mechanical_hammer", "exdeorum:mechanical_sieve", "exmachinis:diamond_upgrade", "exmachinis:flux_compactor", "exmachinis:flux_hammer", "exmachinis:flux_sieve", "exmachinis:netherite_upgrade", "ironfurnaces:augments/augment_factory", "ironfurnaces:augments/augment_generator", "ironfurnaces:furnaces/crystal_furnace", "ironfurnaces:furnaces/diamond_furnace", "ironfurnaces:furnaces/emerald_furnace", "ironfurnaces:furnaces/gold_furnace", "ironfurnaces:furnaces/gold_furnace2", "ironfurnaces:furnaces/netherite_furnace", "ironfurnaces:furnaces/obsidian_furnace", "ironfurnaces:furnaces/obsidian_furnace2", "ironfurnaces:rainbow_core", "ironfurnaces:upgrades/upgrade_crystal", "ironfurnaces:upgrades/upgrade_diamond", "ironfurnaces:upgrades/upgrade_emerald", "ironfurnaces:upgrades/upgrade_gold", "ironfurnaces:upgrades/upgrade_gold2", "ironfurnaces:upgrades/upgrade_netherite", "ironfurnaces:upgrades/upgrade_obsidian", "ironfurnaces:upgrades/upgrade_obsidian2", "mob_grinding_utils:gm_chicken_feed", "mob_grinding_utils:recipe_entity_spawner", "mob_grinding_utils:recipe_fan", "mob_grinding_utils:recipe_saw", "mob_grinding_utils:recipe_saw_upgrade_beheading", "mob_grinding_utils:recipe_saw_upgrade_looting", "mob_grinding_utils:recipe_solidifier"];
  removeIds.forEach(function(id) { event.remove({id:id}); });
  var recipes = [
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/gold_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:ingots/gold"
        },
        "X": {
          "tag": "c:furnaces/iron"
        },
        "Y": {
          "tag": "c:storage_blocks/gold"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:gold_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/gold_furnace2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "#Y#"
      ],
      "key": {
        "Y": {
          "tag": "c:storage_blocks/gold"
        },
        "X": {
          "tag": "c:furnaces/silver"
        },
        "#": {
          "tag": "c:glass_blocks/colorless"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:gold_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_gold",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:ingots/gold"
        },
        "X": {
          "tag": "c:ingots/iron"
        },
        "Y": {
          "tag": "c:storage_blocks/gold"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_gold"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_gold2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:ingots/gold"
        },
        "X": {
          "tag": "c:ingots/silver"
        },
        "Y": {
          "tag": "c:storage_blocks/gold"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_gold2"
      },
      "neoforge:conditions": [
        {
          "value": {
            "type": "neoforge:tag_empty",
            "tag": "c:ingots/silver"
          },
          "type": "neoforge:not"
        }
      ]
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/diamond_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "GXG",
        "###"
      ],
      "key": {
        "#": {
          "tag": "c:gems/diamond"
        },
        "X": {
          "tag": "c:furnaces/gold"
        },
        "G": {
          "tag": "c:glass_blocks/colorless"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "ironfurnaces:diamond_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_diamond",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "GXG",
        "###"
      ],
      "key": {
        "#": {
          "tag": "c:gems/diamond"
        },
        "X": {
          "tag": "c:ingots/gold"
        },
        "G": {
          "tag": "c:glass_blocks/colorless"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_diamond"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/emerald_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "###"
      ],
      "key": {
        "#": {
          "tag": "c:gems/emerald"
        },
        "X": {
          "tag": "c:furnaces/diamond"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "ironfurnaces:emerald_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_emerald",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "###"
      ],
      "key": {
        "#": {
          "tag": "c:gems/emerald"
        },
        "X": {
          "tag": "c:gems/diamond"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_emerald"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/crystal_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "#E#"
      ],
      "key": {
        "#": {
          "tag": "c:glass_blocks/colorless"
        },
        "X": {
          "tag": "c:furnaces/diamond"
        },
        "E": {
          "item": "enderio:pulsating_crystal"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "ironfurnaces:crystal_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_crystal",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "#E#"
      ],
      "key": {
        "#": {
          "tag": "c:glass_blocks/colorless"
        },
        "X": {
          "tag": "c:gems/diamond"
        },
        "E": {
          "item": "enderio:pulsating_crystal"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_crystal"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/obsidian_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!#",
        "YXY",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:obsidians"
        },
        "X": {
          "tag": "c:furnaces/emerald"
        },
        "Y": {
          "tag": "c:rods/blaze"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "ironfurnaces:obsidian_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/obsidian_furnace2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!#",
        "YXY",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:obsidians"
        },
        "X": {
          "tag": "c:furnaces/crystal"
        },
        "Y": {
          "tag": "c:rods/blaze"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "ironfurnaces:obsidian_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_obsidian",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!#",
        "YXY",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:obsidians"
        },
        "X": {
          "tag": "c:gems/emerald"
        },
        "Y": {
          "tag": "c:rods/blaze"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_obsidian"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_obsidian2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!#",
        "YXY",
        "#Y#"
      ],
      "key": {
        "#": {
          "tag": "c:obsidians"
        },
        "X": {
          "tag": "c:glass_blocks/colorless"
        },
        "Y": {
          "tag": "c:rods/blaze"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_obsidian2"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/furnaces/netherite_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "NSN"
      ],
      "key": {
        "#": {
          "item": "minecraft:magma_cream"
        },
        "X": {
          "tag": "c:furnaces/obsidian"
        },
        "S": {
          "tag": "minecraft:soul_fire_base_blocks"
        },
        "N": {
          "item": "minecraft:netherite_ingot"
        },
        "!": {
          "item": "kubejs:stellar_alloy"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        },
        "$": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "ironfurnaces:netherite_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/upgrades/upgrade_netherite",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "#X#",
        "NSN"
      ],
      "key": {
        "#": {
          "item": "minecraft:magma_cream"
        },
        "X": {
          "tag": "c:obsidians"
        },
        "S": {
          "tag": "minecraft:soul_fire_base_blocks"
        },
        "N": {
          "item": "minecraft:netherite_ingot"
        },
        "!": {
          "item": "kubejs:stellar_alloy"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        },
        "$": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "ironfurnaces:upgrade_netherite"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/rainbow_core",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "ROY",
        "@!F",
        "BPM"
      ],
      "key": {
        "R": {
          "item": "minecraft:red_stained_glass"
        },
        "O": {
          "item": "minecraft:orange_stained_glass"
        },
        "Y": {
          "item": "minecraft:yellow_stained_glass"
        },
        "F": {
          "item": "ironfurnaces:netherite_furnace"
        },
        "B": {
          "item": "minecraft:blue_stained_glass"
        },
        "P": {
          "item": "minecraft:purple_stained_glass"
        },
        "M": {
          "item": "minecraft:magenta_stained_glass"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "result": {
        "id": "ironfurnaces:rainbow_core"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/augments/augment_generator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "PXP",
        "#R#"
      ],
      "key": {
        "#": {
          "tag": "c:stones"
        },
        "P": {
          "item": "minecraft:paper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "X": {
          "item": "minecraft:repeater"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:augment_generator"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/ironfurnaces/augments/augment_factory",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!#",
        "PXP",
        "#R#"
      ],
      "key": {
        "#": {
          "tag": "c:stones"
        },
        "P": {
          "item": "minecraft:paper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "X": {
          "item": "minecraft:piston"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "ironfurnaces:augment_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/enderio/alloy_smelter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "ironfurnaces:iron_furnace"
        },
        "G": {
          "tag": "c:gears/iron"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "O": {
          "tag": "c:obsidians"
        },
        "V": {
          "item": "enderio:void_chassis"
        }
      },
      "pattern": [
        "GFG",
        "FVF",
        "IOI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:alloy_smelter"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exdeorum/golden_mesh",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "tag": "c:strings"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "S#S",
        "#!#",
        "S#S"
      ],
      "result": {
        "count": 1,
        "id": "exdeorum:golden_mesh"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exdeorum/golden_mesh_from_iron_mesh",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:ingots/gold"
        },
        "M": {
          "item": "exdeorum:iron_mesh"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!# ",
        "#M#",
        " # "
      ],
      "result": {
        "count": 1,
        "id": "exdeorum:golden_mesh"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exdeorum/diamond_mesh",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "tag": "c:strings"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "S#S",
        "#!#",
        "S#S"
      ],
      "result": {
        "count": 1,
        "id": "exdeorum:diamond_mesh"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exdeorum/diamond_mesh_from_golden_mesh",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:gems/diamond"
        },
        "M": {
          "item": "exdeorum:golden_mesh"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!# ",
        "#M#",
        " # "
      ],
      "result": {
        "count": 1,
        "id": "exdeorum:diamond_mesh"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exdeorum/mechanical_hammer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:iron_block"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "I": {
          "item": "enderio:dark_steel_ingot"
        },
        "T": {
          "tag": "exdeorum:hammers"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:electromechanical_drive"
        },
        "$": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "@$I",
        "ITI",
        "!H#"
      ],
      "result": {
        "count": 1,
        "id": "exdeorum:mechanical_hammer"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exdeorum/mechanical_sieve",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:iron_block"
        },
        "G": {
          "item": "minecraft:glass"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "I": {
          "item": "minecraft:iron_bars"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:electromechanical_drive"
        },
        "$": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!G#",
        "$HI",
        "I@I"
      ],
      "result": {
        "count": 1,
        "id": "exdeorum:mechanical_sieve"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/flux_sieve",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "exmachinis",
      "key": {
        "b": {
          "item": "minecraft:iron_bars"
        },
        "h": {
          "item": "minecraft:hopper"
        },
        "i": {
          "tag": "c:storage_blocks/iron"
        },
        "s": {
          "item": "exdeorum:mechanical_sieve"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@bb",
        "bsb",
        "!hi"
      ],
      "result": {
        "count": 1,
        "id": "exmachinis:flux_sieve"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/flux_hammer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "exmachinis",
      "key": {
        "d": {
          "item": "exdeorum:mechanical_hammer"
        },
        "g": {
          "tag": "c:glass_panes"
        },
        "h": {
          "item": "minecraft:hopper"
        },
        "i": {
          "tag": "c:storage_blocks/iron"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@gg",
        "gdg",
        "!hi"
      ],
      "result": {
        "count": 1,
        "id": "exmachinis:flux_hammer"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/flux_compactor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "exmachinis",
      "key": {
        "c": {
          "item": "minecraft:comparator"
        },
        "h": {
          "item": "minecraft:hopper"
        },
        "i": {
          "tag": "c:storage_blocks/iron"
        },
        "p": {
          "item": "minecraft:piston"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@i",
        "pcp",
        "ihi"
      ],
      "result": {
        "count": 1,
        "id": "exmachinis:flux_compactor"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/diamond_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "exmachinis",
      "key": {
        "n": {
          "tag": "c:gems/diamond"
        },
        "u": {
          "item": "exmachinis:gold_upgrade"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!u@",
        "$un",
        "nun"
      ],
      "result": {
        "count": 1,
        "id": "exmachinis:diamond_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/netherite_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "exmachinis",
      "key": {
        "n": {
          "tag": "c:ingots/netherite"
        },
        "u": {
          "item": "exmachinis:diamond_upgrade"
        },
        "!": {
          "item": "kubejs:stellar_alloy"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!u@",
        "$un",
        "nun"
      ],
      "result": {
        "count": 1,
        "id": "exmachinis:netherite_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/aluminum_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:aluminum_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "immersiveengineering:ore_aluminum"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/cobalt_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:cobalt_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "modernfoundry:cobalt_ore"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/lead_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:lead_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "mekanism:lead_ore"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/nickel_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:nickel_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "immersiveengineering:ore_nickel"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/osmium_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:osmium_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "mekanism:osmium_ore"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/silver_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:silver_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "immersiveengineering:ore_silver"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/tin_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:tin_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "mekanism:tin_ore"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/uranium_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:uranium_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "mekanism:uranium_ore"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/exmachinis/compacting/zinc_ore",
    "recipe": {
      "type": "exmachinis:compacting",
      "input": {
        "count": 4,
        "ingredient": {
          "item": "exdeorum:zinc_ore_chunk"
        }
      },
      "output": {
        "count": 1,
        "id": "railcraft:zinc_ore"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/recipe_fan",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:ingots/iron"
        },
        "S": {
          "item": "minecraft:stone_slab"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:electromechanical_drive"
        }
      },
      "pattern": [
        "@IS",
        "I!I",
        "SIS"
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:fan"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/recipe_saw",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "S": {
          "item": "minecraft:iron_sword"
        },
        "V": {
          "item": "mob_grinding_utils:spikes"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:electromechanical_drive"
        },
        "$": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "$@S",
        "V!V",
        "DID"
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:saw"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/recipe_entity_spawner",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "E": {
          "item": "minecraft:ender_eye"
        },
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "X": {
          "item": "mob_grinding_utils:solid_xp_block"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:arcane_conduit"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@$E",
        "X!X",
        "IPI"
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:entity_spawner"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/recipe_solidifier",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "mob_grinding_utils:entity_conveyor"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "T": {
          "item": "mob_grinding_utils:tank"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@P$",
        "C!C",
        " T "
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:xpsolidifier"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/recipe_saw_upgrade_looting",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:nuggets/gold"
        },
        "L": {
          "tag": "c:dyes/blue"
        },
        "!": {
          "item": "apotheosis:gem_dust"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@$G",
        "L!L",
        "GLG"
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:saw_upgrade_looting"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/recipe_saw_upgrade_beheading",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:nuggets/gold"
        },
        "H": {
          "item": "minecraft:golden_helmet"
        },
        "I": {
          "item": "minecraft:iron_helmet"
        },
        "!": {
          "item": "enderio:dark_steel_ingot"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@H$",
        "I!I",
        "GHG"
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:saw_upgrade_beheading"
      }
    }
  },
  {
    "id": "kubejs:expert_v040/mob_grinding_utils/gm_chicken_feed",
    "recipe": {
      "type": "mob_grinding_utils:chicken_feed",
      "category": "misc",
      "ingredients": [
        {
          "tag": "c:seeds"
        },
        {
          "item": "mob_grinding_utils:mob_swab_used"
        },
        {
          "type": "mob_grinding_utils:fluid",
          "advanced": false,
          "value": {
            "Amount": 1000,
            "Tag": "c:experience"
          }
        },
        {
          "item": "kubejs:precision_mechanism"
        }
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:gm_chicken_feed"
      }
    }
  }
];
  recipes.forEach(function(row) { event.custom(row.recipe).id(row.id); });
});
