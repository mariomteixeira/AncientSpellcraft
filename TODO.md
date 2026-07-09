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
  ✓ AS-7f: WARLOCK ATTUNEMENT pronto (elementOrMagic pela armadura + tabela elemental
  WarlockSpellEffects 1:1; sorcery aproxima force_shove com FORCE+empurrao) + forbidden_tome (livro
  dos warlocks, TODO textura por tier) + chaos_vortex e obliteration (TODO EntityLevitatingBlock;
  sem icone upstream). Warlock spells re-vinculadas ao forbidden_tome.
  ✓ AS-7g: LevitatingBlockEntity + chaos_orb (projetil que se divide) + chaos_field; obliteration
  arranca blocos; chaos_vortex suga blocos (adicao a pedido, nao existia no 1.12.2).
  ✓ AS-7h: 32 warlock orbs (4 tiers x 8 elementos, wands completas via WandItem do Redux) +
  absorb_crystal/spell/potion (canal de 60t, armazenam no attachment WARLOCK_DATA). TODO: consumo
  do conteudo absorvido pelo orb (spell absorvida castavel, pocao aplicavel, elemento p/ upgrade),
  restricao de uso do orb a warlocks, ring_absorb_crystal.
  ✓ AS-7j: chaos_blast (carrega e dispara raio elemental unico; TODO ring multitarget),
  absorb_object (bloco de bolso no attachment; agachado solta no ponto mirado; recusa block
  entities/inquebraveis), absorb_projectile (canaliza absorvendo ate 10 projeteis por TIPO -
  desvio: o original guardava o NBT completo; agachado re-dispara), alter_potion (garrafa no
  offhand + 1o efeito ativo -> splash 50% / lingering 10% agachado; ramo charm_potion_kit TODO).
  Restantes WARLOCK: scrying_orb (grupo da camera client com farsight/astral_projection),
  absorb_artefact (espera os 211 artefatos), orb_space (pocket dimension, AS-8).
  BATTLEMAGE: runeword (sistema de runewords).
  ✓ AS-7k (SAGE): spectral_wall (desvio: colocacao instantanea vs EntityBuilder gradual),
  spectral_floor, teleport_object (usa o circulo de transporte do Redux; TODO charm_hoarders_orb),
  molten_boulder (construct que rola deixando magma conjurado + fogo, render = bloco de magma girando).
  Restantes SAGE com sistema proprio: forced_channel/thoughtsteal (sage tome/WandHelper),
  tome_warp/awaken_tome/perfect_theory (lecterns), phase_jump/spring_charge (charge-release),
  arcane_wall, conceal_object, ternary_storm, experiment (594), pocket_library (822).
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

## AS-7l — Scribing Desk (feito) e pendências das estações

✓ AS-7l: SCRIBING DESK completo (bloco Blockbench original + GUI): relíquia (stone tablet
small/normal/large/grand por tier, spell aleatória da SpellComponentList atribuída ao carregar)
+ componentes de pesquisa (52 entradas parseadas do 1.12.2, resolução em runtime ignora
spells/itens ausentes) + cristais (1, master 2) + tinta (ink sac) + livro -> spell book do AS +
discoverSpell. TODO: ancient_mana_flask como relíquia, loot tables das relíquias (worldgen/AS-8),
transcribing tome, ItemRelic completo (identificação por uso, 835 linhas).
PENDENTE: Arcane Anvil (estação do BATTLEMAGE) — precisa da linha de espadas battlemage
(hilt/blade/espadas por tier/crystal_silver_ingot + WizardClassWeaponHelper), fica com o lote da
classe BATTLEMAGE (runeword + battlemage_sword + contract + shield).

## AS-7m/7n/7o — NPCs, rituais e BATTLEMAGE (feitos) + pendências

✓ AS-7m: ClassWizard/EvilClassWizard (set completo da classe + class spells + livro na mão; desvio:
modelo humanoide do Redux, sem ModelClassWizard; trades herdadas do wizard — TODO trades de class
books; TODO battlemage nos spawns quando a melee AI existir).
✓ AS-7n: rituais funcionais — 24 runas + ritual book; runas jogadas no chão (contagem do pattern) +
canalização 3s. Feitos: bonfire, rejuvenation, warlock_attunement (CLASS SPELLS DE WARLOCK AGORA
EXIGEM O RITUAL), elemental_attunement (+/-25% blast/range). TODO: BlockPlacedRune/TileRune (padrão
desenhado), arcane_barrier/condensing/forest, GUI do ritual book, RitualDiscoveryData, loot de runas.
✓ AS-7o: BATTLEMAGE — 4 espadas (WandItem com dano melee 3/5/7/9 + efeito elemental do set no hit),
hilt/blade/crystal_silver ingot+nugget, battlemage_shield (item; TODO bloqueio de spells 433 linhas),
ARCANE ANVIL (estação 4: hilt+blade->novice, ingot->plating do Redux, espada+ingot->tier seguinte —
TODO exigir progression). TODO: runeword (sistema), battlemage_contract (companion), texturas de
espada por elemento, EnumElementalSwordEffect 1:1, EntityAIBattlemageMelee para NPCs.

✓ AS-7p: prismatic_spray (feixe elemental distinto por inimigo no raio; desvio: tabela
WarlockSpellEffects em vez do switch 1:1; TODO charm_prismatic_spray), ternary_storm (SAGE master:
stormcloud + blizzard do Redux + chão de magma/raio temporário), arcane_wall (bloco arcano
translúcido temporário em muro 3x2; TODO muros de worldgen isGenerated).
Épicos restantes: experiment (594), pocket_library (822), grapple (428), living_comet, conduit,
contingency, master_bolt, charge_as, attire_alteration, conceal_object (mimic block), runeword.

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
