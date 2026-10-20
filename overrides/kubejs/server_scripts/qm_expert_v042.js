// Generated 0.4.3: exact IDs; native serializers and batch outputs retained.
ServerEvents.recipes(function(event) {
  var removeIds = ["ad_astra:cryo_freezer", "ad_astra:energizer", "ad_astra:fuel_refinery", "ad_astra:gravity_normalizer", "ad_astra:oxygen_distributor", "ad_astra:oxygen_loader", "ad_astra:solar_panel", "ad_astra:water_pump", "ae2:network/blocks/energy_dense_energy_cell", "ae2:network/blocks/energy_energy_acceptor_alt", "ae2:network/blocks/interfaces_interface", "ae2:network/blocks/interfaces_interface_alt", "ae2:network/blocks/interfaces_interface_part", "ae2:network/blocks/pattern_providers_interface", "ae2:network/blocks/pattern_providers_interface_alt", "ae2:network/blocks/pattern_providers_interface_part", "ae2:network/blocks/quantum_link", "ae2:network/blocks/spatial_io_pylon", "ae2:network/cells/item_storage_components_cell_16k_part", "ae2:network/cells/item_storage_components_cell_256k_part", "ae2:network/cells/item_storage_components_cell_4k_part", "ae2:network/cells/item_storage_components_cell_64k_part", "ae2:network/cells/spatial_components_0", "ae2:network/cells/spatial_components_1", "ae2:network/crafting/cpu_crafting_accelerator", "ae2:network/parts/export_bus", "ae2:network/parts/import_bus", "ae2:network/upgrade_wireless_crafting_terminal", "ae2:network/wireless_crafting_terminal", "apotheosis:augmenting_table", "apotheosis:gem_cutting_table", "apotheosis:reforging_table", "apotheosis:salvaging_table", "apotheosis:sigil_of_socketing", "apotheosis:simple_reforging_table", "apothic_enchanting:library", "bigreactors:reactor/basic/casing", "bigreactors:reactor/basic/fuelrod_ingots_uranium", "bigreactors:reactor/basic/fuelrod_ingots_yellorium", "bigreactors:reactor/reinforced/casing", "bigreactors:reactor/reinforced/casing_alt", "bigreactors:reactor/reinforced/casing_upgrade", "bigreactors:reactor/reinforced/casing_upgrade_alt", "bigreactors:reactor/reinforced/fuelrod_alt_ingots_uranium", "bigreactors:reactor/reinforced/fuelrod_alt_ingots_yellorium", "bigreactors:reactor/reinforced/fuelrod_ingots_uranium", "bigreactors:reactor/reinforced/fuelrod_ingots_yellorium", "botania:alchemy_catalyst", "botania:aura_ring", "botania:auto_crafting_halo", "botania:brewery", "botania:conjuration_catalyst", "botania:corporea_funnel", "botania:corporea_index", "botania:magnet_ring", "botania:mana_mirror", "botania:mana_pylon", "botania:spark", "botania:terra_axe", "botania:terra_pick", "botania:terra_sword", "buildcraftfactory:autowork_bench_1", "buildcraftfactory:autowork_bench_2", "buildcraftfactory:distiller", "buildcraftfactory:heat_exchange", "buildcraftfactory:mining_well", "buildcraftsilicon:laser", "enderio:advanced_capacitor_bank", "enderio:advanced_capacitor_bank_upgrade", "enderio:basic_capacitor_bank", "enderio:crafter", "enderio:double_layer_capacitor", "enderio:enchanter", "enderio:impulse_hopper", "enderio:octadic_capacitor", "enderio:painting_machine", "enderio:powered_spawner", "enderio:slice_and_splice", "enderio:soul_binder", "enderio:stirling_generator", "enderio:travel_anchor", "enderio:vat", "enderstorage:ender_chest", "enderstorage:ender_pouch", "enderstorage:ender_tank", "evilcraft:crafting/blood_chest", "evilcraft:crafting/colossal_blood_chest", "evilcraft:crafting/effortless_ring", "evilcraft:crafting/exalted_crafter", "evilcraft:crafting/sanguinary_environmental_accumulator", "evilcraft:crafting/spirit_furnace", "evilcraft:crafting/spirit_reanimator", "extrautilitiesreborn:advanced_chicken_ring_rcp", "extrautilitiesreborn:electrical_furnace_rcp", "extrautilitiesreborn:ender_quarry_rcp", "extrautilitiesreborn:ender_thermic_pump_rcp", "extrautilitiesreborn:mechanical_miner_rcp", "extrautilitiesreborn:mechnical_user_rcp", "extrautilitiesreborn:sorter_rcp", "extrautilitiesreborn:wireless_fe_battery_rcp", "immersiveengineering:crafting/blastfurnace_preheater", "immersiveengineering:crafting/buzzsaw", "immersiveengineering:crafting/charging_station", "immersiveengineering:crafting/drill", "immersiveengineering:crafting/electromagnet", "immersiveengineering:crafting/fluid_pump", "immersiveengineering:crafting/heavy_engineering", "immersiveengineering:crafting/item_batcher", "immersiveengineering:crafting/light_engineering", "immersiveengineering:crafting/railgun", "immersiveengineering:crafting/sample_drill", "immersiveengineering:crafting/sorter", "immersiveengineering:crafting/tesla_coil", "industrialforegoing:block_breaker", "industrialforegoing:block_placer", "industrialforegoing:dissolution_chamber", "industrialforegoing:enchantment_applicator", "industrialforegoing:enchantment_extractor", "industrialforegoing:fluid_collector", "industrialforegoing:fluid_laser_base", "industrialforegoing:fluid_placer", "industrialforegoing:fluid_sieving_machine", "industrialforegoing:laser_drill", "industrialforegoing:latex_processing_unit", "industrialforegoing:material_stonework_factory", "industrialforegoing:mob_crusher", "industrialforegoing:mob_duplicator", "industrialforegoing:mob_slaughter_factory", "industrialforegoing:ore_laser_base", "industrialforegoing:resourceful_furnace", "industrialforegoing:stasis_chamber", "industrialforegoing:wither_builder", "ironfurnaces:augments/augment_fuel", "ironfurnaces:augments/augment_speed", "kubejs:apotheosis/augmenting_table", "kubejs:apotheosis/reforging_table", "mekanism:antiprotonic_nucleosynthesizer", "mekanism:chargepad", "mekanism:chemical_infuser", "mekanism:chemical_injection_chamber", "mekanism:chemical_oxidizer", "mekanism:chemical_washer", "mekanism:combiner", "mekanism:crusher", "mekanism:dimensional_stabilizer", "mekanism:electric_pump", "mekanism:electrolytic_separator", "mekanism:energized_smelter", "mekanism:energy_cube/advanced", "mekanism:energy_cube/basic", "mekanism:energy_cube/elite", "mekanism:energy_cube/ultimate", "mekanism:enrichment_chamber", "mekanism:factory/advanced/combining", "mekanism:factory/advanced/compressing", "mekanism:factory/advanced/crushing", "mekanism:factory/advanced/enriching", "mekanism:factory/advanced/infusing", "mekanism:factory/advanced/injecting", "mekanism:factory/advanced/purifying", "mekanism:factory/advanced/sawing", "mekanism:factory/advanced/smelting", "mekanism:factory/basic/combining", "mekanism:factory/basic/compressing", "mekanism:factory/basic/crushing", "mekanism:factory/basic/enriching", "mekanism:factory/basic/infusing", "mekanism:factory/basic/injecting", "mekanism:factory/basic/purifying", "mekanism:factory/basic/sawing", "mekanism:factory/basic/smelting", "mekanism:factory/elite/combining", "mekanism:factory/elite/compressing", "mekanism:factory/elite/crushing", "mekanism:factory/elite/enriching", "mekanism:factory/elite/infusing", "mekanism:factory/elite/injecting", "mekanism:factory/elite/purifying", "mekanism:factory/elite/sawing", "mekanism:factory/elite/smelting", "mekanism:factory/ultimate/combining", "mekanism:factory/ultimate/compressing", "mekanism:factory/ultimate/crushing", "mekanism:factory/ultimate/enriching", "mekanism:factory/ultimate/infusing", "mekanism:factory/ultimate/injecting", "mekanism:factory/ultimate/purifying", "mekanism:factory/ultimate/sawing", "mekanism:factory/ultimate/smelting", "mekanism:formulaic_assemblicator", "mekanism:induction/cell/basic", "mekanism:induction/provider/basic", "mekanism:isotopic_centrifuge", "mekanism:laser", "mekanism:laser_amplifier", "mekanism:laser_tractor_beam", "mekanism:logistical_sorter", "mekanism:modification_station", "mekanism:module_gravitational_modulating_unit", "mekanism:osmium_compressor", "mekanism:portable_teleporter", "mekanism:precision_sawmill", "mekanism:purification_chamber", "mekanism:qio_dashboard", "mekanism:qio_drive_array", "mekanism:qio_drive_base", "mekanism:qio_drive_hyper_dense", "mekanism:qio_drive_supermassive", "mekanism:qio_drive_time_dilating", "mekanism:qio_exporter", "mekanism:qio_importer", "mekanism:resistive_heater", "mekanism:rotary_condensentrator", "mekanism:solar_neutron_activator", "mekanism:sps_casing", "mekanism:sps_port", "mekanism:teleportation_core", "mekanism:teleporter", "mekanism:thermal_evaporation/block", "mekanism:thermal_evaporation/controller", "mekanism:tier_installer/advanced", "mekanism:tier_installer/basic", "mekanism:tier_installer/elite", "mekanism:tier_installer/ultimate", "mekanism:upgrade/energy", "mekanism:upgrade/speed", "mekanismgenerators:fission_reactor/control_rod_assembly", "mekanismgenerators:fission_reactor/fuel_assembly", "mekanismgenerators:fission_reactor/port", "mekanismgenerators:generator/advanced_solar", "mekanismgenerators:laser_focus_matrix", "mekanismgenerators:reactor/frame", "mekanismgenerators:reactor/port", "mekanismgenerators:rotational_complex", "mekanismgenerators:turbine/blade", "mekanismgenerators:turbine/casing", "mekanismgenerators:turbine/rotor", "mob_grinding_utils:recipe_absorption_hopper", "rftoolsbuilder:shape_card_quarry", "rftoolsbuilder:shape_card_quarry_clear", "rftoolsbuilder:shape_card_quarry_clear_fortune", "rftoolsbuilder:shape_card_quarry_clear_silk", "rftoolsbuilder:shape_card_quarry_dirt", "rftoolsbuilder:shape_card_quarry_fortune", "rftoolsbuilder:shape_card_quarry_fortune_dirt", "rftoolsbuilder:shape_card_quarry_silk", "rftoolsbuilder:shape_card_quarry_silk_dirt", "rftoolsbuilder:shield_block1", "rftoolsbuilder:shield_block2", "rftoolsbuilder:shield_block3", "rftoolsbuilder:shield_block4", "rftoolsdim:dimension_editor", "rftoolsdim:dimlet_workbench", "rftoolsdim:researcher", "rftoolspower:cell2", "rftoolspower:cell3", "rftoolspower:dimensionalcell", "rftoolspower:dimensionalcell_advanced", "rftoolspower:endergenic", "rftoolsstorage:modular_storage", "rftoolsstorage:storage_scanner", "rftoolsutility:advanced_charged_porter", "rftoolsutility:charged_porter", "rftoolsutility:crafter1", "rftoolsutility:crafter2", "rftoolsutility:crafter3", "rftoolsutility:environmental_controller", "rftoolsutility:screen_controller", "rftoolsutility:spawner", "sophisticatedbackpacks:advanced_compacting_upgrade", "sophisticatedbackpacks:advanced_deposit_upgrade", "sophisticatedbackpacks:advanced_filter_upgrade", "sophisticatedbackpacks:advanced_magnet_upgrade", "sophisticatedbackpacks:advanced_magnet_upgrade_from_basic", "sophisticatedbackpacks:advanced_mob_catcher_upgrade", "sophisticatedbackpacks:advanced_pickup_upgrade", "sophisticatedbackpacks:advanced_pump_upgrade", "sophisticatedbackpacks:advanced_refill_upgrade", "sophisticatedbackpacks:advanced_restock_upgrade", "sophisticatedbackpacks:advanced_void_upgrade", "sophisticatedbackpacks:auto_blasting_upgrade", "sophisticatedbackpacks:auto_smelting_upgrade", "sophisticatedbackpacks:battery_upgrade", "sophisticatedbackpacks:compacting_upgrade", "sophisticatedbackpacks:diamond_backpack", "sophisticatedbackpacks:everlasting_upgrade", "sophisticatedbackpacks:gold_backpack", "sophisticatedbackpacks:inception_upgrade", "sophisticatedbackpacks:mob_catcher_upgrade", "sophisticatedbackpacks:netherite_backpack", "sophisticatedbackpacks:pump_upgrade", "sophisticatedbackpacks:stack_upgrade_omega_tier", "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_1_conversion", "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_2_conversion", "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_3_conversion", "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_4_conversion", "sophisticatedbackpacks:stack_upgrade_tier_1", "sophisticatedbackpacks:stack_upgrade_tier_1_from_starter", "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_2_conversion", "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_3_conversion", "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_4_conversion", "sophisticatedbackpacks:stack_upgrade_tier_2", "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_3_conversion", "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_4_conversion", "sophisticatedbackpacks:stack_upgrade_tier_3", "sophisticatedbackpacks:stack_upgrade_tier_3_to_tier_4_conversion", "sophisticatedbackpacks:stack_upgrade_tier_4", "sophisticatedbackpacks:tank_upgrade", "sophisticatedbackpacks:xp_pump_upgrade", "sophisticatedstorage:advanced_compacting_upgrade", "sophisticatedstorage:advanced_filter_upgrade", "sophisticatedstorage:advanced_hopper_upgrade", "sophisticatedstorage:advanced_magnet_upgrade", "sophisticatedstorage:advanced_magnet_upgrade_from_basic", "sophisticatedstorage:advanced_pickup_upgrade", "sophisticatedstorage:advanced_void_upgrade", "sophisticatedstorage:auto_blasting_upgrade", "sophisticatedstorage:auto_smelting_upgrade", "sophisticatedstorage:basic_to_diamond_tier_upgrade", "sophisticatedstorage:basic_to_gold_tier_upgrade", "sophisticatedstorage:basic_to_netherite_tier_upgrade", "sophisticatedstorage:compacting_upgrade", "sophisticatedstorage:compression_upgrade", "sophisticatedstorage:controller", "sophisticatedstorage:copper_to_diamond_tier_upgrade", "sophisticatedstorage:copper_to_gold_tier_upgrade", "sophisticatedstorage:copper_to_netherite_tier_upgrade", "sophisticatedstorage:diamond_barrel", "sophisticatedstorage:diamond_chest", "sophisticatedstorage:diamond_shulker_box", "sophisticatedstorage:diamond_to_netherite_tier_upgrade", "sophisticatedstorage:double_diamond_chest", "sophisticatedstorage:double_gold_chest", "sophisticatedstorage:double_netherite_chest", "sophisticatedstorage:gold_barrel", "sophisticatedstorage:gold_chest", "sophisticatedstorage:gold_shulker_box", "sophisticatedstorage:gold_to_diamond_tier_upgrade", "sophisticatedstorage:gold_to_netherite_tier_upgrade", "sophisticatedstorage:hopper_upgrade", "sophisticatedstorage:iron_to_diamond_tier_upgrade", "sophisticatedstorage:iron_to_gold_tier_upgrade", "sophisticatedstorage:iron_to_netherite_tier_upgrade", "sophisticatedstorage:limited_diamond_barrel_1", "sophisticatedstorage:limited_diamond_barrel_2", "sophisticatedstorage:limited_diamond_barrel_3", "sophisticatedstorage:limited_diamond_barrel_4", "sophisticatedstorage:limited_gold_barrel_1", "sophisticatedstorage:limited_gold_barrel_2", "sophisticatedstorage:limited_gold_barrel_3", "sophisticatedstorage:limited_gold_barrel_4", "sophisticatedstorage:limited_netherite_barrel_1", "sophisticatedstorage:limited_netherite_barrel_2", "sophisticatedstorage:limited_netherite_barrel_3", "sophisticatedstorage:limited_netherite_barrel_4", "sophisticatedstorage:netherite_barrel", "sophisticatedstorage:netherite_chest", "sophisticatedstorage:netherite_shulker_box", "sophisticatedstorage:stack_upgrade_omega_tier", "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_2_conversion", "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_3_conversion", "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_4_conversion", "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_5_conversion", "sophisticatedstorage:stack_upgrade_tier_1_to_tier_2_conversion", "sophisticatedstorage:stack_upgrade_tier_1_to_tier_3_conversion", "sophisticatedstorage:stack_upgrade_tier_1_to_tier_4_conversion", "sophisticatedstorage:stack_upgrade_tier_1_to_tier_5_conversion", "sophisticatedstorage:stack_upgrade_tier_2", "sophisticatedstorage:stack_upgrade_tier_2_from_tier_1_plus", "sophisticatedstorage:stack_upgrade_tier_2_to_tier_3_conversion", "sophisticatedstorage:stack_upgrade_tier_2_to_tier_4_conversion", "sophisticatedstorage:stack_upgrade_tier_2_to_tier_5_conversion", "sophisticatedstorage:stack_upgrade_tier_3", "sophisticatedstorage:stack_upgrade_tier_3_to_tier_4_conversion", "sophisticatedstorage:stack_upgrade_tier_3_to_tier_5_conversion", "sophisticatedstorage:stack_upgrade_tier_4", "sophisticatedstorage:stack_upgrade_tier_4_to_tier_5_conversion", "sophisticatedstorage:stack_upgrade_tier_5", "sophisticatedstorage:storage_input", "sophisticatedstorage:storage_io", "sophisticatedstorage:storage_output", "storagedrawers:compacting_drawers_2", "storagedrawers:compacting_drawers_3", "storagedrawers:compacting_half_drawers_2", "storagedrawers:compacting_half_drawers_3", "storagedrawers:controller", "storagedrawers:controller_io", "storagedrawers:diamond_storage_upgrade", "storagedrawers:emerald_storage_upgrade", "storagedrawers:netherite_storage_upgrade", "storagedrawers:remote_group_upgrade", "storagedrawers:remote_group_upgrade_inc", "storagedrawers:remote_upgrade", "waystones:mossy_waystone", "waystones:mossy_waystone_from_moss_blocks", "waystones:mossy_waystone_from_vines", "waystones:sandy_waystone", "waystones:warp_stone", "waystones:waystone"];
  removeIds.forEach(function(id) { event.remove({id:id}); });
  var recipes = [
  {
    "id": "kubejs:expert_v042/ad_astra/cryo_freezer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:ostrum_plates"
        },
        "G": {
          "item": "ad_astra:large_gas_tank"
        },
        "I": {
          "item": "minecraft:blue_ice"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@#",
        "IGI",
        "###"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:cryo_freezer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/energizer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:ostrum_plates"
        },
        "B": {
          "tag": "ad_astra:ostrum_blocks"
        },
        "D": {
          "item": "minecraft:diamond_block"
        },
        "E": {
          "item": "mekanism:advanced_energy_cube"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:advanced_capacitor_bank"
        }
      },
      "pattern": [
        "!%@",
        "BDB",
        "#E#"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:energizer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/fuel_refinery",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:steel_plates"
        },
        "B": {
          "item": "minecraft:bucket"
        },
        "F": {
          "item": "minecraft:furnace"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@#",
        "BFB",
        "###"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:fuel_refinery"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/gravity_normalizer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:desh_plates"
        },
        "D": {
          "item": "minecraft:diamond_block"
        },
        "E": {
          "item": "ad_astra:etrionic_capacitor"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "mekanism:module_gravitational_modulating_unit"
        }
      },
      "pattern": [
        "%DE",
        "###",
        "!@ "
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:gravity_normalizer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/oxygen_distributor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:desh_plates"
        },
        "F": {
          "item": "ad_astra:fan"
        },
        "G": {
          "item": "ad_astra:large_gas_tank"
        },
        "L": {
          "item": "ad_astra:oxygen_loader"
        },
        "O": {
          "item": "ad_astra:oxygen_gear"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!G@",
        "FLF",
        "#O#"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:oxygen_distributor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/oxygen_loader",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:steel_plates"
        },
        "F": {
          "item": "ad_astra:fan"
        },
        "G": {
          "item": "ad_astra:gas_tank"
        },
        "R": {
          "item": "minecraft:redstone_block"
        },
        "|": {
          "item": "minecraft:lightning_rod"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!F@",
        "G|G",
        "#R#"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:oxygen_loader"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/solar_panel",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:desh_plates"
        },
        "P": {
          "item": "ad_astra:photovoltaic_etrium_cell"
        },
        "S": {
          "tag": "ad_astra:steel_plates"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@PP",
        "S!S",
        "###"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:solar_panel"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ad_astra/water_pump",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "ad_astra:desh_plates"
        },
        "G": {
          "item": "ad_astra:gas_tank"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "S": {
          "tag": "ad_astra:steel_plates"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@S",
        "SGS",
        "#H#"
      ],
      "result": {
        "count": 1,
        "id": "ad_astra:water_pump"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/interfaces_interface_part",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:interface"
        },
        {
          "item": "kubejs:control_assembly"
        },
        {
          "item": "kubejs:precision_mechanism"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:cable_interface"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/pattern_providers_interface_part",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:pattern_provider"
        },
        {
          "item": "kubejs:control_assembly"
        },
        {
          "item": "kubejs:precision_mechanism"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:cable_pattern_provider"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/cells/item_storage_components_cell_256k_part",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:dusts/sky_stone"
        },
        "b": {
          "item": "ae2:calculation_processor"
        },
        "c": {
          "item": "ae2:cell_component_64k"
        },
        "d": {
          "item": "ae2:quartz_glass"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!b@",
        "cdc",
        "aca"
      ],
      "result": {
        "count": 1,
        "id": "ae2:cell_component_256k"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/cells/item_storage_components_cell_64k_part",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:dusts/glowstone"
        },
        "b": {
          "item": "ae2:calculation_processor"
        },
        "c": {
          "item": "ae2:cell_component_16k"
        },
        "d": {
          "item": "ae2:quartz_glass"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!b@",
        "cdc",
        "aca"
      ],
      "result": {
        "count": 1,
        "id": "ae2:cell_component_64k"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/crafting/cpu_crafting_accelerator",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:crafting_unit"
        },
        {
          "item": "ae2:engineering_processor"
        },
        {
          "item": "kubejs:control_assembly"
        },
        {
          "item": "kubejs:precision_mechanism"
        },
        {
          "item": "rftoolsutility:crafter2"
        },
        {
          "item": "enderio:double_layer_capacitor"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:crafting_accelerator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/energy_dense_energy_cell",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "ae2:energy_cell"
        },
        "b": {
          "item": "ae2:calculation_processor"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@a",
        "aba",
        "aaa"
      ],
      "result": {
        "count": 1,
        "id": "ae2:dense_energy_cell"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/energy_energy_acceptor_alt",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:cable_energy_acceptor"
        },
        {
          "item": "kubejs:control_assembly"
        },
        {
          "item": "kubejs:precision_mechanism"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:energy_acceptor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/parts/export_bus",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "item": "ae2:formation_core"
        },
        "c": {
          "item": "minecraft:piston"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "aba",
        "!c@",
        "   "
      ],
      "result": {
        "count": 1,
        "id": "ae2:export_bus"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/parts/import_bus",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "ae2:annihilation_core"
        },
        "b": {
          "tag": "c:ingots/iron"
        },
        "c": {
          "item": "minecraft:piston"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!a@",
        "bcb",
        "   "
      ],
      "result": {
        "count": 1,
        "id": "ae2:import_bus"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/interfaces_interface",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "tag": "c:glass_blocks/cheap"
        },
        "c": {
          "item": "ae2:annihilation_core"
        },
        "d": {
          "item": "ae2:formation_core"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@ba",
        "c!d",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:interface"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/interfaces_interface_alt",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:cable_interface"
        },
        {
          "item": "kubejs:control_assembly"
        },
        {
          "item": "kubejs:precision_mechanism"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:interface"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/pattern_providers_interface",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:ingots/iron"
        },
        "b": {
          "item": "minecraft:crafting_table"
        },
        "c": {
          "item": "ae2:annihilation_core"
        },
        "d": {
          "item": "ae2:formation_core"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@ba",
        "c!d",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:pattern_provider"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/pattern_providers_interface_alt",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:cable_pattern_provider"
        },
        {
          "item": "kubejs:control_assembly"
        },
        {
          "item": "kubejs:precision_mechanism"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:pattern_provider"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/quantum_link",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "ae2:quartz_glass"
        },
        "b": {
          "item": "ae2:fluix_pearl"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "@ba",
        "b!b",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:quantum_link"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/cells/spatial_components_1",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:dusts/glowstone"
        },
        "b": {
          "item": "ae2:spatial_cell_component_16"
        },
        "c": {
          "item": "ae2:engineering_processor"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@a",
        "bcb",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:spatial_cell_component_128"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/cells/spatial_components_0",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:dusts/glowstone"
        },
        "b": {
          "item": "ae2:spatial_cell_component_2"
        },
        "c": {
          "item": "ae2:engineering_processor"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@a",
        "bcb",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:spatial_cell_component_16"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/blocks/spatial_io_pylon",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "ae2:quartz_glass"
        },
        "b": {
          "item": "ae2:fluix_glass_cable"
        },
        "c": {
          "tag": "c:dusts/fluix"
        },
        "d": {
          "tag": "ae2:all_fluix"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "mekanism:teleporter_frame"
        }
      },
      "pattern": [
        "!b@",
        "cdc",
        "%ba"
      ],
      "result": {
        "count": 1,
        "id": "ae2:spatial_pylon"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/upgrade_wireless_crafting_terminal",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "ae2:wireless_terminal"
        },
        {
          "item": "minecraft:crafting_table"
        },
        {
          "item": "ae2:calculation_processor"
        },
        {
          "item": "kubejs:quantum_processor"
        },
        {
          "item": "kubejs:dimensional_matrix"
        },
        {
          "item": "rftoolsutility:charged_porter"
        }
      ],
      "result": {
        "count": 1,
        "id": "ae2:wireless_crafting_terminal"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/wireless_crafting_terminal",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "item": "ae2:wireless_receiver"
        },
        "b": {
          "item": "ae2:crafting_terminal"
        },
        "c": {
          "item": "ae2:dense_energy_cell"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "rftoolsutility:charged_porter"
        }
      },
      "pattern": [
        "a!@",
        "b% ",
        "c  "
      ],
      "result": {
        "count": 1,
        "id": "ae2:wireless_crafting_terminal"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/kubejs/apotheosis/augmenting_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!N@",
        "GAG",
        "TER"
      ],
      "key": {
        "N": {
          "item": "minecraft:nether_star"
        },
        "G": {
          "item": "apotheosis:godforged_pearl"
        },
        "A": {
          "item": "minecraft:enchanting_table"
        },
        "T": {
          "item": "botania:terrasteel_ingot"
        },
        "E": {
          "item": "divinerpg:eden_chunk"
        },
        "R": {
          "item": "mekanism:ingot_refined_obsidian"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "apotheosis:augmenting_table",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/apotheosis/gem_cutting_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "apotheosis",
      "key": {
        "G": {
          "item": "apotheosis:gem_dust"
        },
        "H": {
          "item": "minecraft:shears"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "S": {
          "item": "minecraft:smooth_stone"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "SHS",
        "@GP",
        "P!P"
      ],
      "result": {
        "count": 1,
        "id": "apotheosis:gem_cutting_table"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/kubejs/apotheosis/reforging_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!I@",
        "ASA",
        "RNR"
      ],
      "key": {
        "I": {
          "tag": "c:ingots/netherite"
        },
        "A": {
          "item": "apotheosis:arcane_sands"
        },
        "S": {
          "item": "apotheosis:simple_reforging_table"
        },
        "R": {
          "item": "mekanism:ingot_refined_obsidian"
        },
        "N": {
          "item": "minecraft:nether_bricks"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "apotheosis:reforging_table",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/apotheosis/salvaging_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "apotheosis",
      "key": {
        "G": {
          "item": "apotheosis:gem_dust"
        },
        "I": {
          "tag": "c:ingots/copper"
        },
        "L": {
          "item": "minecraft:lava_bucket"
        },
        "O": {
          "item": "minecraft:iron_axe"
        },
        "R": {
          "item": "minecraft:iron_pickaxe"
        },
        "S": {
          "item": "minecraft:smithing_table"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@I",
        "RSO",
        "GLG"
      ],
      "result": {
        "count": 1,
        "id": "apotheosis:salvaging_table"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/apotheosis/sigil_of_socketing",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "apotheosis",
      "key": {
        "A": {
          "item": "minecraft:amethyst_shard"
        },
        "E": {
          "item": "apotheosis:gem_fused_slate"
        },
        "G": {
          "item": "apotheosis:gem_dust"
        },
        "U": {
          "item": "minecraft:gunpowder"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!U@",
        "EEE",
        "GAG"
      ],
      "result": {
        "count": 3,
        "id": "apotheosis:sigil_of_socketing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/apotheosis/simple_reforging_table",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "group": "apotheosis",
      "key": {
        "E": {
          "item": "minecraft:enchanting_table"
        },
        "G": {
          "item": "apotheosis:gem_dust"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "S": {
          "item": "minecraft:smooth_stone"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!I@",
        "GEG",
        "SSS"
      ],
      "result": {
        "count": 1,
        "id": "apotheosis:simple_reforging_table"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/apothic_enchanting/library",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "group": "lemons",
      "pattern": [
        "!@C",
        "STS",
        "CSC"
      ],
      "key": {
        "C": {
          "item": "minecraft:ender_chest"
        },
        "S": {
          "tag": "apothic_enchanting:infused_shelves"
        },
        "T": {
          "item": "minecraft:enchanting_table"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "apothic_enchanting:library",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/basic/casing",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "tag": "c:sands/colorless"
        },
        "G": {
          "tag": "c:ingots/graphite"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@I",
        "GCG",
        "IGI"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:basic_reactorcasing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/basic/fuelrod_ingots_uranium",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "G": {
          "tag": "c:ingots/graphite"
        },
        "L": {
          "tag": "c:glass_blocks"
        },
        "M": {
          "tag": "c:ingots/iron"
        },
        "Y": {
          "tag": "c:ingots/uranium"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!G@",
        "LYL",
        "MGM"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:basic_reactorfuelrod"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/basic/fuelrod_ingots_yellorium",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "G": {
          "tag": "c:ingots/graphite"
        },
        "L": {
          "tag": "c:glass_blocks"
        },
        "M": {
          "tag": "c:ingots/iron"
        },
        "Y": {
          "tag": "c:ingots/yellorium"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!G@",
        "LYL",
        "MGM"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:basic_reactorfuelrod"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/casing",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:not",
          "value": {
            "type": "neoforge:tag_empty",
            "tag": "c:ingots/steel"
          }
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "tag": "c:storage_blocks/iron"
        },
        "G": {
          "tag": "c:ingots/graphite"
        },
        "I": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@I",
        "GCG",
        "IGI"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorcasing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/casing_alt",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:tag_empty",
          "tag": "c:ingots/steel"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "tag": "c:storage_blocks/iron"
        },
        "G": {
          "tag": "c:ingots/graphite"
        },
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@I",
        "GCG",
        "IGI"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorcasing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/casing_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:not",
          "value": {
            "type": "neoforge:tag_empty",
            "tag": "c:ingots/steel"
          }
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "item": "bigreactors:basic_reactorcasing"
        },
        "G": {
          "tag": "c:ingots/graphite"
        },
        "I": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@I",
        "GCG",
        "IGI"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorcasing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/casing_upgrade_alt",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:tag_empty",
          "tag": "c:ingots/steel"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "C": {
          "item": "bigreactors:basic_reactorcasing"
        },
        "G": {
          "tag": "c:ingots/graphite"
        },
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@I",
        "GCG",
        "IGI"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorcasing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/fuelrod_alt_ingots_uranium",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:tag_empty",
          "tag": "c:ingots/steel"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "G": {
          "tag": "c:ingots/graphite"
        },
        "L": {
          "tag": "c:glass_blocks"
        },
        "M": {
          "tag": "c:storage_blocks/iron"
        },
        "Y": {
          "tag": "c:ingots/uranium"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!G@",
        "LYL",
        "MGM"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorfuelrod"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/fuelrod_alt_ingots_yellorium",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:tag_empty",
          "tag": "c:ingots/steel"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "G": {
          "tag": "c:ingots/graphite"
        },
        "L": {
          "tag": "c:glass_blocks"
        },
        "M": {
          "tag": "c:storage_blocks/iron"
        },
        "Y": {
          "tag": "c:ingots/yellorium"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!G@",
        "LYL",
        "MGM"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorfuelrod"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/fuelrod_ingots_uranium",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:not",
          "value": {
            "type": "neoforge:tag_empty",
            "tag": "c:ingots/steel"
          }
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "G": {
          "tag": "c:ingots/graphite"
        },
        "L": {
          "tag": "c:glass_blocks"
        },
        "M": {
          "tag": "c:ingots/steel"
        },
        "Y": {
          "tag": "c:ingots/uranium"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!G@",
        "LYL",
        "MGM"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorfuelrod"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/bigreactors/reactor/reinforced/fuelrod_ingots_yellorium",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:not",
          "value": {
            "type": "neoforge:tag_empty",
            "tag": "c:ingots/steel"
          }
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "building",
      "key": {
        "G": {
          "tag": "c:ingots/graphite"
        },
        "L": {
          "tag": "c:glass_blocks"
        },
        "M": {
          "tag": "c:ingots/steel"
        },
        "Y": {
          "tag": "c:ingots/yellorium"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!G@",
        "LYL",
        "MGM"
      ],
      "result": {
        "count": 1,
        "id": "bigreactors:reinforced_reactorfuelrod"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/alchemy_catalyst",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:brewing_stand"
        },
        "G": {
          "item": "minecraft:gold_ingot"
        },
        "P": {
          "item": "botania:mana_pearl"
        },
        "S": {
          "item": "botania:livingrock"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!G@",
        "BPB",
        "SGS"
      ],
      "result": {
        "count": 1,
        "id": "botania:alchemy_catalyst"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/aura_ring",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "I": {
          "tag": "botania:manasteel_ingots"
        },
        "R": {
          "item": "botania:rune_mana"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "RI!",
        "I@I",
        " I "
      ],
      "result": {
        "count": 1,
        "id": "botania:aura_ring"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/auto_crafting_halo",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "equipment",
      "ingredients": [
        {
          "item": "botania:crafting_halo"
        },
        {
          "tag": "botania:mana_diamond_gems"
        },
        {
          "item": "kubejs:quantum_processor"
        },
        {
          "item": "kubejs:dimensional_matrix"
        }
      ],
      "result": {
        "count": 1,
        "id": "botania:auto_crafting_halo"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/brewery",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "item": "botania:rune_mana"
        },
        "M": {
          "tag": "botania:manasteel_blocks"
        },
        "R": {
          "item": "botania:livingrock"
        },
        "S": {
          "item": "minecraft:brewing_stand"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!S@",
        "RAR",
        "RMR"
      ],
      "result": {
        "count": 1,
        "id": "botania:brewery"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/conjuration_catalyst",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "botania:pixie_dust"
        },
        "G": {
          "tag": "botania:elementium_ingots"
        },
        "P": {
          "item": "botania:alchemy_catalyst"
        },
        "S": {
          "item": "botania:livingrock"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!B@",
        "GPG",
        "SGS"
      ],
      "result": {
        "count": 1,
        "id": "botania:conjuration_catalyst"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/corporea_funnel",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "redstone",
      "ingredients": [
        {
          "item": "minecraft:dropper"
        },
        {
          "item": "botania:corporea_spark"
        },
        {
          "item": "kubejs:quantum_processor"
        },
        {
          "item": "kubejs:dimensional_matrix"
        }
      ],
      "result": {
        "count": 1,
        "id": "botania:corporea_funnel"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/corporea_index",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "redstone",
      "key": {
        "A": {
          "item": "botania:ender_air_bottle"
        },
        "D": {
          "tag": "botania:dragonstone_gems"
        },
        "O": {
          "item": "minecraft:obsidian"
        },
        "S": {
          "item": "botania:corporea_spark"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "A!A",
        "@SO",
        "DOD"
      ],
      "result": {
        "count": 1,
        "id": "botania:corporea_index"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/magnet_ring",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "L": {
          "item": "botania:lens_magnet"
        },
        "M": {
          "tag": "botania:manasteel_ingots"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "LM!",
        "M@M",
        " M "
      ],
      "result": {
        "count": 1,
        "id": "botania:magnet_ring"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/mana_mirror",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "I": {
          "tag": "botania:terrasteel_ingots"
        },
        "P": {
          "item": "botania:mana_pearl"
        },
        "R": {
          "item": "botania:livingrock"
        },
        "S": {
          "item": "botania:livingwood_twig"
        },
        "T": {
          "item": "botania:mana_tablet"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!PR",
        "@SI",
        "T  "
      ],
      "result": {
        "count": 1,
        "id": "botania:mana_mirror"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/mana_pylon",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "botania:mana_diamond_gems"
        },
        "G": {
          "item": "minecraft:gold_ingot"
        },
        "M": {
          "tag": "botania:manasteel_ingots"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!G@",
        "MDM",
        " G "
      ],
      "result": {
        "count": 1,
        "id": "botania:mana_pylon"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/spark",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:blaze_powder"
        },
        "N": {
          "item": "minecraft:gold_nugget"
        },
        "P": {
          "tag": "botania:petals"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!P@",
        "BNB",
        " P "
      ],
      "result": {
        "count": 1,
        "id": "botania:spark"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/terra_axe",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "G": {
          "item": "minecraft:glowstone"
        },
        "S": {
          "item": "botania:livingwood_twig"
        },
        "T": {
          "tag": "botania:terrasteel_ingots"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "TTG",
        "TST",
        "!S@"
      ],
      "result": {
        "count": 1,
        "id": "botania:terra_axe"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/terra_pick",
    "recipe": {
      "type": "botania:mana_upgrade",
      "category": "equipment",
      "key": {
        "I": {
          "tag": "botania:terrasteel_ingots"
        },
        "L": {
          "item": "botania:livingwood_twig"
        },
        "T": {
          "item": "botania:mana_tablet"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "ITI",
        "ILI",
        "!L@"
      ],
      "result": {
        "count": 1,
        "id": "botania:terra_pick"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/botania/terra_sword",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "equipment",
      "key": {
        "I": {
          "tag": "botania:terrasteel_ingots"
        },
        "S": {
          "item": "botania:livingwood_twig"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "I!@",
        "I  ",
        "S  "
      ],
      "result": {
        "count": 1,
        "id": "botania:terra_sword"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/buildcraftfactory/autowork_bench_1",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "key": {
        "c": {
          "item": "minecraft:crafting_table"
        },
        "s": {
          "item": "buildcraftcore:gears/gear_stone"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!s@",
        " c ",
        " s "
      ],
      "result": {
        "id": "buildcraftfactory:autoworkbench_item"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/buildcraftfactory/autowork_bench_2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "key": {
        "c": {
          "item": "minecraft:crafting_table"
        },
        "s": {
          "item": "buildcraftcore:gears/gear_stone"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@ ",
        "scs",
        "   "
      ],
      "result": {
        "id": "buildcraftfactory:autoworkbench_item"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/buildcraftfactory/distiller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "key": {
        "e": {
          "item": "buildcraftcore:gears/gear_diamond"
        },
        "r": {
          "item": "minecraft:redstone_torch"
        },
        "t": {
          "item": "buildcraftfactory:tank"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "rtr",
        "tet",
        "!@ "
      ],
      "result": {
        "id": "buildcraftfactory:distiller"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/buildcraftfactory/heat_exchange",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "key": {
        "e": {
          "item": "buildcraftcore:gears/gear_iron"
        },
        "g": {
          "item": "minecraft:glass"
        },
        "i": {
          "item": "minecraft:iron_ingot"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!e@",
        "ggg",
        "iei"
      ],
      "result": {
        "id": "buildcraftfactory:heat_exchange"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/buildcraftfactory/mining_well",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "key": {
        "a": {
          "item": "minecraft:iron_pickaxe"
        },
        "g": {
          "item": "buildcraftcore:gears/gear_iron"
        },
        "i": {
          "item": "minecraft:iron_ingot"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!r@",
        "igi",
        "iai"
      ],
      "result": {
        "id": "buildcraftfactory:mining_well"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/buildcraftsilicon/laser",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@O",
        "RDD",
        "RRO"
      ],
      "key": {
        "R": {
          "item": "minecraft:redstone"
        },
        "D": {
          "item": "minecraft:diamond"
        },
        "O": {
          "item": "minecraft:obsidian"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "buildcraftsilicon:laser"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/advanced_capacitor_bank",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "item": "enderio:conductive_alloy_block"
        },
        "C": {
          "item": "enderio:double_layer_capacitor"
        },
        "R": {
          "tag": "c:storage_blocks/redstone"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:basic_capacitor_bank"
        }
      },
      "pattern": [
        "!@%",
        "CRC",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "enderio:advanced_capacitor_bank"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/advanced_capacitor_bank_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "enderio:basic_capacitor_bank"
        },
        "C": {
          "item": "enderio:double_layer_capacitor"
        },
        "E": {
          "item": "enderio:energetic_alloy_block"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@E",
        "BCB",
        "EEE"
      ],
      "result": {
        "count": 1,
        "id": "enderio:advanced_capacitor_bank"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/basic_capacitor_bank",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "enderio:basic_capacitor"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "R": {
          "tag": "c:storage_blocks/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "mekanism:basic_energy_cube"
        }
      },
      "pattern": [
        "!@I",
        "%RC",
        "ICI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:basic_capacitor_bank"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/crafter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "enderio:void_chassis"
        },
        "G": {
          "tag": "c:gears/iron"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "S": {
          "tag": "c:silicon"
        },
        "T": {
          "item": "buildcraftfactory:autoworkbench_item"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@S",
        "ICI",
        "GTG"
      ],
      "result": {
        "count": 1,
        "id": "enderio:crafter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/double_layer_capacitor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "enderio:basic_capacitor"
        },
        "D": {
          "tag": "c:dusts/coal"
        },
        "I": {
          "item": "enderio:energetic_alloy_block"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "immersiveengineering:coil_lv"
        }
      },
      "pattern": [
        "!I@",
        "CDC",
        "%I "
      ],
      "result": {
        "count": 1,
        "id": "enderio:double_layer_capacitor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/enchanter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:book"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "I": {
          "tag": "c:ingots/dark_steel"
        },
        "L": {
          "item": "minecraft:lectern"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!B@",
        "DLD",
        "III"
      ],
      "result": {
        "count": 1,
        "id": "enderio:enchanter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/impulse_hopper",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "enderio:void_chassis"
        },
        "G": {
          "tag": "c:gears/iron"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "I": {
          "tag": "c:ingots/conductive_alloy"
        },
        "R": {
          "tag": "c:ingots/redstone_alloy"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!H@",
        "GCG",
        "IRI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:impulse_hopper"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/octadic_capacitor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "enderio:double_layer_capacitor"
        },
        "G": {
          "item": "minecraft:glowstone"
        },
        "I": {
          "item": "enderio:vibrant_alloy_block"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "immersiveengineering:coil_mv"
        }
      },
      "pattern": [
        "!I@",
        "CGC",
        "%I "
      ],
      "result": {
        "count": 1,
        "id": "enderio:octadic_capacitor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/painting_machine",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "c:ingots/redstone_alloy"
        },
        "B": {
          "tag": "c:dyes/blue"
        },
        "C": {
          "item": "enderio:void_chassis"
        },
        "G": {
          "tag": "c:dyes/green"
        },
        "I": {
          "tag": "c:ingots/conductive_alloy"
        },
        "M": {
          "tag": "c:gears/iron"
        },
        "R": {
          "tag": "c:dyes/red"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "RGB",
        "!CI",
        "@AM"
      ],
      "result": {
        "count": 1,
        "id": "enderio:painting_machine"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/powered_spawner",
    "recipe": {
      "type": "enderio:shaped_entity_storage",
      "category": "misc",
      "key": {
        "B": {
          "item": "enderio:broken_spawner"
        },
        "C": {
          "item": "enderio:ensouled_chassis"
        },
        "I": {
          "item": "enderio:soularium_block"
        },
        "V": {
          "tag": "c:gems/vibrant_crystal"
        },
        "Z": {
          "item": "enderio:z_logic_controller"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "%": {
          "item": "enderio:soul_binder"
        }
      },
      "pattern": [
        "!B@",
        "ICI",
        "%ZV"
      ],
      "result": {
        "count": 1,
        "id": "enderio:powered_spawner"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/slice_and_splice",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "immersiveengineering:buzzsaw"
        },
        "C": {
          "item": "enderio:ensouled_chassis"
        },
        "G": {
          "tag": "c:gears/energized"
        },
        "H": {
          "tag": "minecraft:skulls"
        },
        "I": {
          "tag": "c:ingots/soularium"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!H@",
        "ICI",
        "GBG"
      ],
      "result": {
        "count": 1,
        "id": "enderio:slice_and_splice"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/soul_binder",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "enderio:ensouled_chassis"
        },
        "G": {
          "tag": "c:gears/energized"
        },
        "I": {
          "item": "enderio:soularium_block"
        },
        "V": {
          "type": "enderio:empty_soul_storage",
          "item": "enderio:soul_vial"
        },
        "Z": {
          "item": "enderio:z_logic_controller"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        },
        "%": {
          "item": "enderio:enchanter"
        }
      },
      "pattern": [
        "!V@",
        "%CG",
        "IZI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:soul_binder"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/stirling_generator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "item": "minecraft:deepslate"
        },
        "F": {
          "item": "minecraft:furnace"
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
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!FG",
        "@VI",
        "ODO"
      ],
      "result": {
        "count": 1,
        "id": "enderio:stirling_generator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/travel_anchor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "enderio:conduit_binder"
        },
        "C": {
          "tag": "c:gems/pulsating_crystal"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@I",
        "BCB",
        "IBI"
      ],
      "result": {
        "count": 1,
        "id": "enderio:travel_anchor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderio/vat",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "c:ingots/conductive_alloy"
        },
        "B": {
          "item": "minecraft:barrel"
        },
        "C": {
          "item": "enderio:void_chassis"
        },
        "M": {
          "tag": "c:gears/iron"
        },
        "R": {
          "tag": "c:ingots/redstone_alloy"
        },
        "S": {
          "tag": "c:ingots/dark_steel"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!BS",
        "@CA",
        "MRM"
      ],
      "result": {
        "count": 1,
        "id": "enderio:vat"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderstorage/ender_chest",
    "recipe": {
      "type": "enderstorage:create_recipe",
      "key": {
        "B": {
          "item": "minecraft:blaze_rod"
        },
        "C": {
          "tag": "c:chests/wooden"
        },
        "O": {
          "tag": "c:obsidians"
        },
        "P": {
          "tag": "c:ender_pearls"
        },
        "W": {
          "tag": "c:wools"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!W@",
        "OCO",
        "BPB"
      ],
      "result": {
        "count": 1,
        "id": "enderstorage:ender_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderstorage/ender_pouch",
    "recipe": {
      "type": "enderstorage:create_recipe",
      "key": {
        "B": {
          "item": "minecraft:blaze_powder"
        },
        "L": {
          "tag": "c:leathers"
        },
        "P": {
          "tag": "c:ender_pearls"
        },
        "W": {
          "tag": "c:wools"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@B",
        "LPL",
        "BWB"
      ],
      "result": {
        "count": 1,
        "id": "enderstorage:ender_pouch"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/enderstorage/ender_tank",
    "recipe": {
      "type": "enderstorage:create_recipe",
      "key": {
        "B": {
          "item": "minecraft:blaze_rod"
        },
        "C": {
          "item": "minecraft:cauldron"
        },
        "O": {
          "tag": "c:obsidians"
        },
        "P": {
          "tag": "c:ender_pearls"
        },
        "W": {
          "tag": "c:wools"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!W@",
        "OCO",
        "BPB"
      ],
      "result": {
        "count": 1,
        "id": "enderstorage:ender_tank"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/blood_chest",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@P",
        "PIP",
        "PPP"
      ],
      "key": {
        "P": {
          "tag": "minecraft:planks"
        },
        "I": {
          "item": "evilcraft:blood_infusion_core"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "evilcraft:blood_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/colossal_blood_chest",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!P@",
        "PIP",
        " P "
      ],
      "key": {
        "P": {
          "item": "evilcraft:reinforced_undead_planks"
        },
        "I": {
          "item": "evilcraft:blood_infusion_core"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "evilcraft:colossal_blood_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/effortless_ring",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "SG!",
        "IEI",
        "@G "
      ],
      "key": {
        "S": {
          "item": "evilcraft:promise_speed_0"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "E": {
          "item": "evilcraft:promise_efficiency_0"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "evilcraft:effortless_ring"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/exalted_crafter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!C@",
        "DED",
        "GIG"
      ],
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "C": {
          "item": "minecraft:crafting_table"
        },
        "D": {
          "item": "evilcraft:dark_gem_crushed"
        },
        "E": {
          "tag": "c:chests/ender"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "evilcraft:exalted_crafter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/sanguinary_environmental_accumulator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!E@",
        "IGI",
        "CBC"
      ],
      "key": {
        "C": {
          "tag": "c:cobblestones"
        },
        "E": {
          "item": "evilcraft:environmental_accumulation_core"
        },
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "G": {
          "item": "evilcraft:garmonbozia"
        },
        "B": {
          "item": "evilcraft:blood_infusion_core"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "evilcraft:sanguinary_environmental_accumulator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/spirit_furnace",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!B@",
        "BIB",
        " B "
      ],
      "key": {
        "B": {
          "item": "evilcraft:dark_blood_brick"
        },
        "I": {
          "item": "evilcraft:blood_infusion_core"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "evilcraft:spirit_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/evilcraft/crafting/spirit_reanimator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!I@",
        "CBC",
        "CRC"
      ],
      "key": {
        "C": {
          "tag": "c:cobblestones"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "B": {
          "item": "evilcraft:blood_infusion_core"
        },
        "R": {
          "tag": "c:storage_blocks/iron"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "evilcraft:spirit_reanimator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/advanced_chicken_ring_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!@a",
        "%cb",
        "aba"
      ],
      "key": {
        "a": {
          "item": "minecraft:gold_block"
        },
        "b": {
          "item": "extrautilitiesreborn:chicken_wing_ring"
        },
        "c": {
          "item": "extrautilitiesreborn:evil_infused_iron_ingot"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "mekanism:jetpack_armored"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:advanced_chicken_wing_ring",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/electrical_furnace_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!@a",
        "aba",
        "aaa"
      ],
      "key": {
        "a": {
          "item": "minecraft:brick"
        },
        "b": {
          "item": "extrautilitiesreborn:machine_block"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:electrical_furnace",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/ender_quarry_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!ba",
        "@dc",
        "efe"
      ],
      "key": {
        "a": {
          "item": "extrautilitiesreborn:ender_infused_obsidian"
        },
        "b": {
          "tag": "minecraft:saplings"
        },
        "c": {
          "item": "extrautilitiesreborn:ender_core"
        },
        "d": {
          "item": "extrautilitiesreborn:diamond_edged_computational_matrix"
        },
        "e": {
          "item": "extrautilitiesreborn:ender_thermic_pump"
        },
        "f": {
          "item": "minecraft:netherite_pickaxe"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:ender_quarry",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/ender_thermic_pump_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!b@",
        "cde",
        "afa"
      ],
      "key": {
        "a": {
          "item": "extrautilitiesreborn:ender_infused_obsidian"
        },
        "b": {
          "item": "minecraft:diamond"
        },
        "c": {
          "item": "minecraft:lava_bucket"
        },
        "d": {
          "item": "minecraft:ender_eye"
        },
        "e": {
          "item": "minecraft:water_bucket"
        },
        "f": {
          "item": "minecraft:diamond_pickaxe"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:ender_thermic_pump",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/mechanical_miner_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "ab!",
        "c@ ",
        "   "
      ],
      "key": {
        "a": {
          "item": "minecraft:dropper"
        },
        "b": {
          "item": "extrautilitiesreborn:redstone_crystal"
        },
        "c": {
          "item": "minecraft:iron_pickaxe"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:mechanical_miner",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/mechnical_user_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "ab!",
        "c@ ",
        "   "
      ],
      "key": {
        "a": {
          "item": "minecraft:dropper"
        },
        "b": {
          "item": "extrautilitiesreborn:redstone_crystal"
        },
        "c": {
          "item": "minecraft:lever"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:mechanical_user",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/sorter_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!@a",
        "bcb",
        "aaa"
      ],
      "key": {
        "a": {
          "item": "minecraft:cobblestone"
        },
        "b": {
          "item": "minecraft:chest"
        },
        "c": {
          "item": "extrautilitiesreborn:redstone_crystal"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:sorting_machine",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/extrautilitiesreborn/wireless_fe_battery_rcp",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "!@a",
        "bcb",
        "aaa"
      ],
      "key": {
        "a": {
          "item": "extrautilitiesreborn:stoneburnt"
        },
        "b": {
          "item": "minecraft:redstone_block"
        },
        "c": {
          "item": "extrautilitiesreborn:redstone_crystal"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "extrautilitiesreborn:wireless_fe_battery",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/blastfurnace_preheater",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "h": {
          "item": "immersiveengineering:furnace_heater"
        },
        "p": {
          "item": "immersiveengineering:fluid_pipe"
        },
        "s": {
          "tag": "c:sheetmetals/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "ss!",
        "ss@",
        "ph "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:blastfurnace_preheater"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/buzzsaw",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "g": {
          "item": "immersiveengineering:wooden_grip"
        },
        "h": {
          "item": "immersiveengineering:heavy_engineering"
        },
        "r": {
          "tag": "c:rods/steel"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@g",
        "rhg",
        "r  "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:buzzsaw"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/charging_station",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "g": {
          "tag": "c:glass_blocks"
        },
        "i": {
          "tag": "c:plates/iron"
        },
        "l": {
          "item": "immersiveengineering:coil_lv"
        },
        "t": {
          "item": "immersiveengineering:electron_tube"
        },
        "w": {
          "tag": "immersiveengineering:treated_wood"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@ig",
        "!tt",
        "wlw"
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:charging_station"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/drill",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "item": "immersiveengineering:component_iron"
        },
        "g": {
          "item": "immersiveengineering:wooden_grip"
        },
        "h": {
          "item": "immersiveengineering:heavy_engineering"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@g",
        " hg",
        "c  "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:drill"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/electromagnet",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "item": "immersiveengineering:component_electronic"
        },
        "i": {
          "tag": "c:ingots/iron"
        },
        "p": {
          "tag": "c:rods/steel"
        },
        "w": {
          "item": "immersiveengineering:wirecoil_copper"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!c@",
        "wiw",
        "pwp"
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:electromagnet"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/fluid_pump",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "item": "immersiveengineering:component_iron"
        },
        "i": {
          "tag": "c:plates/iron"
        },
        "p": {
          "item": "immersiveengineering:fluid_pipe"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!i@",
        "ici",
        "ppp"
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:fluid_pump"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/heavy_engineering",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "e": {
          "tag": "c:ingots/electrum"
        },
        "g": {
          "item": "immersiveengineering:component_steel"
        },
        "i": {
          "tag": "c:sheetmetals/steel"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@i",
        "geg",
        "igi"
      ],
      "result": {
        "count": 4,
        "id": "immersiveengineering:heavy_engineering"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/item_batcher",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "item": "immersiveengineering:component_iron"
        },
        "e": {
          "item": "immersiveengineering:component_electronic"
        },
        "w": {
          "item": "immersiveengineering:basic_engineering"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "c!@",
        "w  ",
        "e  "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:item_batcher"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/light_engineering",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "tag": "c:ingots/copper"
        },
        "g": {
          "item": "immersiveengineering:component_iron"
        },
        "i": {
          "tag": "c:sheetmetals/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@i",
        "gcg",
        "igi"
      ],
      "result": {
        "count": 4,
        "id": "immersiveengineering:light_engineering"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/railgun",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "item": "immersiveengineering:coil_mv"
        },
        "g": {
          "item": "immersiveengineering:wooden_grip"
        },
        "i": {
          "tag": "c:ingots/steel"
        },
        "p": {
          "item": "immersiveengineering:component_electronic_adv"
        },
        "v": {
          "item": "immersiveengineering:capacitor_hv"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!vg",
        "icp",
        "ci@"
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:railgun"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/sample_drill",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "e": {
          "item": "immersiveengineering:light_engineering"
        },
        "f": {
          "item": "immersiveengineering:steel_fence"
        },
        "s": {
          "item": "immersiveengineering:steel_scaffolding_standard"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@s",
        "sfs",
        "efe"
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:sample_drill"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/sorter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "b": {
          "item": "immersiveengineering:conveyor_basic"
        },
        "c": {
          "item": "immersiveengineering:component_iron"
        },
        "w": {
          "item": "immersiveengineering:basic_engineering"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "c!@",
        "w  ",
        "b  "
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:sorter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/immersiveengineering/crafting/tesla_coil",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "c": {
          "item": "immersiveengineering:component_iron"
        },
        "e": {
          "item": "immersiveengineering:component_electronic_adv"
        },
        "h": {
          "item": "immersiveengineering:capacitor_hv"
        },
        "i": {
          "tag": "c:plates/aluminum"
        },
        "l": {
          "item": "immersiveengineering:coil_mv"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "iii",
        "!l@",
        "ehc"
      ],
      "result": {
        "count": 1,
        "id": "immersiveengineering:tesla_coil"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/block_breaker",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:block_breaker"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "item": "minecraft:iron_shovel"
        },
        "G": {
          "tag": "c:gears/gold"
        },
        "I": {
          "item": "minecraft:iron_pickaxe"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "S": {
          "tag": "c:gears/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!GP",
        "IMD",
        "@RS"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:block_breaker"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/block_placer",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:block_placer"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "item": "minecraft:dropper"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@P",
        "DMD",
        "PRP"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:block_placer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/dissolution_chamber",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:dissolution_chamber"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:bucket"
        },
        "C": {
          "tag": "c:chests/wooden"
        },
        "D": {
          "tag": "c:gears/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!CP",
        "@MB",
        "GDG"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:dissolution_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/enchantment_applicator",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:enchantment_applicator"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:anvil"
        },
        "G": {
          "tag": "c:gears/gold"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "P": {
          "tag": "c:plastics"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!PP",
        "@MB",
        "GBG"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:enchantment_applicator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/enchantment_extractor",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:enchantment_extractor"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:book"
        },
        "D": {
          "item": "minecraft:diamond"
        },
        "G": {
          "tag": "c:gears/gold"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "P": {
          "tag": "c:plastics"
        },
        "S": {
          "item": "minecraft:nether_bricks"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!SP",
        "@MB",
        "DGD"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:enchantment_extractor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/fluid_collector",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:fluid_collector"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:bucket"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "S": {
          "tag": "c:gears/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "@!P",
        "BMB",
        "SRS"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:fluid_collector"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/fluid_laser_base",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:fluid_laser_base"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "b": {
          "item": "buildcraftfactory:pump"
        },
        "f": {
          "item": "minecraft:diamond_pickaxe"
        },
        "g": {
          "tag": "c:gears/diamond"
        },
        "m": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "p": {
          "tag": "c:plastics"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!fp",
        "@mb",
        "grg"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:fluid_laser_base"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/fluid_placer",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:fluid_placer"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:water_bucket"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "S": {
          "tag": "c:gears/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "@!P",
        "BMB",
        "SRS"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:fluid_placer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/fluid_sieving_machine",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:fluid_sieving_machine"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:gears/gold"
        },
        "b": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "c": {
          "item": "industrialforegoing:pink_slime"
        },
        "g": {
          "item": "minecraft:iron_bars"
        },
        "p": {
          "tag": "c:plastics"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@cp",
        "!gg",
        "aba"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:fluid_sieving_machine"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/laser_drill",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:laser_drill"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "b": {
          "item": "minecraft:piston"
        },
        "f": {
          "tag": "c:gears/diamond"
        },
        "g": {
          "tag": "c:gears/gold"
        },
        "m": {
          "tag": "industrialforegoing:machine_frame/simple"
        },
        "p": {
          "tag": "c:plastics"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "buildcraftsilicon:laser"
        }
      },
      "pattern": [
        "!fp",
        "@mb",
        "%rg"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:laser_drill"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/latex_processing_unit",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:latex_processing_unit"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:bucket"
        },
        "F": {
          "item": "minecraft:furnace"
        },
        "G": {
          "tag": "c:storage_blocks/redstone"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!G@",
        "BMB",
        "IFI"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:latex_processing_unit"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/material_stonework_factory",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:material_stonework_factory"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:gears/gold"
        },
        "b": {
          "item": "industrialforegoing:pink_slime"
        },
        "c": {
          "item": "minecraft:crafting_table"
        },
        "f": {
          "item": "industrialforegoing:resourceful_furnace"
        },
        "g": {
          "item": "industrialforegoing:block_breaker"
        },
        "m": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "p": {
          "tag": "c:plastics"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!cp",
        "gmf",
        "@ba"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:material_stonework_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/mob_crusher",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:mob_crusher"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "mob_grinding_utils:absorption_hopper"
        },
        "G": {
          "tag": "c:gears/gold"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "S": {
          "item": "mob_grinding_utils:saw"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!SP",
        "@MB",
        "GRG"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:mob_crusher"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/mob_duplicator",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:mob_duplicator"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "minecraft:magma_cream"
        },
        "E": {
          "item": "minecraft:emerald_block"
        },
        "M": {
          "item": "enderio:powered_spawner"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "W": {
          "item": "minecraft:nether_wart"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!WP",
        "@MC",
        "ERE"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:mob_duplicator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/mob_slaughter_factory",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:mob_slaughter_factory"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "item": "minecraft:iron_axe"
        },
        "D": {
          "tag": "c:gears/gold"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "item": "minecraft:redstone"
        },
        "S": {
          "item": "minecraft:iron_sword"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "immersiveengineering:buzzsaw"
        }
      },
      "pattern": [
        "!DP",
        "@MS",
        "%RA"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:mob_slaughter_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/ore_laser_base",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:ore_laser_base"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "b": {
          "tag": "c:ores/iron"
        },
        "f": {
          "item": "buildcraftfactory:mining_well"
        },
        "g": {
          "tag": "c:gears/diamond"
        },
        "m": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "p": {
          "tag": "c:plastics"
        },
        "r": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!fp",
        "@mb",
        "grg"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:ore_laser_base"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/resourceful_furnace",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:resourceful_furnace"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "minecraft:bucket"
        },
        "L": {
          "item": "minecraft:furnace"
        },
        "M": {
          "tag": "industrialforegoing:machine_frame/pity"
        },
        "P": {
          "tag": "c:plastics"
        },
        "R": {
          "tag": "c:gears/gold"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!B@",
        "LML",
        "PRP"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:resourceful_furnace"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/stasis_chamber",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:stasis_chamber"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "g": {
          "item": "minecraft:ghast_tear"
        },
        "i": {
          "tag": "c:gears/gold"
        },
        "m": {
          "tag": "industrialforegoing:machine_frame/advanced"
        },
        "p": {
          "item": "mekanism:dimensional_stabilizer"
        },
        "s": {
          "item": "minecraft:soul_sand"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@s",
        "gmg",
        "ipi"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:stasis_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/industrialforegoing/wither_builder",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "neoforge:item_exists",
          "item": "industrialforegoing:wither_builder"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "industrialforegoing:machine_frame/supreme"
        },
        "N": {
          "item": "minecraft:nether_star"
        },
        "P": {
          "tag": "c:plastics"
        },
        "S": {
          "item": "industrialforegoing:block_placer"
        },
        "W": {
          "item": "minecraft:wither_skeleton_skull"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "@NP",
        "WCW",
        "!SS"
      ],
      "result": {
        "count": 1,
        "id": "industrialforegoing:wither_builder"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ironfurnaces/augments/augment_fuel",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!R@",
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
          "item": "minecraft:coal"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:augment_fuel"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ironfurnaces/augments/augment_speed",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!R@",
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
          "item": "minecraft:sugar"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "ironfurnaces:augment_speed"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/combining",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_combining_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_combining_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/compressing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_compressing_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_compressing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/crushing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_crushing_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_crushing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/energy_cube/advanced",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_energy_cube"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_energy_cube"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/enriching",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_enriching_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_enriching_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/infusing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_infusing_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_infusing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/injecting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_injecting_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_injecting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/purifying",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_purifying_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_purifying_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/sawing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_sawing_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_sawing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/advanced/smelting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "item": "mekanism:basic_smelting_factory"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_smelting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/tier_installer/advanced",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "I": {
          "item": "mekanism:block_osmium"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "enderio:double_layer_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:advanced_tier_installer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/antiprotonic_nucleosynthesizer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:alloy_atomic"
        },
        "A": {
          "tag": "c:pellets/antimatter"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!C@",
        "AXA",
        "#C#"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:antiprotonic_nucleosynthesizer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/combining",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:combiner"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_combining_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/compressing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:osmium_compressor"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_compressing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/crushing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:crusher"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_crushing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/energy_cube/basic",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_energy_cube"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/enriching",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:enrichment_chamber"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_enriching_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/induction/cell/basic",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:basic_energy_cube"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "L": {
          "tag": "c:dusts/lithium"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@L",
        "E#E",
        "LEL"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_induction_cell"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/induction/provider/basic",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:basic_energy_cube"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "L": {
          "tag": "c:dusts/lithium"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@L",
        "C#C",
        "LCL"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_induction_provider"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/infusing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:metallurgic_infuser"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_infusing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/injecting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:chemical_injection_chamber"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_injecting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/purifying",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:purification_chamber"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_purifying_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/sawing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:precision_sawmill"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_sawing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/basic/smelting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "item": "mekanism:energized_smelter"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_smelting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/tier_installer/basic",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        },
        "%": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:basic_tier_installer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/chargepad",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:polished_blackstone_pressure_plate"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "###",
        "SES",
        "!@ "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chargepad"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/chemical_infuser",
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
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!C@",
        "TXT",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chemical_infuser"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/chemical_injection_chamber",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:purification_chamber"
        },
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "tag": "c:ingots/gold"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!C@",
        "I#I",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chemical_injection_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/chemical_oxidizer",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:dynamic_tank"
        },
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "P": {
          "tag": "mekanism:personal_storage"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!C@",
        "P#T",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chemical_oxidizer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/chemical_washer",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "B": {
          "item": "mekanism:basic_fluid_tank"
        },
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
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!B@",
        "CXC",
        "ITI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:chemical_washer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/combiner",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "_": {
          "tag": "mekanism:stone_crafting_materials"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!C@",
        "_X_",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:combiner"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/crusher",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:buckets/lava"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!C@",
        "BXB",
        "RCR"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:crusher"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/dimensional_stabilizer",
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
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "I": {
          "tag": "c:ingots/refined_obsidian"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!C@",
        "ADA",
        "ICI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:dimensional_stabilizer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/electric_pump",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "B": {
          "item": "minecraft:bucket"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!B@",
        "AXA",
        "OOO"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:electric_pump"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/electrolytic_separator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:electrolytic_core"
        },
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!R@",
        "A#A",
        "IRI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:electrolytic_separator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/combining",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_combining_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_combining_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/compressing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_compressing_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_compressing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/crushing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_crushing_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_crushing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/energy_cube/elite",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_energy_cube"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_energy_cube"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/enriching",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_enriching_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_enriching_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/infusing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_infusing_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_infusing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/injecting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_injecting_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_injecting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/purifying",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_purifying_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_purifying_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/sawing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_sawing_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_sawing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/elite/smelting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "item": "mekanism:advanced_smelting_factory"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_smelting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/tier_installer/elite",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "item": "minecraft:gold_block"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:elite_tier_installer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/energized_smelter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "G": {
          "tag": "c:glass_blocks/cheap"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!C@",
        "GXG",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:energized_smelter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/enrichment_chamber",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/basic"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!C@",
        "IXI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:enrichment_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/formulaic_assemblicator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:chests/wooden"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "P": {
          "item": "minecraft:crafter"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!P@",
        "CXC",
        "S#S"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:formulaic_assemblicator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/isotopic_centrifuge",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "tag": "c:ingots/lead"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@I",
        "CTC",
        "III"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:isotopic_centrifuge"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/laser",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:gems/diamond"
        },
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "AE!",
        "AX#",
        "AE@"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:laser"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/laser_amplifier",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:gems/diamond"
        },
        "E": {
          "item": "mekanism:basic_energy_cube"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@S",
        "SE#",
        "SSS"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:laser_amplifier"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/laser_tractor_beam",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:laser_amplifier"
        },
        "P": {
          "tag": "mekanism:personal_storage"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "P!@",
        "#  ",
        "   "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:laser_tractor_beam"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/logistical_sorter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:piston"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!#@",
        "ICI",
        "III"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:logistical_sorter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/modification_station",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "c:pellets/polonium"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "W": {
          "tag": "c:chests/wooden"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!W@",
        "CXC",
        "PAP"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:modification_station"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/module_gravitational_modulating_unit",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:nether_stars"
        },
        "A": {
          "tag": "c:alloys/ultimate"
        },
        "E": {
          "item": "mekanism:ultimate_induction_provider"
        },
        "H": {
          "tag": "c:pellets/antimatter"
        },
        "P": {
          "item": "mekanism:module_base"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "@#A",
        "EPE",
        "!HH"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:module_gravitational_modulating_unit"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/osmium_compressor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "B": {
          "item": "minecraft:bucket"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!C@",
        "BXB",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:osmium_compressor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/portable_teleporter",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/basic"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        },
        "%": {
          "item": "rftoolsutility:charged_porter"
        }
      },
      "pattern": [
        "!E@",
        "CTC",
        "%E "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:portable_teleporter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/precision_sawmill",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!C@",
        "AXA",
        "ICI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:precision_sawmill"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/purification_chamber",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "O": {
          "tag": "c:ingots/osmium"
        },
        "P": {
          "item": "mekanism:enrichment_chamber"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!C@",
        "OPO",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:purification_chamber"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_dashboard",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "c:ender_pearls"
        },
        "G": {
          "tag": "c:glass_panes"
        },
        "I": {
          "tag": "c:ingots/lead"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@I",
        "AGA",
        "ITI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_dashboard"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_drive_array",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "tag": "mekanism:personal_storage"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "G": {
          "tag": "c:glass_panes"
        },
        "I": {
          "tag": "c:ender_pearls"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!G@",
        "C#C",
        "TIT"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_drive_array"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_drive_base",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:ender_pearls"
        },
        "C": {
          "item": "mekanism:ultimate_control_circuit"
        },
        "I": {
          "tag": "c:ingots/lead"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@I",
        "C#C",
        "ICI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_drive_base"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_drive_hyper_dense",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:teleportation_core"
        },
        "I": {
          "tag": "c:pellets/plutonium"
        },
        "P": {
          "item": "mekanism:qio_drive_base"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@I",
        "P#P",
        "IPI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_drive_hyper_dense"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_drive_supermassive",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:pellets/antimatter"
        },
        "I": {
          "tag": "c:pellets/polonium"
        },
        "P": {
          "item": "mekanism:qio_drive_time_dilating"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@I",
        "P#P",
        "IPI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_drive_supermassive"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_drive_time_dilating",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:pellets/polonium"
        },
        "I": {
          "tag": "c:pellets/plutonium"
        },
        "P": {
          "item": "mekanism:qio_drive_hyper_dense"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@I",
        "P#P",
        "IPI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_drive_time_dilating"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_exporter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:piston"
        },
        "A": {
          "tag": "c:ender_pearls"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "tag": "c:ingots/lead"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "ITI",
        "ACA",
        "!#@"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_exporter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/qio_importer",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "minecraft:sticky_piston"
        },
        "A": {
          "tag": "c:ender_pearls"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "tag": "c:ingots/lead"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "ITI",
        "ACA",
        "!#@"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:qio_importer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/resistive_heater",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "tag": "c:ingots/tin"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@I",
        "RXR",
        "IEI"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:resistive_heater"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/rotary_condensentrator",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:basic_fluid_tank"
        },
        "C": {
          "tag": "c:circuits/basic"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "G": {
          "tag": "c:glass_blocks/cheap"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!C@",
        "TE#",
        "GCG"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:rotary_condensentrator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/solar_neutron_activator",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:hdpe_sheet"
        },
        "A": {
          "tag": "mekanism:alloys/reinforced"
        },
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "tag": "c:ingots/bronze"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "@#A",
        "CXC",
        "!II"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:solar_neutron_activator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/sps_casing",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:pellets/plutonium"
        },
        "A": {
          "tag": "c:pellets/polonium"
        },
        "P": {
          "item": "mekanism:hdpe_sheet"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@P",
        "A#A",
        "PAP"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:sps_casing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/sps_port",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:sps_casing"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!#@",
        "#C#",
        " # "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:sps_port"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/teleportation_core",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:ender_pearls"
        },
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "I": {
          "tag": "c:ingots/gold"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!A@",
        "IDI",
        "#A#"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:teleportation_core"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/teleporter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/basic"
        },
        "T": {
          "item": "mekanism:teleportation_core"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@C",
        "XTX",
        "CXC"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:teleporter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/thermal_evaporation/block",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:ingots/copper"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!S@",
        "SIS",
        " S "
      ],
      "result": {
        "count": 4,
        "id": "mekanism:thermal_evaporation_block"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/thermal_evaporation/controller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "item": "mekanism:thermal_evaporation_block"
        },
        "B": {
          "item": "minecraft:bucket"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "G": {
          "tag": "c:glass_panes"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "CGC",
        "!B@",
        "###"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:thermal_evaporation_controller"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/combining",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_combining_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_combining_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/compressing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_compressing_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_compressing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/crushing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_crushing_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_crushing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/energy_cube/ultimate",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "E": {
          "item": "mekanism:energy_tablet"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_energy_cube"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "AEA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_energy_cube"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/enriching",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_enriching_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_enriching_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/infusing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_infusing_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_infusing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/injecting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_injecting_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_injecting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/purifying",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_purifying_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_purifying_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/sawing",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_sawing_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_sawing_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/factory/ultimate/smelting",
    "recipe": {
      "type": "mekanism:mek_data",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/atomic"
        },
        "C": {
          "tag": "c:circuits/ultimate"
        },
        "I": {
          "item": "minecraft:diamond_block"
        },
        "P": {
          "item": "mekanism:elite_smelting_factory"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_smelting_factory"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/tier_installer/ultimate",
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
          "item": "minecraft:diamond_block"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        },
        "%": {
          "item": "enderio:octadic_capacitor"
        }
      },
      "pattern": [
        "!%@",
        "IPI",
        "ACA"
      ],
      "result": {
        "count": 1,
        "id": "mekanism:ultimate_tier_installer"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/upgrade/energy",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:dusts/gold"
        },
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "G": {
          "tag": "c:glass_blocks/cheap"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!G@",
        "A#A",
        " G "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:upgrade_energy"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanism/upgrade/speed",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:dusts/osmium"
        },
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "G": {
          "tag": "c:glass_blocks/cheap"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!G@",
        "A#A",
        " G "
      ],
      "result": {
        "count": 1,
        "id": "mekanism:upgrade_speed"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/generator/advanced_solar",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "P": {
          "item": "mekanismgenerators:solar_generator"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!A@",
        "PAP",
        "III"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:advanced_solar_generator"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/fission_reactor/control_rod_assembly",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/elite"
        },
        "I": {
          "tag": "c:ingots/lead"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!CI",
        "@IS",
        "SIS"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:control_rod_assembly"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/fission_reactor/fuel_assembly",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:ingots/lead"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "T": {
          "item": "mekanism:basic_chemical_tank"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!S@",
        "ITI",
        "ISI"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:fission_fuel_assembly"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/fission_reactor/port",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:circuits/elite"
        },
        "F": {
          "item": "mekanismgenerators:fission_reactor_casing"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!F@",
        "FCF",
        " F "
      ],
      "result": {
        "count": 2,
        "id": "mekanismgenerators:fission_reactor_port"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/reactor/frame",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "#": {
          "tag": "c:pellets/polonium"
        },
        "A": {
          "tag": "c:alloys/ultimate"
        },
        "X": {
          "item": "mekanism:steel_casing"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!@A",
        "#X#",
        "A#A"
      ],
      "result": {
        "count": 4,
        "id": "mekanismgenerators:fusion_reactor_frame"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/reactor/port",
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
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!F@",
        "FCF",
        " F "
      ],
      "result": {
        "count": 2,
        "id": "mekanismgenerators:fusion_reactor_port"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/laser_focus_matrix",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "item": "mekanismgenerators:reactor_glass"
        },
        "R": {
          "tag": "c:storage_blocks/redstone"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!G@",
        "GRG",
        " G "
      ],
      "result": {
        "count": 2,
        "id": "mekanismgenerators:laser_focus_matrix"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/rotational_complex",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "C": {
          "tag": "c:circuits/advanced"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@S",
        "CAC",
        "SAS"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:rotational_complex"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/turbine/blade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!S@",
        "SAS",
        " S "
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:turbine_blade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/turbine/casing",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "O": {
          "tag": "c:ingots/osmium"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!S@",
        "SOS",
        " S "
      ],
      "result": {
        "count": 4,
        "id": "mekanismgenerators:turbine_casing"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mekanismgenerators/turbine/rotor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "A": {
          "tag": "mekanism:alloys/infused"
        },
        "S": {
          "tag": "c:ingots/steel"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!A@",
        "SAS",
        "SAS"
      ],
      "result": {
        "count": 1,
        "id": "mekanismgenerators:turbine_rotor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/mob_grinding_utils/recipe_absorption_hopper",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "E": {
          "item": "minecraft:ender_eye"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "O": {
          "tag": "c:obsidians"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!E@",
        " O ",
        "OHO"
      ],
      "result": {
        "count": 1,
        "id": "mob_grinding_utils:absorption_hopper"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsbuilder:shape_card_def"
          },
          "P": {
            "item": "minecraft:diamond_pickaxe"
          },
          "S": {
            "item": "minecraft:diamond_shovel"
          },
          "i": {
            "tag": "c:ingots/iron"
          },
          "r": {
            "item": "minecraft:redstone"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!P@",
          "iMi",
          "rSr"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_dirt",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "D": {
            "item": "minecraft:dirt"
          },
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry_clear"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@D",
          "DMD",
          "DDD"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_clear",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "G": {
            "tag": "c:glass_blocks"
          },
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@G",
          "GMG",
          "GGG"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_clear"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_clear_fortune",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "G": {
            "tag": "c:glass_blocks"
          },
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry_fortune"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@G",
          "GMG",
          "GGG"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_clear_fortune"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_clear_silk",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "G": {
            "tag": "c:glass_blocks"
          },
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry_silk"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@G",
          "GMG",
          "GGG"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_clear_silk"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_fortune",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry"
          },
          "d": {
            "item": "minecraft:diamond"
          },
          "e": {
            "item": "minecraft:emerald"
          },
          "n": {
            "item": "minecraft:ghast_tear"
          },
          "r": {
            "item": "minecraft:redstone"
          },
          "s": {
            "item": "rftoolsbase:dimensionalshard"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!n@",
          "eMd",
          "srs"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_fortune"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_fortune_dirt",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "D": {
            "item": "minecraft:dirt"
          },
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry_clear_fortune"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@D",
          "DMD",
          "DDD"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_fortune"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_silk",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry"
          },
          "d": {
            "item": "minecraft:diamond"
          },
          "n": {
            "item": "minecraft:nether_star"
          },
          "s": {
            "item": "rftoolsbase:dimensionalshard"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!n@",
          "dMd",
          "sds"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_silk"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shape_card_quarry_silk_dirt",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "D": {
            "item": "minecraft:dirt"
          },
          "M": {
            "item": "rftoolsbuilder:shape_card_quarry_clear_silk"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@D",
          "DMD",
          "DDD"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shape_card_quarry_silk"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shield_block1",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "O": {
          "item": "minecraft:obsidian"
        },
        "T": {
          "item": "minecraft:redstone_torch"
        },
        "g": {
          "tag": "c:ingots/gold"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "@Tg",
        "rFr",
        "!OO"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsbuilder:shield_block1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shield_block2",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsbuilder:shield_block1"
          },
          "O": {
            "item": "minecraft:obsidian"
          },
          "R": {
            "item": "minecraft:redstone_block"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@R",
          "OMO",
          "ROR"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shield_block2"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shield_block3",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsbuilder:shield_block2"
          },
          "O": {
            "item": "minecraft:obsidian"
          },
          "s": {
            "item": "rftoolsbase:dimensionalshard"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "!@s",
          "OMO",
          "sOs"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shield_block3"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsbuilder/shield_block4",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsbuilder:shield_block3"
          },
          "O": {
            "item": "minecraft:obsidian"
          },
          "n": {
            "item": "minecraft:nether_star"
          },
          "s": {
            "item": "rftoolsbase:dimensionalshard"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          }
        },
        "pattern": [
          "n!s",
          "@MO",
          "sOn"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsbuilder:shield_block4"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsdim/dimension_editor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "g": {
          "item": "minecraft:gold_ingot"
        },
        "i": {
          "tag": "c:ingots/iron"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "o!o",
        "iFi",
        "@gg"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsdim:dimension_editor"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsdim/dimlet_workbench",
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
        "r": {
          "item": "minecraft:redstone"
        },
        "u": {
          "item": "rftoolsdim:empty_dimlet"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!u@",
        "CFC",
        "rur"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsdim:dimlet_workbench"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsdim/researcher",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "minecraft:enchanting_table"
        },
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "X": {
          "item": "minecraft:comparator"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "u": {
          "item": "rftoolsdim:empty_dimlet"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!u@",
        "XFC",
        "rur"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsdim:researcher"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolspower/cell2",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "K": {
            "item": "rftoolspower:power_core2"
          },
          "P": {
            "item": "rftoolspower:cell1"
          },
          "r": {
            "item": "minecraft:redstone"
          },
          "!": {
            "item": "kubejs:control_assembly"
          },
          "@": {
            "item": "kubejs:precision_mechanism"
          }
        },
        "pattern": [
          "!@r",
          "KPK",
          "rKr"
        ],
        "result": {
          "count": 1,
          "id": "rftoolspower:cell2"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolspower/cell3",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "K": {
            "item": "rftoolspower:power_core3"
          },
          "P": {
            "item": "rftoolspower:cell2"
          },
          "r": {
            "item": "minecraft:redstone"
          },
          "!": {
            "item": "kubejs:control_assembly"
          },
          "@": {
            "item": "kubejs:precision_mechanism"
          }
        },
        "pattern": [
          "!@r",
          "KPK",
          "rKr"
        ],
        "result": {
          "count": 1,
          "id": "rftoolspower:cell3"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolspower/dimensionalcell",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "P": {
          "item": "minecraft:prismarine_shard"
        },
        "R": {
          "item": "minecraft:redstone_block"
        },
        "d": {
          "item": "minecraft:diamond"
        },
        "e": {
          "item": "minecraft:emerald"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!d@",
        "PFP",
        "ReR"
      ],
      "result": {
        "count": 1,
        "id": "rftoolspower:dimensionalcell"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolspower/dimensionalcell_advanced",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "*": {
          "item": "rftoolsbase:infused_diamond"
        },
        "K": {
          "item": "rftoolspower:dimensionalcell"
        },
        "R": {
          "item": "minecraft:redstone_block"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@R",
        "*K*",
        "R*R"
      ],
      "result": {
        "count": 1,
        "id": "rftoolspower:dimensionalcell_advanced"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolspower/endergenic",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "Z": {
          "item": "rftoolsbase:infused_enderpearl"
        },
        "d": {
          "item": "minecraft:diamond"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!o@",
        "ZFZ",
        "dod"
      ],
      "result": {
        "count": 1,
        "id": "rftoolspower:endergenic"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsstorage/modular_storage",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "tag": "c:chests"
        },
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "q": {
          "item": "minecraft:quartz"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!C@",
        "qFq",
        "rqr"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsstorage:modular_storage"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsstorage/storage_scanner",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "T": {
          "item": "minecraft:redstone_torch"
        },
        "g": {
          "item": "minecraft:gold_ingot"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!o@",
        "gFg",
        "ToT"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsstorage:storage_scanner"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/advanced_charged_porter",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "M": {
            "item": "rftoolsutility:charged_porter"
          },
          "R": {
            "item": "minecraft:redstone_block"
          },
          "d": {
            "item": "minecraft:diamond_block"
          },
          "!": {
            "item": "kubejs:quantum_processor"
          },
          "@": {
            "item": "kubejs:dimensional_matrix"
          },
          "%": {
            "item": "mekanism:teleporter"
          }
        },
        "pattern": [
          "!@R",
          "%Md",
          "RdR"
        ],
        "result": {
          "count": 1,
          "id": "rftoolsutility:advanced_charged_porter"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/charged_porter",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "R": {
          "item": "minecraft:redstone_block"
        },
        "i": {
          "tag": "c:ingots/iron"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!o@",
        "oRo",
        "ioi"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:charged_porter"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/crafter1",
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
        "T": {
          "item": "minecraft:redstone_torch"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        },
        "%": {
          "item": "buildcraftfactory:autoworkbench_item"
        },
        "&": {
          "item": "enderio:basic_capacitor"
        }
      },
      "pattern": [
        "!T@",
        "%FC",
        "&T "
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:crafter1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/crafter2",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "C": {
            "item": "enderio:double_layer_capacitor"
          },
          "M": {
            "item": "rftoolsutility:crafter1"
          },
          "T": {
            "item": "minecraft:redstone_torch"
          },
          "!": {
            "item": "kubejs:control_assembly"
          },
          "@": {
            "item": "kubejs:precision_mechanism"
          },
          "%": {
            "item": "enderio:crafter"
          }
        },
        "pattern": [
          "!T@",
          "%MC",
          " T "
        ],
        "result": {
          "count": 1,
          "id": "rftoolsutility:crafter2"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/crafter3",
    "recipe": {
      "type": "mcjtylib:copy_components",
      "recipe": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "key": {
          "C": {
            "item": "enderio:octadic_capacitor"
          },
          "M": {
            "item": "rftoolsutility:crafter2"
          },
          "T": {
            "item": "minecraft:redstone_torch"
          },
          "!": {
            "item": "kubejs:control_assembly"
          },
          "@": {
            "item": "kubejs:precision_mechanism"
          },
          "%": {
            "item": "ae2:molecular_assembler"
          }
        },
        "pattern": [
          "!T@",
          "%MC",
          " T "
        ],
        "result": {
          "count": 1,
          "id": "rftoolsutility:crafter3"
        }
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/environmental_controller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "E": {
          "item": "minecraft:emerald_block"
        },
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "I": {
          "item": "minecraft:iron_block"
        },
        "X": {
          "item": "minecraft:diamond_block"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "z": {
          "item": "minecraft:gold_block"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!X@",
        "zFI",
        "oEo"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:environmental_controller"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/screen_controller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "rftoolsbase:machine_frame"
        },
        "G": {
          "tag": "c:glass_blocks"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!o@",
        "GFG",
        "rGr"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:screen_controller"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/rftoolsutility/spawner",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "F": {
          "item": "enderio:powered_spawner"
        },
        "P": {
          "tag": "c:bones"
        },
        "X": {
          "tag": "c:rods/blaze"
        },
        "o": {
          "tag": "c:ender_pearls"
        },
        "r": {
          "item": "minecraft:redstone"
        },
        "z": {
          "item": "minecraft:rotten_flesh"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!z@",
        "oFX",
        "rPr"
      ],
      "result": {
        "count": 1,
        "id": "rftoolsutility:spawner"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_compacting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_compacting_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "C": {
          "item": "sophisticatedbackpacks:compacting_upgrade"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GCG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_compacting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_deposit_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_deposit_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "V": {
          "item": "sophisticatedbackpacks:deposit_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GVG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_deposit_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_filter_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_filter_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "item": "sophisticatedbackpacks:filter_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "GPG",
        "RRR",
        "!@ "
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_filter_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_magnet_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_magnet_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "E": {
          "tag": "c:ender_pearls"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "L": {
          "tag": "c:gems/lapis"
        },
        "P": {
          "item": "sophisticatedbackpacks:advanced_pickup_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "E@E",
        "IPI",
        "R!L"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_magnet_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_magnet_upgrade_from_basic",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_magnet_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "M": {
          "item": "sophisticatedbackpacks:magnet_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GMG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_magnet_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_mob_catcher_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_mob_catcher_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:mob_catcher_upgrade"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "E": {
          "item": "minecraft:ender_eye"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "H": {
          "item": "minecraft:wither_skeleton_skull"
        },
        "S": {
          "item": "minecraft:soul_sand"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "HDE",
        "@BG",
        "!SS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_mob_catcher_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_pickup_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_pickup_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "item": "sophisticatedbackpacks:pickup_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GPG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_pickup_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_pump_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_pump_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "I": {
          "item": "minecraft:dispenser"
        },
        "P": {
          "item": "sophisticatedbackpacks:pump_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "@ID",
        "GPG",
        "!RR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_pump_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_refill_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_refill_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "F": {
          "item": "sophisticatedbackpacks:refill_upgrade"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GFG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_refill_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_restock_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_restock_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "V": {
          "item": "sophisticatedbackpacks:restock_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GVG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_restock_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/advanced_void_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:advanced_void_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "V": {
          "item": "sophisticatedbackpacks:void_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GVG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:advanced_void_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/auto_blasting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:auto_blasting_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "S": {
          "item": "sophisticatedbackpacks:blasting_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@!D",
        "RSH",
        "GHG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:auto_blasting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/auto_smelting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:auto_smelting_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "S": {
          "item": "sophisticatedbackpacks:smelting_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@!D",
        "RSH",
        "GHG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:auto_smelting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/battery_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:battery_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:storage_blocks/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "RBR",
        "GRG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:battery_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/compacting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:compacting_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "I!I",
        "@BP",
        "RPR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:compacting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/diamond_backpack",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:diamond_backpack"
        }
      ],
      "type": "sophisticatedbackpacks:backpack_upgrade",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:gold_backpack"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DBD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:diamond_backpack"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/everlasting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:everlasting_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "C": {
          "item": "minecraft:end_crystal"
        },
        "S": {
          "tag": "c:nether_stars"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@C",
        "SBS",
        "CSC"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:everlasting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/gold_backpack",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:gold_backpack"
        }
      ],
      "type": "sophisticatedbackpacks:backpack_upgrade",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:iron_backpack"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GBG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:gold_backpack"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/inception_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:inception_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "E": {
          "item": "minecraft:ender_eye"
        },
        "S": {
          "tag": "c:nether_stars"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!S@",
        "DBD",
        "EDE"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:inception_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/mob_catcher_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:mob_catcher_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "E": {
          "tag": "c:ender_pearls"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "L": {
          "item": "minecraft:lead"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!E@",
        "IBI",
        "L L"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:mob_catcher_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/netherite_backpack",
    "recipe": {
      "type": "sophisticatedbackpacks:smithing_backpack_upgrade",
      "addition": {
        "item": "kubejs:reinforced_netherite_plate"
      },
      "base": {
        "item": "sophisticatedbackpacks:diamond_backpack"
      },
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:netherite_backpack"
      },
      "template": {
        "item": "minecraft:netherite_upgrade_smithing_template"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/pump_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:pump_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "G": {
          "tag": "c:glass_blocks"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "S": {
          "item": "minecraft:sticky_piston"
        },
        "U": {
          "item": "minecraft:bucket"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!U@",
        "PBS",
        "GUG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:pump_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_omega_tier",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_omega_tier"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_4"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!@S",
        "SSS",
        "SSS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_omega_tier"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_starter_tier_to_tier_1_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_1_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "L": {
          "item": "minecraft:leather"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!I@",
        "ILI",
        " I "
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_1_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_starter_tier_to_tier_2_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_2_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:storage_blocks/gold"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_1_conversion"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_2_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_starter_tier_to_tier_3_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_3_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_2_conversion"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_3_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_starter_tier_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "N": {
          "tag": "c:storage_blocks/netherite"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_3_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "NNN"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_starter_tier_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_1",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_1"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@I",
        "IBI",
        "III"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_1_from_starter",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_1"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "I": {
          "tag": "c:storage_blocks/iron"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_starter_tier"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!I@",
        "ISI",
        " I "
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_1_to_tier_2_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_2_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:storage_blocks/gold"
        },
        "L": {
          "item": "minecraft:leather"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GLG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_2_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_1_to_tier_3_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_3_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_2_conversion"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_3_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_1_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "N": {
          "tag": "c:storage_blocks/netherite"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_3_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "NNN"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_1_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_2",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_2"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:storage_blocks/gold"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_1"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_2_to_tier_3_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_3_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "L": {
          "item": "minecraft:leather"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DLD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_3_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_2_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "N": {
          "tag": "c:storage_blocks/netherite"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_3_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "NNN"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_2_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_3",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_3"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:storage_blocks/diamond"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_2"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_3_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_3_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "L": {
          "item": "minecraft:leather"
        },
        "N": {
          "tag": "c:storage_blocks/netherite"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NLN",
        "NNN"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_3_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/stack_upgrade_tier_4",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:stack_upgrade_tier_4"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "N": {
          "tag": "c:storage_blocks/netherite"
        },
        "S": {
          "item": "sophisticatedbackpacks:stack_upgrade_tier_3"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "NNN"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:stack_upgrade_tier_4"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/tank_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:tank_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedbackpacks:upgrade_base"
        },
        "G": {
          "tag": "c:glass_blocks"
        },
        "!": {
          "item": "kubejs:sealed_process_chamber"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@G",
        "GBG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:tank_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedbackpacks/xp_pump_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedbackpacks:xp_pump_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "C": {
          "item": "minecraft:experience_bottle"
        },
        "E": {
          "item": "minecraft:ender_eye"
        },
        "P": {
          "item": "sophisticatedbackpacks:advanced_pump_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!E@",
        "CPC",
        "RER"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedbackpacks:xp_pump_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_compacting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_compacting_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "C": {
          "item": "sophisticatedstorage:compacting_upgrade"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GCG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_compacting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_filter_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_filter_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "item": "sophisticatedstorage:filter_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "GPG",
        "RRR",
        "!@ "
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_filter_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_hopper_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_hopper_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "H": {
          "item": "sophisticatedstorage:hopper_upgrade"
        },
        "O": {
          "item": "minecraft:dropper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GHG",
        "ROR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_hopper_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_magnet_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_magnet_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "E": {
          "tag": "c:ender_pearls"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "L": {
          "tag": "c:gems/lapis"
        },
        "P": {
          "item": "sophisticatedstorage:advanced_pickup_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "E@E",
        "IPI",
        "R!L"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_magnet_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_magnet_upgrade_from_basic",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_magnet_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "M": {
          "item": "sophisticatedstorage:magnet_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GMG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_magnet_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_pickup_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_pickup_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "item": "sophisticatedstorage:pickup_upgrade"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GPG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_pickup_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/advanced_void_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:advanced_void_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "V": {
          "item": "sophisticatedstorage:void_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!D@",
        "GVG",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:advanced_void_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/auto_blasting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:auto_blasting_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "S": {
          "item": "sophisticatedstorage:blasting_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@!D",
        "RSH",
        "GHG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:auto_blasting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/auto_smelting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:auto_smelting_upgrade"
        }
      ],
      "type": "sophisticatedcore:upgrade_next_tier",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "S": {
          "item": "sophisticatedstorage:smelting_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "@!D",
        "RSH",
        "GHG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:auto_smelting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/basic_to_diamond_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:basic_to_diamond_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "T": {
          "item": "sophisticatedstorage:basic_to_gold_tier_upgrade"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DTD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:basic_to_diamond_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/basic_to_gold_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:basic_to_gold_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "T": {
          "item": "sophisticatedstorage:basic_to_iron_tier_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GTG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:basic_to_gold_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/basic_to_netherite_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:basic_to_netherite_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:basic_to_diamond_tier_upgrade"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:basic_to_netherite_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/compacting_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:compacting_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedstorage:upgrade_base"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "I!I",
        "@BP",
        "RPR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:compacting_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/compression_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:compression_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedstorage:upgrade_base"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "P": {
          "item": "minecraft:piston"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!I@",
        "PBP",
        "RIR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:compression_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/controller",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:controller"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "type": "sophisticatedstorage:base_tier_wooden_storage"
        },
        "C": {
          "item": "minecraft:comparator"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "S": {
          "tag": "c:stones"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!C@",
        "PBP",
        "SCS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:controller"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/copper_to_diamond_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:copper_to_diamond_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "T": {
          "item": "sophisticatedstorage:copper_to_gold_tier_upgrade"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DTD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:copper_to_diamond_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/copper_to_gold_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:copper_to_gold_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "T": {
          "item": "sophisticatedstorage:copper_to_iron_tier_upgrade"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GTG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:copper_to_gold_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/copper_to_netherite_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:copper_to_netherite_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:copper_to_diamond_tier_upgrade"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:copper_to_netherite_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/diamond_barrel",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:diamond_barrel"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:gold_barrel"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:diamond_barrel"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/diamond_chest",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:diamond_chest"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:gold_chest"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:diamond_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/double_diamond_chest",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:diamond_chest"
        }
      ],
      "type": "sophisticatedstorage:double_chest_tier_upgrade",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/diamond"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:gold_chest"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DBD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:diamond_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/diamond_shulker_box",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:diamond_shulker_box"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:gold_shulker_box"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:diamond_shulker_box"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/diamond_to_netherite_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:diamond_to_netherite_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "minecraft:lever"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:diamond_to_netherite_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/gold_barrel",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:gold_barrel"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:iron_barrel"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:gold_barrel"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/double_gold_chest",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:gold_chest"
        }
      ],
      "type": "sophisticatedstorage:double_chest_tier_upgrade",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/gold"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:iron_chest"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GBG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:gold_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/gold_chest",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:gold_chest"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:iron_chest"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:gold_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/gold_shulker_box",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:gold_shulker_box"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:iron_shulker_box"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:gold_shulker_box"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/gold_to_diamond_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:gold_to_diamond_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "L": {
          "item": "minecraft:lever"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DLD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:gold_to_diamond_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/gold_to_netherite_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:gold_to_netherite_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:gold_to_diamond_tier_upgrade"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:gold_to_netherite_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/hopper_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:hopper_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "item": "sophisticatedstorage:upgrade_base"
        },
        "H": {
          "item": "minecraft:hopper"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!H@",
        "IBI",
        "RRR"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:hopper_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/iron_to_diamond_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:iron_to_diamond_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "T": {
          "item": "sophisticatedstorage:iron_to_gold_tier_upgrade"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DTD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:iron_to_diamond_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/iron_to_gold_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:iron_to_gold_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "L": {
          "item": "minecraft:lever"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GLG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:iron_to_gold_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/iron_to_netherite_tier_upgrade",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:iron_to_netherite_tier_upgrade"
        }
      ],
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:iron_to_diamond_tier_upgrade"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:iron_to_netherite_tier_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_diamond_barrel_1",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_diamond_barrel_1"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:limited_gold_barrel_1"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_diamond_barrel_1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_diamond_barrel_2",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_diamond_barrel_2"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:limited_gold_barrel_2"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_diamond_barrel_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_diamond_barrel_3",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_diamond_barrel_3"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:limited_gold_barrel_3"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_diamond_barrel_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_diamond_barrel_4",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_diamond_barrel_4"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:limited_gold_barrel_4"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "DDD"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_diamond_barrel_4"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_gold_barrel_1",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_gold_barrel_1"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:limited_iron_barrel_1"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_gold_barrel_1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_gold_barrel_2",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_gold_barrel_2"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:limited_iron_barrel_2"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_gold_barrel_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_gold_barrel_3",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_gold_barrel_3"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:limited_iron_barrel_3"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_gold_barrel_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_gold_barrel_4",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_gold_barrel_4"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade",
      "category": "misc",
      "key": {
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:limited_iron_barrel_4"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "GGG"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_gold_barrel_4"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_netherite_barrel_1",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_netherite_barrel_1"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:limited_diamond_barrel_1"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_netherite_barrel_1"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_netherite_barrel_2",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_netherite_barrel_2"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:limited_diamond_barrel_2"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_netherite_barrel_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_netherite_barrel_3",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_netherite_barrel_3"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:limited_diamond_barrel_3"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_netherite_barrel_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/limited_netherite_barrel_4",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:limited_netherite_barrel_4"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:limited_diamond_barrel_4"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:limited_netherite_barrel_4"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/netherite_barrel",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:netherite_barrel"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:diamond_barrel"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:netherite_barrel"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/double_netherite_chest",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:netherite_chest"
        }
      ],
      "type": "sophisticatedstorage:double_chest_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:diamond_chest"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:netherite_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/netherite_chest",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:netherite_chest"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:diamond_chest"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:netherite_chest"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/netherite_shulker_box",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:netherite_shulker_box"
        }
      ],
      "type": "sophisticatedstorage:storage_tier_upgrade_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "sophisticatedstorage:diamond_shulker_box"
        },
        {
          "tag": "c:ingots/netherite"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:netherite_shulker_box"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_omega_tier",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_omega_tier"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_5"
        },
        "!": {
          "item": "kubejs:mythic_circuit"
        },
        "@": {
          "item": "kubejs:radiation_shield"
        }
      },
      "pattern": [
        "!@S",
        "SSS",
        "SSS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_omega_tier"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_plus_to_tier_2_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_2_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/iron"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!I@",
        "IPI",
        " B "
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_2_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_plus_to_tier_3_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_3_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/gold"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_2_conversion"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "BGB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_3_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_plus_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/diamond"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_3_conversion"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "BDB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_plus_to_tier_5_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_5_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/netherite"
        },
        "N": {
          "tag": "c:ingots/netherite"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_4_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "BNB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_plus_to_tier_5_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_to_tier_2_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_2_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/iron"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_1_plus_conversion"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@I",
        "ISI",
        "BIB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_2_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_to_tier_3_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_3_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/gold"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_2_conversion"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "BGB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_3_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/diamond"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_3_conversion"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "BDB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_1_to_tier_5_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_5_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/netherite"
        },
        "N": {
          "tag": "c:ingots/netherite"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_4_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "BNB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_1_to_tier_5_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_2",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_2"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/iron"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!@I",
        "ISI",
        "BIB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_2_from_tier_1_plus",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_2"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/iron"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_1_plus"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "pattern": [
        "!I@",
        "ISI",
        " B "
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_2_to_tier_3_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_3_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/gold"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GPG",
        "BGB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_3_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_2_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/diamond"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_3_conversion"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "BDB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_2_to_tier_5_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_5_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/netherite"
        },
        "N": {
          "tag": "c:ingots/netherite"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_4_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "BNB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_2_to_tier_5_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_3",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_3"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/gold"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_2"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!@G",
        "GSG",
        "BGB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_3_to_tier_4_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_3_to_tier_4_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/diamond"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DPD",
        "BDB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_3_to_tier_4_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_3_to_tier_5_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_3_to_tier_5_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/netherite"
        },
        "N": {
          "tag": "c:ingots/netherite"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_3_to_tier_4_conversion"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "BNB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_3_to_tier_5_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_4",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_4"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/diamond"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_3"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!@D",
        "DSD",
        "BDB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_4"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_4_to_tier_5_conversion",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_4_to_tier_5_conversion"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/netherite"
        },
        "N": {
          "tag": "c:ingots/netherite"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NPN",
        "BNB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_4_to_tier_5_conversion"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/stack_upgrade_tier_5",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:stack_upgrade_tier_5"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "tag": "c:storage_blocks/netherite"
        },
        "N": {
          "tag": "c:ingots/netherite"
        },
        "S": {
          "item": "sophisticatedstorage:stack_upgrade_tier_4"
        },
        "!": {
          "item": "kubejs:radiation_shield"
        },
        "@": {
          "item": "kubejs:quantum_processor"
        }
      },
      "pattern": [
        "!@N",
        "NSN",
        "BNB"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:stack_upgrade_tier_5"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/storage_input",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:storage_input"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "type": "sophisticatedstorage:base_tier_wooden_storage"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "R": {
          "item": "minecraft:repeater"
        },
        "S": {
          "tag": "c:stones"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!R@",
        "PBP",
        "SGS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:storage_input"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/storage_io",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:storage_io"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "type": "sophisticatedstorage:base_tier_wooden_storage"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "R": {
          "item": "minecraft:repeater"
        },
        "S": {
          "tag": "c:stones"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!P@",
        "RBG",
        "SPS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:storage_io"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/sophisticatedstorage/storage_output",
    "recipe": {
      "neoforge:conditions": [
        {
          "type": "sophisticatedcore:item_enabled",
          "itemRegistryName": "sophisticatedstorage:storage_output"
        }
      ],
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "B": {
          "type": "sophisticatedstorage:base_tier_wooden_storage"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "P": {
          "tag": "minecraft:planks"
        },
        "R": {
          "item": "minecraft:repeater"
        },
        "S": {
          "tag": "c:stones"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "pattern": [
        "!G@",
        "PBP",
        "SRS"
      ],
      "result": {
        "count": 1,
        "id": "sophisticatedstorage:storage_output"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/compacting_drawers_2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!T@",
        "#X#",
        "#I#"
      ],
      "key": {
        "#": {
          "item": "minecraft:stone"
        },
        "T": {
          "item": "minecraft:piston"
        },
        "X": {
          "tag": "storagedrawers:full_drawers"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "result": {
        "id": "storagedrawers:compacting_drawers_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/compacting_drawers_3",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@#",
        "TXT",
        "#I#"
      ],
      "key": {
        "#": {
          "item": "minecraft:stone"
        },
        "T": {
          "item": "minecraft:piston"
        },
        "X": {
          "tag": "storagedrawers:full_drawers"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "result": {
        "id": "storagedrawers:compacting_drawers_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/compacting_half_drawers_2",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!T@",
        "#X#",
        "#I#"
      ],
      "key": {
        "#": {
          "item": "minecraft:stone"
        },
        "T": {
          "item": "minecraft:piston"
        },
        "X": {
          "tag": "storagedrawers:half_drawers"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "result": {
        "id": "storagedrawers:compacting_half_drawers_2"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/compacting_half_drawers_3",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@#",
        "TXT",
        "#I#"
      ],
      "key": {
        "#": {
          "item": "minecraft:stone"
        },
        "T": {
          "item": "minecraft:piston"
        },
        "X": {
          "tag": "storagedrawers:half_drawers"
        },
        "I": {
          "tag": "c:ingots/iron"
        },
        "!": {
          "item": "kubejs:electromechanical_drive"
        },
        "@": {
          "item": "kubejs:industrial_chassis"
        }
      },
      "result": {
        "id": "storagedrawers:compacting_half_drawers_3"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/controller",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@#",
        "CXC",
        "#D#"
      ],
      "key": {
        "#": {
          "item": "minecraft:stone"
        },
        "C": {
          "item": "minecraft:comparator"
        },
        "X": {
          "tag": "storagedrawers:drawers"
        },
        "D": {
          "tag": "c:gems/diamond"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "storagedrawers:controller"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/controller_io",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@#",
        "CXC",
        "#G#"
      ],
      "key": {
        "#": {
          "item": "minecraft:stone"
        },
        "C": {
          "item": "minecraft:comparator"
        },
        "X": {
          "tag": "storagedrawers:drawers"
        },
        "G": {
          "tag": "c:ingots/gold"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "storagedrawers:controller_io"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/diamond_storage_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@/",
        "#X#",
        "///"
      ],
      "key": {
        "/": {
          "tag": "c:rods/wooden"
        },
        "#": {
          "tag": "c:gems/diamond"
        },
        "X": {
          "item": "storagedrawers:upgrade_template"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "storagedrawers:diamond_storage_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/emerald_storage_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@/",
        "#X#",
        "///"
      ],
      "key": {
        "/": {
          "tag": "c:rods/wooden"
        },
        "#": {
          "tag": "c:gems/emerald"
        },
        "X": {
          "item": "storagedrawers:upgrade_template"
        },
        "!": {
          "item": "kubejs:control_assembly"
        },
        "@": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "storagedrawers:emerald_storage_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/netherite_storage_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!@/",
        "#X#",
        "///"
      ],
      "key": {
        "/": {
          "tag": "c:rods/wooden"
        },
        "#": {
          "tag": "c:ingots/netherite"
        },
        "X": {
          "item": "storagedrawers:upgrade_template"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "storagedrawers:netherite_storage_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/remote_group_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!EE",
        "RXR",
        "@//"
      ],
      "key": {
        "/": {
          "tag": "c:rods/wooden"
        },
        "E": {
          "item": "minecraft:ender_pearl"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "X": {
          "item": "storagedrawers:upgrade_template"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "storagedrawers:remote_group_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/remote_group_upgrade_inc",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "EXE",
        "!@ ",
        "   "
      ],
      "key": {
        "E": {
          "item": "minecraft:ender_pearl"
        },
        "X": {
          "item": "storagedrawers:remote_upgrade"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "storagedrawers:remote_group_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/storagedrawers/remote_upgrade",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "pattern": [
        "!E@",
        "RXR",
        "///"
      ],
      "key": {
        "/": {
          "tag": "c:rods/wooden"
        },
        "E": {
          "item": "minecraft:ender_pearl"
        },
        "R": {
          "tag": "c:dusts/redstone"
        },
        "X": {
          "item": "storagedrawers:upgrade_template"
        },
        "!": {
          "item": "kubejs:quantum_processor"
        },
        "@": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "storagedrawers:remote_upgrade"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/waystones/mossy_waystone",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "S": {
          "item": "minecraft:mossy_stone_bricks"
        },
        "W": {
          "item": "waystones:warp_stone"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!S@",
        "SWS",
        "OOO"
      ],
      "result": {
        "count": 1,
        "id": "waystones:mossy_waystone"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/waystones/mossy_waystone_from_moss_blocks",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "waystones:waystone"
        },
        {
          "item": "minecraft:moss_block"
        },
        {
          "item": "minecraft:moss_block"
        },
        {
          "item": "minecraft:moss_block"
        },
        {
          "item": "kubejs:arcane_conduit"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "waystones:mossy_waystone"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/waystones/mossy_waystone_from_vines",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "waystones:waystone"
        },
        {
          "item": "minecraft:vine"
        },
        {
          "item": "minecraft:vine"
        },
        {
          "item": "minecraft:vine"
        },
        {
          "item": "kubejs:arcane_conduit"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "count": 1,
        "id": "waystones:mossy_waystone"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/waystones/sandy_waystone",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "S": {
          "item": "minecraft:chiseled_sandstone"
        },
        "W": {
          "item": "waystones:warp_stone"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!S@",
        "SWS",
        "OOO"
      ],
      "result": {
        "count": 1,
        "id": "waystones:sandy_waystone"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/waystones/warp_stone",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "D": {
          "item": "minecraft:amethyst_shard"
        },
        "E": {
          "item": "minecraft:ender_pearl"
        },
        "G": {
          "tag": "c:gems/emerald"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!@D",
        "EGE",
        "DED"
      ],
      "result": {
        "count": 1,
        "id": "waystones:warp_stone"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/waystones/waystone",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "O": {
          "item": "minecraft:obsidian"
        },
        "S": {
          "item": "minecraft:stone_bricks"
        },
        "W": {
          "item": "waystones:warp_stone"
        },
        "!": {
          "item": "kubejs:arcane_conduit"
        },
        "@": {
          "item": "kubejs:control_assembly"
        }
      },
      "pattern": [
        "!S@",
        "SWS",
        "OOO"
      ],
      "result": {
        "count": 1,
        "id": "waystones:waystone"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/electromechanical_drive",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "PWP",
        "SMS",
        "PWP"
      ],
      "key": {
        "P": {
          "item": "immersiveengineering:plate_iron"
        },
        "W": {
          "item": "immersiveengineering:wirecoil_copper"
        },
        "S": {
          "item": "immersiveengineering:stick_steel"
        },
        "M": {
          "item": "minecraft:piston"
        }
      },
      "result": {
        "id": "kubejs:electromechanical_drive",
        "count": 4
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/control_assembly",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "RCR",
        "EPE",
        "RCR"
      ],
      "key": {
        "R": {
          "item": "enderio:redstone_alloy_ingot"
        },
        "C": {
          "item": "mekanism:basic_control_circuit"
        },
        "E": {
          "item": "ae2:logic_processor"
        },
        "P": {
          "item": "kubejs:precision_mechanism"
        }
      },
      "result": {
        "id": "kubejs:control_assembly",
        "count": 4
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/sealed_process_chamber",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "GLG",
        "PFP",
        "GLG"
      ],
      "key": {
        "G": {
          "item": "minecraft:glass"
        },
        "L": {
          "item": "immersiveengineering:plate_lead"
        },
        "P": {
          "item": "kubejs:precision_mechanism"
        },
        "F": {
          "item": "mekanism:basic_fluid_tank"
        }
      },
      "result": {
        "id": "kubejs:sealed_process_chamber",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/arcane_conduit",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "GMG",
        "DCD",
        "GMG"
      ],
      "key": {
        "G": {
          "item": "minecraft:quartz"
        },
        "M": {
          "item": "botania:manasteel_ingot"
        },
        "D": {
          "item": "evilcraft:dark_gem"
        },
        "C": {
          "item": "kubejs:control_assembly"
        }
      },
      "result": {
        "id": "kubejs:arcane_conduit",
        "count": 4
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/quantum_processor",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "VLV",
        "EDE",
        "VLV"
      ],
      "key": {
        "V": {
          "item": "enderio:vibrant_alloy_ingot"
        },
        "L": {
          "item": "botania:elementium_ingot"
        },
        "E": {
          "item": "ae2:engineering_processor"
        },
        "D": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "result": {
        "id": "kubejs:quantum_processor",
        "count": 2
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/radiation_shield",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "pattern": [
        "LSL",
        "HDH",
        "LSL"
      ],
      "key": {
        "L": {
          "item": "immersiveengineering:plate_lead"
        },
        "S": {
          "item": "ad_astra:desh_plate"
        },
        "H": {
          "item": "mekanism:hdpe_sheet"
        },
        "D": {
          "item": "kubejs:quantum_processor"
        }
      },
      "result": {
        "id": "kubejs:radiation_shield",
        "count": 4
      }
    }
  },
  {
    "id": "kubejs:expert_v042/components/reinforced_netherite_plate",
    "recipe": {
      "type": "minecraft:crafting_shapeless",
      "category": "misc",
      "ingredients": [
        {
          "item": "minecraft:netherite_ingot"
        },
        {
          "item": "kubejs:radiation_shield"
        },
        {
          "item": "kubejs:control_assembly"
        }
      ],
      "result": {
        "id": "kubejs:reinforced_netherite_plate",
        "count": 1
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/cells/item_storage_components_cell_4k_part",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:dusts/redstone"
        },
        "b": {
          "item": "ae2:calculation_processor"
        },
        "c": {
          "item": "ae2:cell_component_1k"
        },
        "d": {
          "item": "ae2:quartz_glass"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!ba",
        "cdc",
        "aca"
      ],
      "result": {
        "count": 1,
        "id": "ae2:cell_component_4k"
      }
    }
  },
  {
    "id": "kubejs:expert_v042/ae2/network/cells/item_storage_components_cell_16k_part",
    "recipe": {
      "type": "minecraft:crafting_shaped",
      "category": "misc",
      "key": {
        "a": {
          "tag": "c:dusts/glowstone"
        },
        "b": {
          "item": "ae2:calculation_processor"
        },
        "c": {
          "item": "ae2:cell_component_4k"
        },
        "d": {
          "item": "ae2:quartz_glass"
        },
        "!": {
          "item": "kubejs:dimensional_matrix"
        }
      },
      "pattern": [
        "!ba",
        "cdc",
        "aca"
      ],
      "result": {
        "count": 1,
        "id": "ae2:cell_component_16k"
      }
    }
  }
];
  recipes.forEach(function(row) { event.custom(row.recipe).id(row.id); });
});
