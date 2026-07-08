# Pendências do port (NeoForge 1.21.1)

O que ainda não foi portado do 1.12.2 e por quê. Cada item volta quando o sistema do qual depende
for portado. Nada aqui entra "pela metade" — spell só é registrada quando funciona inteira.

## Sistemas grandes (lotes próprios futuros)

- **Estações GUI (abertura do AS-7)**: Sphere of Cognizance (pesquisa de spells com hints e fuel de
  cristal + renderer próprio), Scribing Desk (criação de scrolls, sistema de blank/mystic scrolls),
  Arcane Anvil (melhoria de artefatos), Artefact Pensive, Skull Watch. Ficaram fora do AS-6 porque
  dependem dos sistemas de conhecimento/scroll/artefato — entram juntos.
- **Blocos restantes do AS-6**: snow_slab/ice_door/ice_bed (+ ice_tower spell), sentinel blocks
  (sistema de sentinelas), dimension boundary/focus (pocket dimension, AS-8), crystal tree
  (worldgen AS-8), placed_rune (sistema de runas), arcane_anvil, skull_watch, artefact_pensive,
  sphere_cognizance, scribing_desk.

- **Sistema de classes de wizard** (Sage/Battlemage/Warlock): ~50 class spells (`IClassSpell`),
  armaduras de classe, mystic spell book, NPCs class wizards. Inclui: conjure_creeper, nether_guard,
  summon_zombie_pigman, chaos orb/blast/field/vortex, absorb_*, runeword, counterspell, ternary_storm,
  scrying_orb, spectral_wall/floor, phase_jump, teleport_object, thoughtsteal, obliteration etc.
  ✓ AS-7b: base do sistema pronta (ClassSpell + gate de set completo via SpellCastEvent.Pre + mystic
  spell book) e primeiras 7 SAGE: conjure_creeper, nether_guard, summon_zombie_pigman, magic_sparks
  + conjure_cake/torchlight/vanish re-vinculadas. TODO warlock attunement no gate.
  ✓ AS-7c (SAGE): conjure_ink, unveil, poison_spray, transplace, extension (bug do 1.12.2 corrigido),
  counterspell (TODO cooldown na wand), ray_of_enfeeblement.
  ✓ AS-7d (WARLOCK): efeito CHAOS completo (17 variantes) + chaos_touch + confusion.
  Restantes SAGE: tome_warp/awaken_tome (lecterns), phase_jump (charge-release), spectral_wall/floor,
  forced_channel, thoughtsteal, ternary_storm, teleport_object, conceal_object, molten_boulder,
  arcane_wall, perfect_theory, experiment (594), pocket_library (822).
  ✓ AS-7e (WARLOCK): chaotic_empowerment (minions explodem/buffam) e chaotic_rebinding (bomba de
  livro dropado -> ruined_spell_book).
  Restantes WARLOCK: absorb_* (sistema de absorcao com orb space), chaos_blast/field/vortex/orb e
  obliteration/scrying_orb (dependem do WARLOCK ATTUNEMENT: elemento da armadura + tabela
  WarlockElementalSpellEffects), alter_potion. BATTLEMAGE: runeword (sistema de runewords).
  Restantes SAGE com sistema proprio: forced_channel/thoughtsteal (escrevem no sage tome via
  WandHelper), tome_warp/awaken_tome (lecterns), phase_jump/spring_charge (charge-release),
  spectral_wall/floor, molten_boulder (entity), arcane_wall, conceal_object, perfect_theory,
  ternary_storm, teleport_object, experiment, pocket_library.
- **Rituais** (14) + lecterns + SpellLecternInteract (5 spells).
- **Metamagic**: efeitos registrados (spell_range/blast/duration/cooldown/siphon, arcane_augmentation,
  intensifying_focus, continuity_charm) mas sem os read-hooks nos modifiers de cast;
  metamagic_projectile e MetaSpellBuff esperam isso.
- **Pocket dimension** (AS-8): pocket_dimension e o sistema de dimensão.
- **Estruturas/worldgen/biomes** (AS-8): 22 estruturas .nbt, spawns do skeleton mage selvagem
  (elemento por bioma), 2 biomes.
- **Artefatos** (211): entram por lote junto dos efeitos. Vários TODO já anotados no código:
  charm_seed_bag (harvest replanta), ring_lily_flower (fileira de lírios), rings de permanência
  (shrink/growth), amulet_elemental_offense + cristal no offhand (raise_skeleton_mage),
  head_fortune (conjure_pickaxe do wizardry), vessel_of_the_withered_oath (plague_of_darkness),
  belt_horse (horse_whistle), ring_disenchanter (words_of_unbinding), head_shield (wizard_shield).

## Spells individuais adiadas

- **farsight, astral_projection, eagle_eye**: sistema de câmera livre client-side (astral travel).
- **static_charge**: sistema de imbuement de enchant em arma (ASEnchantments + duração).
- **words_of_unbinding**: API de remoção de upgrades de wand no Redux.
- **horse_whistle**: rastreio do último cavalo montado (attachment + evento).
- **spring_charge**: mecânica de carga/soltura de cast.
- **living_comet, conduit, prismatic_spray, grapple, contingency, clairvoyance_as, charge_as,
  attire_alteration, master_bolt**: sistemas próprios grandes (um por vez em lotes futuros).
- **Rays com dep de bloco (AS-3d)**: ✓ portados no AS-6c: electrify, shock_zone, firewall,
  molten_earth, summon_quicksand. Restantes: wildfire_flame/arcane_flame-ray/teleportation_flame
  (chamas com mecanica propria), beanstalk, ice_workbench (mesa de gelo), heat_furnace,
  ice_tower (precisa ice_door/snow_slab), runeword_sealbreaker (class), conceal_object (class), moonlight.
  Desvios documentados: shock_zone/magma_shell/static_dome colocam os blocos de uma vez (o 1.12.2
  construia gradualmente via EntityBuilder); create_igloo usa hemisferio oco aproximado (a lista
  manual de posicoes do original nao foi replicada 1:1).
  ✓ portados no AS-6d: magma_shell, static_dome, quicksand_ring, create_igloo, frost_nova.
  ✓ portados no AS-6e: fairy_ring, wild_sporeling, sporelings_aid (cogumelos: fear/mind_trick do
  mushroom_mind em players e amulet_anchoring do mushroom_force ficam TODO; empowering sorteia
  entre mana_regeneration/spell_siphon/spell_cooldown - os 2 ultimos ainda sao shells de metamagic).

## Overrides descartados (não voltam)

- **conjure_pickaxe** e **plague_of_darkness_as**: eram overrides config-gated de spells do próprio
  wizardry para integrar artefatos — a integração virá pelos artefatos, sem override.
- **drain_vitality, hand_of_gaia, SpellProjectileAOEPotion**: classes mortas no 1.12.2
  (nunca registradas, sem JSON).

## Detalhes menores (TODO no código)

- Partículas custom do 1.12.2 (DARK_MIST, RAINDROP, TIME_KNOT...) aproximadas com as do Redux.
- Sons próprios do pack (sounds.json) não portados; usando sons vanilla/Redux próximos.
- Tinte por elemento na robe do skeleton mage; modelo próprio do druid (usa evil wizard).
- cure_zombie converte na hora (startConverting é privado no 1.21; sem delay/reputação).
- Shaders (assets/shaders) — mesmo caso dos shaders do Redux.
- Mecânicas anti-magia do devoritium (handlers) — blocos/itens já existem.
- teleportation_flame e wildfire_flame (chamas com mecânica própria de teleporte/espalhamento).
