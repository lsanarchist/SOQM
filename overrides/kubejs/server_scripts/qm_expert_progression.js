// Generated 0.4.3: exact IDs; native serializers and batch outputs retained.
ServerEvents.recipes(function(event) {
  var removeIds = ["ad_astra:calorite_engine", "ad_astra:compressor", "ad_astra:desh_engine", "ad_astra:nasa_workbench", "ad_astra:ostrum_engine", "ad_astra:steel_engine", "ae2:network/blocks/controller", "ae2:network/blocks/crystal_processing_charger", "ae2:network/blocks/energy_energy_acceptor", "ae2:network/blocks/inscribers", "ae2:network/blocks/quantum_ring", "ae2:network/blocks/spatial_io_port", "ae2:network/blocks/storage_drive", "ae2:network/crafting/cpu_crafting_unit", "ae2:network/crafting/molecular_assembler", "avaritia:cosmic_meatballs", "avaritia:extreme_crafting_table", "avaritia:infinity_ingot", "avaritia:neutron_collector", "avaritia:neutron_compressor", "avaritia:ultimate_stew", "bigreactors:reactor/basic/controller_ingots_uranium", "bigreactors:reactor/basic/controller_ingots_yellorium", "bigreactors:reactor/reinforced/controller_ingots_uranium", "bigreactors:reactor/reinforced/controller_ingots_yellorium", "botania:alfheim_portal", "botania:runic_altar", "botania:runic_altar_alt", "botania:terra_plate", "buildcraftbuilders:quarry", "buildcraftfactory:pump", "buildcraftsilicon:advanced_crafting_table", "buildcraftsilicon:assembly_table", "buildcraftsilicon:integration_table", "buildcraftsilicon:programming_table", "enderio:sag_mill", "enderio:void_chassis", "evilcraft:crafting/blood_infuser", "evilcraft:crafting/purifier", "immersiveengineering:crafting/capacitor_hv", "immersiveengineering:crafting/capacitor_mv", "industrialforegoing:dissolution_chamber/advanced_machine_frame", "industrialforegoing:dissolution_chamber/simple_machine_frame", "industrialforegoing:dissolution_chamber/supreme_machine_frame", "industrialforegoing:machine_frame_pity", "mekanism:atomic_disassembler", "mekanism:chemical_crystallizer", "mekanism:chemical_dissolution_chamber", "mekanism:control_circuit/advanced", "mekanism:control_circuit/elite", "mekanism:control_circuit/infused_advanced", "mekanism:control_circuit/infused_elite", "mekanism:control_circuit/infused_ultimate", "mekanism:control_circuit/ultimate", "mekanism:digital_miner", "mekanism:meka_tool", "mekanism:mekasuit_bodyarmor", "mekanism:mekasuit_boots", "mekanism:mekasuit_helmet", "mekanism:mekasuit_pants", "mekanism:metallurgic_infuser", "mekanism:pressurized_reaction_chamber", "mekanism:quantum_entangloporter", "mekanism:steel_casing", "mekanism:supercharged_coil", "mekanismgenerators:fission_reactor/casing", "mekanismgenerators:generator/gas_burning", "mekanismgenerators:generator/heat", "mekanismgenerators:generator/solar", "mekanismgenerators:generator/wind", "mekanismgenerators:reactor/controller", "railcraft:electric_locomotive", "railcraft:powered_rolling_machine", "railcraft:steam_locomotive", "rftoolsbase:machine_frame", "rftoolsbuilder:builder", "rftoolsdim:dimension_builder", "rftoolsdim:enscriber", "rftoolsutility:matter_receiver", "rftoolsutility:matter_transmitter"];
  removeIds.forEach(function(id) { event.remove({id:id}); });
  var recipes = [
  {
    "id": "kubejs:expert/kubejs/industrial_chassis",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "PIP",
        "CRC",
        "PIP"
      ],
      "key": {
        "P": {
          "item": "immersiveengineering:plate_steel"
        },
        "I": {
          "item": "immersiveengineering:treated_wood_horizontal"
        },
        "C": {
          "item": "immersiveengineering:wirecoil_copper"
        },
        "R": {
          "item": "immersiveengineering:component_iron"
        }
      },
      "result": {
        "id": "kubejs:industrial_chassis",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/kubejs/precision_mechanism",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "ECE",
        "RMR",
        "DCD"
      ],
      "key": {
        "E": {
          "item": "enderio:redstone_alloy_ingot"
        },
        "C": {
          "item": "ae2:calculation_processor"
        },
        "R": {
          "item": "mekanism:alloy_infused"
        },
        "M": {
          "item": "kubejs:industrial_chassis"
        },
        "D": {
          "item": "kubejs:electromechanical_drive"
        }
      },
      "result": {
        "id": "kubejs:precision_mechanism",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/kubejs/dimensional_matrix",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "VTV",
        "PMP",
        "FTF"
      ],
      "key": {
        "V": {
          "item": "enderio:vibrant_alloy_ingot"
        },
        "T": {
          "item": "botania:terrasteel_ingot"
        },
        "P": {
          "item": "kubejs:precision_mechanism"
        },
        "M": {
          "item": "aether:enchanted_gravitite"
        },
        "F": {
          "item": "twilightforest:fiery_ingot"
        }
      },
      "result": {
        "id": "kubejs:dimensional_matrix",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/kubejs/stellar_alloy",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "DPD",
        "ECE",
        "DPD"
      ],
      "key": {
        "D": {
          "item": "ad_astra:desh_plate"
        },
        "P": {
          "item": "kubejs:dimensional_matrix"
        },
        "E": {
          "item": "divinerpg:eden_chunk"
        },
        "C": {
          "item": "mekanism:elite_control_circuit"
        }
      },
      "result": {
        "id": "kubejs:stellar_alloy",
        "count": 4
      }
    }
  },
  {
    "id": "kubejs:expert/kubejs/mythic_circuit",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "GUG",
        "MSM",
        "GUG"
      ],
      "key": {
        "G": {
          "item": "divinerpg:mortum_chunk"
        },
        "U": {
          "item": "mekanism:ultimate_control_circuit"
        },
        "M": {
          "item": "botania:gaia_ingot"
        },
        "S": {
          "item": "kubejs:stellar_alloy"
        }
      },
      "result": {
        "id": "kubejs:mythic_circuit",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/steel_casing",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:glass_blocks/cheap"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "S!S",
        "GOG",
        "SGS"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:steel_casing"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/metallurgic_infuser",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:furnace"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!#I",
        "ROR",
        "I#I"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:metallurgic_infuser"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanismgenerators/generator/heat",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:ingots/copper"
        },
        "F": {
          "item": "minecraft:furnace"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "W": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "I!I",
        "WOW",
        "CFC"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:heat_generator"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/inscribers",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "item": "minecraft:piston"
        },
        "c": {
          "tag": "c:ingots/copper"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "aba",
        "c!a",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:inscriber"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/crystal_processing_charger",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "tag": "c:ingots/copper"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "aba",
        "a! ",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:charger"
      }
    }
  },
  {
    "id": "kubejs:expert/enderio/void_chassis",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:dusts/grains_of_infinity"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "IGI",
        "G!G",
        "IGI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:void_chassis"
      }
    }
  },
  {
    "id": "kubejs:expert/industrialforegoing/machine_frame_pity",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:machine_frame_pity"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:ingots/iron"
        },
        "R": {
          "tag": "c:storage_blocks/redstone"
        },
        "W": {
          "tag": "minecraft:logs"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "W!W",
        "IRI",
        "WIW"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:machine_frame_pity"
      }
    }
  },
  {
    "id": "kubejs:expert/ad_astra/compressor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:iron_ingot"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "#P#",
        "#!#",
        "#P#"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:compressor"
      }
    }
  },
  {
    "id": "kubejs:expert/railcraft/steam_locomotive",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "railcraft:iron_tank_wall"
        },
        "b": {
          "item": "railcraft:blast_furnace_bricks"
        },
        "c": {
          "item": "minecraft:iron_bars"
        },
        "d": {
          "item": "minecraft:minecart"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:electromechanical_drive"
        }
      },
      "pattern": [
        "!@b",
        "aab",
        "cdd"
      ],
      "result": {
        "count": 1,
        "id": "railcraft:steam_locomotive"
      }
    }
  },
  {
    "id": "kubejs:expert/evilcraft/crafting/blood_infuser",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "CIC",
        "CCC"
      ],
      "key": {
        "C": {
          "tag": "c:cobblestones"
        },
        "I": {
          "item": "evilcraft:blood_infusion_core"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:arcane_conduit"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "evilcraft:blood_infuser"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/control_circuit/advanced",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "mekanism:basic_control_circuit"
        },
        {
          "item": "mekanism:basic_control_circuit"
        },
        {
          "item": "mekanism:alloy_infused"
        },
        {
          "item": "mekanism:alloy_infused"
        },
        {
          "item": "mekanism:alloy_infused"
        },
        {
          "item": "mekanism:alloy_infused"
        },
        {
          "item": "kubejs:precision_mechanism"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "id": "mekanism:advanced_control_circuit",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/control_circuit/elite",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "mekanism:advanced_control_circuit"
        },
        {
          "item": "mekanism:advanced_control_circuit"
        },
        {
          "item": "mekanism:alloy_reinforced"
        },
        {
          "item": "mekanism:alloy_reinforced"
        },
        {
          "item": "mekanism:alloy_reinforced"
        },
        {
          "item": "mekanism:alloy_reinforced"
        },
        {
          "item": "kubejs:dimensional_matrix"
        },
        {
          "item": "kubejs:quantum_processor"
        }
      ],
      "result": {
        "id": "mekanism:elite_control_circuit",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/control_circuit/ultimate",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "mekanism:elite_control_circuit"
        },
        {
          "item": "mekanism:elite_control_circuit"
        },
        {
          "item": "mekanism:alloy_atomic"
        },
        {
          "item": "mekanism:alloy_atomic"
        },
        {
          "item": "mekanism:alloy_atomic"
        },
        {
          "item": "mekanism:alloy_atomic"
        },
        {
          "item": "kubejs:stellar_alloy"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:quantum_processor"
        }
      ],
      "result": {
        "id": "mekanism:ultimate_control_circuit",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert/mekanismgenerators/generator/wind",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@! ",
        "OAO",
        "ECE"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:wind_generator"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanismgenerators/generator/solar",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanismgenerators:solar_panel"
        },
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@!#",
        "AIA",
        "OEO"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:solar_generator"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/pressurized_reaction_chamber",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:dynamic_tank"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "P": {
          "item": "mekanism:enrichment_chamber"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:sealed_process_chamber"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@!S",
        "$PC",
        "T#T"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:pressurized_reaction_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/energy_energy_acceptor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "item": "ae2:quartz_glass"
        },
        "c": {
          "tag": "c:ingots/copper"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@!a",
        "bcb",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:energy_acceptor"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/storage_drive",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "kubejs:precision_mechanism"
        },
        "C": {
          "item": "ae2:fluix_glass_cable"
        },
        "B": {
          "item": "ironchest:gold_chest"
        },
        "S": {
          "item": "ae2:engineering_processor"
        },
        "A": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "IPI",
        "CBC",
        "SAS"
      ],
      "result": {
        "count": 1,
        "id": "ae2:drive"
      }
    }
  },
  {
    "id": "kubejs:expert/enderio/sag_mill",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "minecraft:flint"
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
        "P": {
          "item": "minecraft:piston"
        },
        "V": {
          "item": "enderio:void_chassis"
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
        "@FG",
        "!VI",
        "$PO"
      ],
      "result": {
        "count": 1,
        "id": "enderio:sag_mill"
      }
    }
  },
  {
    "id": "kubejs:expert/rftoolsbase/machine_frame",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "g": {
          "item": "minecraft:gold_nugget"
        },
        "i": {
          "tag": "c:ingots/iron"
        },
        "l": {
          "tag": "c:dyes/blue"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "i!i",
        "g g",
        "ili"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsbase:machine_frame"
      }
    }
  },
  {
    "id": "kubejs:expert/buildcraftfactory/pump",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "key": {
        "b": {
          "item": "minecraft:bucket"
        },
        "g": {
          "item": "buildcraftcore:gears/gear_iron"
        },
        "i": {
          "item": "minecraft:iron_ingot"
        },
        "t": {
          "item": "buildcraftfactory:tank"
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
        "@!$",
        "igi",
        "tbt"
      ],
      "result": {
        "id": "buildcraftfactory:pump"
      }
    }
  },
  {
    "id": "kubejs:expert/buildcraftsilicon/assembly_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!D@",
        "ORO",
        "OGO"
      ],
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "D": {
          "item": "minecraft:diamond"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "G": {
          "item": "buildcraftcore:gears/gear_diamond"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "buildcraftsilicon:assembly_table"
      }
    }
  },
  {
    "id": "kubejs:expert/buildcraftsilicon/integration_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!G@",
        "ORO",
        "ODO"
      ],
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "G": {
          "item": "minecraft:gold_ingot"
        },
        "R": {
          "item": "buildcraftsilicon:redstone_chipset/iron"
        },
        "D": {
          "item": "buildcraftcore:gears/gear_diamond"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "buildcraftsilicon:integration_table"
      }
    }
  },
  {
    "id": "kubejs:expert/buildcraftsilicon/programming_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!B@",
        "ORO",
        "OGO"
      ],
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "B": {
          "item": "buildcraftrobotics:redstone_board"
        },
        "R": {
          "item": "buildcraftsilicon:redstone_chipset/red"
        },
        "G": {
          "item": "buildcraftcore:gears/gear_gold"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "buildcraftsilicon:programming_table"
      }
    }
  },
  {
    "id": "kubejs:expert/railcraft/powered_rolling_machine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:gears/steel"
        },
        "b": {
          "item": "minecraft:piston"
        },
        "c": {
          "item": "minecraft:crafting_table"
        },
        "d": {
          "item": "railcraft:charge_motor"
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
        "!@$",
        "bcb",
        "ada"
      ],
      "result": {
        "count": 1,
        "id": "railcraft:powered_rolling_machine"
      }
    }
  },
  {
    "id": "kubejs:expert/railcraft/electric_locomotive",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "minecraft:redstone_lamp"
        },
        "b": {
          "tag": "c:plates/steel"
        },
        "c": {
          "item": "railcraft:charge_motor"
        },
        "d": [
          {
            "item": "railcraft:nickel_iron_battery"
          },
          {
            "item": "railcraft:nickel_zinc_battery"
          }
        ],
        "e": {
          "tag": "c:gears/steel"
        },
        "f": {
          "item": "minecraft:minecart"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "ab!",
        "@dc",
        "efe"
      ],
      "result": {
        "count": 1,
        "id": "railcraft:electric_locomotive"
      }
    }
  },
  {
    "id": "kubejs:expert/immersiveengineering/crafting/capacitor_mv",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:plates/nickel"
        },
        "c": {
          "tag": "c:plates/iron"
        },
        "e": {
          "tag": "c:ingots/electrum"
        },
        "w": {
          "item": "immersiveengineering:basic_engineering"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "e!e",
        "awc",
        "@  "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:capacitor_mv"
      }
    }
  },
  {
    "id": "kubejs:expert/botania/runic_altar",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "P": {
          "item": "botania:mana_pearl"
        },
        "S": {
          "item": "botania:livingrock"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:arcane_conduit"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "S!S",
        "SPS",
        "@$ "
      ],
      "result": {
        "count": 1,
        "id": "botania:runic_altar"
      }
    }
  },
  {
    "id": "kubejs:expert/botania/runic_altar_alt",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "P": {
          "tag": "botania:mana_diamond_gems"
        },
        "S": {
          "item": "botania:livingrock"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:arcane_conduit"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "S!S",
        "SPS",
        "@$ "
      ],
      "result": {
        "count": 1,
        "id": "botania:runic_altar"
      }
    }
  },
  {
    "id": "kubejs:expert/botania/terra_plate",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "0": {
          "item": "botania:rune_water"
        },
        "1": {
          "item": "botania:rune_fire"
        },
        "2": {
          "item": "botania:rune_earth"
        },
        "3": {
          "item": "botania:rune_air"
        },
        "8": {
          "item": "botania:rune_mana"
        },
        "L": {
          "item": "minecraft:lapis_block"
        },
        "M": {
          "tag": "botania:manasteel_blocks"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "L!L",
        "0M1",
        "283"
      ],
      "result": {
        "count": 1,
        "id": "botania:terra_plate"
      }
    }
  },
  {
    "id": "kubejs:expert/evilcraft/crafting/purifier",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "S!S",
        "@I$",
        "GBG"
      ],
      "key": {
        "S": {
          "item": "evilcraft:hardened_blood_shard"
        },
        "G": {
          "item": "evilcraft:dark_gem"
        },
        "I": {
          "item": "evilcraft:blood_infusion_core"
        },
        "B": {
          "item": "evilcraft:dark_block"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:arcane_conduit"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "evilcraft:purifier"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/controller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "industrialforegoing:machine_frame_simple"
        },
        "E": {
          "item": "ae2:engineering_processor"
        },
        "C": {
          "item": "rftoolsutility:crafter1"
        },
        "A": {
          "item": "ae2:annihilation_core"
        },
        "U": {
          "item": "ae2:drive"
        },
        "B": {
          "item": "ae2:formation_core"
        },
        "M": {
          "item": "kubejs:dimensional_matrix"
        },
        "D": {
          "item": "kubejs:control_assembly"
        },
        "P": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "FEC",
        "AUB",
        "MDP"
      ],
      "result": {
        "count": 1,
        "id": "ae2:controller"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/crafting/cpu_crafting_unit",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "minecraft:iron_block"
        },
        "b": {
          "item": "ae2:calculation_processor"
        },
        "c": {
          "item": "ae2:fluix_glass_cable"
        },
        "d": {
          "item": "ae2:logic_processor"
        },
        "!": {
          "item": "kubejs:industrial_chassis"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:crafter"
        }
      },
      "pattern": [
        "@!$",
        "cdc",
        "%ba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:crafting_unit"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/crafting/molecular_assembler",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "minecraft:iron_block"
        },
        "b": {
          "item": "ae2:quartz_glass"
        },
        "c": {
          "item": "ae2:annihilation_core"
        },
        "d": {
          "item": "rftoolsutility:crafter1"
        },
        "e": {
          "item": "ae2:formation_core"
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
        "@!$",
        "cde",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:molecular_assembler"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/digital_miner",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "R": {
          "item": "mekanism:robit"
        },
        "S": {
          "item": "mekanism:logistical_sorter"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "X": {
          "item": "rftoolsbuilder:builder"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "buildcraftfactory:mining_well"
        }
      },
      "pattern": [
        "@!A",
        "%RS",
        "TXT"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:digital_miner"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/atomic_disassembler",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "equipment",
      "key": {
        "#": {
          "tag": "mekanism:alloys/atomic"
        },
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "tag": "c:ingots/refined_obsidian"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "AEA",
        "A#A",
        "!I "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:atomic_disassembler"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanismgenerators/generator/gas_burning",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "item": "mekanism:electrolytic_core"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:sealed_process_chamber"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!$@",
        "XCX",
        "OAO"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:gas_burning_generator"
      }
    }
  },
  {
    "id": "kubejs:expert/rftoolsbuilder/builder",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "buildcraftfactory:mining_well"
        },
        "&": {
          "item": "rftoolsutility:crafter2"
        }
      },
      "pattern": [
        "@!$",
        "rFr",
        "%r&"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsbuilder:builder"
      }
    }
  },
  {
    "id": "kubejs:expert/rftoolsutility/matter_transmitter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "i": {
          "tag": "c:ingots/iron"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "o!o",
        "rFr",
        "@ii"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:matter_transmitter"
      }
    }
  },
  {
    "id": "kubejs:expert/rftoolsutility/matter_receiver",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "i": {
          "tag": "c:ingots/iron"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "@ii",
        "rFr",
        "o!o"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:matter_receiver"
      }
    }
  },
  {
    "id": "kubejs:expert/buildcraftbuilders/quarry",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "@!$",
        "%ig",
        "&pd"
      ],
      "key": {
        "i": {
          "item": "buildcraftcore:gears/gear_iron"
        },
        "g": {
          "item": "buildcraftcore:gears/gear_gold"
        },
        "d": {
          "item": "buildcraftcore:gears/gear_diamond"
        },
        "p": {
          "item": "minecraft:diamond_pickaxe"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "$": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "buildcraftfactory:mining_well"
        },
        "&": {
          "item": "rftoolsbuilder:builder"
        }
      },
      "result": {
        "id": "buildcraftbuilders:quarry"
      }
    }
  },
  {
    "id": "kubejs:expert/buildcraftsilicon/advanced_crafting_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!T@",
        "$CO",
        "ORO"
      ],
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "T": {
          "item": "minecraft:crafting_table"
        },
        "C": {
          "item": "minecraft:chest"
        },
        "R": {
          "item": "buildcraftsilicon:redstone_chipset/red"
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
      "result": {
        "id": "buildcraftsilicon:advanced_crafting_table"
      }
    }
  },
  {
    "id": "kubejs:expert/immersiveengineering/crafting/capacitor_hv",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:plates/aluminum"
        },
        "c": {
          "tag": "c:plates/hop_graphite"
        },
        "e": {
          "tag": "c:ingots/aluminum"
        },
        "w": {
          "item": "immersiveengineering:basic_engineering"
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
        "e!e",
        "awc",
        "@$ "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:capacitor_hv"
      }
    }
  },
  {
    "id": "kubejs:expert/botania/alfheim_portal",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "T": {
          "tag": "botania:terrasteel_nuggets"
        },
        "W": {
          "tag": "botania:livingwood_logs"
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
        "!T@",
        "$TW",
        "WTW"
      ],
      "result": {
        "count": 1,
        "id": "botania:alfheim_portal"
      }
    }
  },
  {
    "id": "kubejs:expert/ad_astra/nasa_workbench",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:steel_plates"
        },
        "B": {
          "tag": "ad_astra:steel_blocks"
        },
        "C": {
          "item": "minecraft:crafting_table"
        },
        "T": {
          "item": "minecraft:redstone_torch"
        },
        "|": {
          "tag": "ad_astra:iron_rods"
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
        "@!|",
        "$CT",
        "#B#"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:nasa_workbench"
      }
    }
  },
  {
    "id": "kubejs:expert/ad_astra/steel_engine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:steel_plates"
        },
        "E": {
          "item": "ad_astra:engine_frame"
        },
        "F": {
          "item": "ad_astra:fan"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "###",
        "#E#",
        "!F "
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:steel_engine"
      }
    }
  },
  {
    "id": "kubejs:expert/bigreactors/reactor/basic/controller_ingots_uranium",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "item": "bigreactors:basic_reactorcasing"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "X": {
          "item": "minecraft:comparator"
        },
        "Y": {
          "tag": "c:ingots/uranium"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        },
        "$": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "@X$",
        "YDY",
        "C!C"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:basic_reactorcontroller"
      }
    }
  },
  {
    "id": "kubejs:expert/bigreactors/reactor/basic/controller_ingots_yellorium",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "item": "bigreactors:basic_reactorcasing"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "X": {
          "item": "minecraft:comparator"
        },
        "Y": {
          "tag": "c:ingots/yellorium"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        },
        "$": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "@X$",
        "YDY",
        "C!C"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:basic_reactorcontroller"
      }
    }
  },
  {
    "id": "kubejs:expert/ad_astra/desh_engine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:desh_plates"
        },
        "E": {
          "item": "ad_astra:steel_engine"
        },
        "F": {
          "item": "ad_astra:fan"
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
        "$##",
        "#E#",
        "!F@"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:desh_engine"
      }
    }
  },
  {
    "id": "kubejs:expert/ad_astra/ostrum_engine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:ostrum_plates"
        },
        "E": {
          "item": "ad_astra:desh_engine"
        },
        "F": {
          "item": "ad_astra:fan"
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
        "$##",
        "#E#",
        "!F@"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:ostrum_engine"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/quantum_ring",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "item": "ae2:logic_processor"
        },
        "c": {
          "item": "ae2:engineering_processor"
        },
        "d": {
          "item": "ae2:energy_cell"
        },
        "e": {
          "tag": "ae2:smart_dense_cable"
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
        "@!$",
        "cde",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:quantum_ring"
      }
    }
  },
  {
    "id": "kubejs:expert/ae2/network/blocks/spatial_io_port",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:glass_blocks/cheap"
        },
        "b": {
          "item": "ae2:fluix_glass_cable"
        },
        "c": {
          "item": "ae2:io_port"
        },
        "d": {
          "item": "rftoolsutility:matter_receiver"
        },
        "e": {
          "item": "ae2:engineering_processor"
        },
        "!": {
          "item": "kubejs:stellar_alloy"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "ae2:controller"
        }
      },
      "pattern": [
        "@!a",
        "$cb",
        "%ed"
      ],
      "result": {
        "count": 1,
        "id": "ae2:spatial_io_port"
      }
    }
  },
  {
    "id": "kubejs:expert/rftoolsdim/enscriber",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "minecraft:crafting_table"
        },
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "p": {
          "item": "minecraft:paper"
        },
        "u": {
          "item": "rftoolsdim:empty_dimlet"
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
        "!$@",
        "CFC",
        "pup"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsdim:enscriber"
      }
    }
  },
  {
    "id": "kubejs:expert/bigreactors/reactor/reinforced/controller_ingots_uranium",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "item": "bigreactors:reinforced_reactorcasing"
        },
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "X": {
          "item": "minecraft:comparator"
        },
        "Y": {
          "tag": "c:ingots/uranium"
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
      "pattern": [
        "@X$",
        "YDY",
        "C!C"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorcontroller"
      }
    }
  },
  {
    "id": "kubejs:expert/bigreactors/reactor/reinforced/controller_ingots_yellorium",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "item": "bigreactors:reinforced_reactorcasing"
        },
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "X": {
          "item": "minecraft:comparator"
        },
        "Y": {
          "tag": "c:ingots/yellorium"
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
      "pattern": [
        "@X$",
        "YDY",
        "C!C"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorcontroller"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/chemical_dissolution_chamber",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "tag": "c:ingots/refined_obsidian"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:sealed_process_chamber"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!$@",
        "CXC",
        "ITI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chemical_dissolution_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/chemical_crystallizer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "c:gems/fluorite"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "tag": "c:ingots/refined_obsidian"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:precision_mechanism"
        },
        "@": {
          "item": "kubejs:sealed_process_chamber"
        },
        "$": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!$@",
        "CXC",
        "IAI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chemical_crystallizer"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/quantum_entangloporter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "tag": "c:ingots/refined_obsidian"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "@!I",
        "ATA",
        "ICI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:quantum_entangloporter"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanismgenerators/fission_reactor/casing",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:ingots/lead"
        },
        "X": {
          "item": "mekanism:steel_casing"
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
      "pattern": [
        "!I@",
        "IXI",
        "$I "
      ],
      "result": {
        "count": 4,
        "id": "mekanismgenerators:fission_reactor_casing"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanismgenerators/reactor/controller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "F": {
          "item": "mekanismgenerators:fusion_reactor_frame"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "C!C",
        "@TF",
        "FFF"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:fusion_reactor_controller"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/supercharged_coil",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:laser"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "c": {
          "tag": "c:ingots/copper"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        },
        "$": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "$!c",
        "C#C",
        "@AA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:supercharged_coil"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/meka_tool",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "#": {
          "item": "mekanism:atomic_disassembler"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "E": {
          "item": "mekanism:basic_induction_cell"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "o": {
          "item": "mekanism:configurator"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!oC",
        "@#P",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:meka_tool"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/mekasuit_helmet",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "#": {
          "item": "minecraft:netherite_helmet"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "E": {
          "item": "mekanism:basic_induction_cell"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!C@",
        "P#P",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:mekasuit_helmet"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/mekasuit_bodyarmor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "#": {
          "item": "minecraft:netherite_chestplate"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "E": {
          "item": "mekanism:basic_induction_cell"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!C@",
        "P#P",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:mekasuit_bodyarmor"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/mekasuit_pants",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "#": {
          "item": "minecraft:netherite_leggings"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "E": {
          "item": "mekanism:basic_induction_cell"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!C@",
        "P#P",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:mekasuit_pants"
      }
    }
  },
  {
    "id": "kubejs:expert/mekanism/mekasuit_boots",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "#": {
          "item": "minecraft:netherite_boots"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "E": {
          "item": "mekanism:basic_induction_cell"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!C@",
        "P#P",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:mekasuit_boots"
      }
    }
  },
  {
    "id": "kubejs:expert/ad_astra/calorite_engine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:calorite_plates"
        },
        "E": {
          "item": "ad_astra:ostrum_engine"
        },
        "F": {
          "item": "ad_astra:fan"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        },
        "$": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "$##",
        "#E#",
        "!F@"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:calorite_engine"
      }
    }
  },
  {
    "id": "kubejs:expert/rftoolsdim/dimension_builder",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "d": {
          "item": "minecraft:diamond"
        },
        "g": {
          "item": "minecraft:gold_ingot"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "$": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "$!o",
        "dFd",
        "@gg"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsdim:dimension_builder"
      }
    }
  },
  {
    "id": "kubejs:expert/industrialforegoing/dissolution_chamber/simple_machine_frame",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:machine_frame_simple"
        }
      ],
      "type": "industrialforegoing:dissolution_chamber",
      "input": [
        {
          "tag": "c:plastics"
        },
        {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        {
          "tag": "c:plastics"
        },
        {
          "item": "minecraft:nether_brick"
        },
        {
          "item": "minecraft:nether_brick"
        },
        {
          "item": "kubejs:precision_mechanism"
        },
        {
          "tag": "c:gears/gold"
        },
        {
          "tag": "c:ingots/iron"
        }
      ],
      "inputFluid": {
        "amount": 250,
        "fluid": "industrialforegoing:latex"
      },
      "output": {
        "count": 1,
        "id": "industrialforegoing:machine_frame_simple"
      },
      "processingTime": 300
    }
  },
  {
    "id": "kubejs:expert/industrialforegoing/dissolution_chamber/advanced_machine_frame",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:machine_frame_advanced"
        }
      ],
      "type": "industrialforegoing:dissolution_chamber",
      "input": [
        {
          "tag": "c:plastics"
        },
        {
          "tag": "industrialforegoing:machine_frame/simple"
        },
        {
          "tag": "c:plastics"
        },
        {
          "item": "minecraft:netherite_scrap"
        },
        {
          "item": "minecraft:netherite_scrap"
        },
        {
          "item": "kubejs:dimensional_matrix"
        },
        {
          "tag": "c:gears/diamond"
        },
        {
          "tag": "c:ingots/gold"
        }
      ],
      "inputFluid": {
        "amount": 500,
        "fluid": "industrialforegoing:pink_slime"
      },
      "output": {
        "count": 1,
        "id": "industrialforegoing:machine_frame_advanced"
      },
      "processingTime": 300
    }
  },
  {
    "id": "kubejs:expert/industrialforegoing/dissolution_chamber/supreme_machine_frame",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:machine_frame_supreme"
        }
      ],
      "type": "industrialforegoing:dissolution_chamber",
      "input": [
        {
          "tag": "c:plastics"
        },
        {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        {
          "tag": "c:plastics"
        },
        {
          "item": "minecraft:netherite_ingot"
        },
        {
          "item": "minecraft:netherite_ingot"
        },
        {
          "item": "kubejs:stellar_alloy"
        },
        {
          "tag": "c:gears/diamond"
        },
        {
          "tag": "c:gems/diamond"
        }
      ],
      "inputFluid": {
        "amount": 135,
        "fluid": "industrialforegoing:ether_gas"
      },
      "output": {
        "count": 1,
        "id": "industrialforegoing:machine_frame_supreme"
      },
      "processingTime": 300
    }
  },
  {
    "id": "kubejs:expert/industrialforegoing/plastic_from_hdpe",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "mekanism:hdpe_sheet"
        }
      ],
      "result": {
        "id": "industrialforegoing:plastic",
        "count": 3
      }
    }
  },
  {
    "id": "kubejs:expert/avaritia/extreme_crafting_table",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "a": {
          "item": "avaritia:double_compressed_crafting_table"
        },
        "b": {
          "item": "minecraft:lodestone"
        },
        "c": {
          "item": "avaritia:diamond_lattice"
        },
        "d": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "e": {
          "item": "avaritia:crystal_matrix"
        },
        "f": {
          "item": "minecraft:recovery_compass"
        },
        "g": {
          "item": "minecraft:dragon_egg"
        },
        "h": {
          "item": "minecraft:beacon"
        },
        "i": {
          "item": "minecraft:reinforced_deepslate"
        },
        "j": {
          "item": "minecraft:netherite_block"
        },
        "k": {
          "item": "minecraft:heart_of_the_sea"
        },
        "!": {
          "item": "kubejs:emc_core"
        }
      },
      "pattern": [
        "!ccfccb",
        "cddgddc",
        "cdihidc",
        "cdiaidc",
        "cdjkjdc",
        "cdddddc",
        "beeeeeb"
      ],
      "result": {
        "count": 1,
        "id": "avaritia:extreme_crafting_table"
      },
      "tier": 3
    }
  },
  {
    "id": "kubejs:expert/avaritia/neutron_collector",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "Q": {
          "item": "minecraft:quartz_block"
        },
        "R": {
          "item": "minecraft:redstone_block"
        },
        "!": {
          "item": "kubejs:emc_core"
        }
      },
      "pattern": [
        "!IQQQQQII",
        "I QQQQQ I",
        "I  RRR  I",
        "C RRRRR C",
        "I RRCRR I",
        "C RRRRR C",
        "I  RRR  I",
        "I       I",
        "IIICICIII"
      ],
      "result": {
        "count": 1,
        "id": "avaritia:neutron_collector"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert/avaritia/neutron_compressor",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "O": {
          "item": "avaritia:neutron"
        },
        "R": {
          "item": "minecraft:redstone_block"
        },
        "!": {
          "item": "kubejs:emc_core"
        }
      },
      "pattern": [
        "!IIHHHIII",
        "C N   N C",
        "I N   N I",
        "C N   N C",
        "RNN O NNR",
        "C N   N C",
        "I N   N I",
        "C N   N C",
        "IIICICIII"
      ],
      "result": {
        "count": 1,
        "id": "avaritia:neutron_compressor"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert/avaritia/infinity_ingot",
    "recipe": {
      "type": "avaritia:shaped_table",
      "key": {
        "C": {
          "item": "avaritia:crystal_matrix_ingot"
        },
        "N": {
          "item": "avaritia:neutron_ingot"
        },
        "X": {
          "item": "avaritia:infinity_catalyst"
        },
        "!": {
          "item": "kubejs:emc_core"
        }
      },
      "pattern": [
        "!NNNNNNNN",
        "NCXXCXXCN",
        "NXCCXCCXN",
        "NCXXCXXCN",
        "NNNNNNNNN"
      ],
      "result": {
        "count": 1,
        "id": "avaritia:infinity_ingot"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert/avaritia/ultimate_stew",
    "recipe": {
      "type": "avaritia:shapeless_table",
      "ingredients": [
        {
          "item": "minecraft:golden_carrot"
        },
        {
          "item": "minecraft:golden_carrot"
        },
        {
          "item": "minecraft:golden_carrot"
        },
        {
          "item": "minecraft:golden_carrot"
        },
        {
          "item": "botania:mana_pearl"
        },
        {
          "item": "botania:mana_pearl"
        },
        {
          "item": "avaritia:neutron_nugget"
        },
        {
          "item": "kubejs:mythic_circuit"
        }
      ],
      "result": {
        "count": 1,
        "id": "avaritia:ultimate_stew"
      },
      "tier": 4
    }
  },
  {
    "id": "kubejs:expert/avaritia/cosmic_meatballs",
    "recipe": {
      "type": "avaritia:shapeless_table",
      "ingredients": [
        {
          "item": "minecraft:cooked_beef"
        },
        {
          "item": "minecraft:cooked_beef"
        },
        {
          "item": "minecraft:cooked_beef"
        },
        {
          "item": "minecraft:cooked_beef"
        },
        {
          "item": "enderio:vibrant_alloy_ingot"
        },
        {
          "item": "enderio:vibrant_alloy_ingot"
        },
        {
          "item": "avaritia:neutron_nugget"
        },
        {
          "item": "kubejs:mythic_circuit"
        }
      ],
      "result": {
        "count": 1,
        "id": "avaritia:cosmic_meatballs"
      },
      "tier": 4
    }
  }
];
  recipes.forEach(function(row) { event.custom(row.recipe).id(row.id); });
});
