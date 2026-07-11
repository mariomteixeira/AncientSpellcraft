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
  absorb_crystal/spell/potion (canal de 60t, armazenam no attachment WARLOCK_DATA).
  ✓ ring_absorb_crystal (AS-18); consumo do absorvido fechado no AS-28 (nota: "restrição do orb
  a warlocks" não existia no 1.12.2 — o orb é wand comum; só as class spells têm gate).
  ✓ AS-7j: chaos_blast (carrega e dispara raio elemental unico; TODO ring multitarget),
  absorb_object (bloco de bolso no attachment; agachado solta no ponto mirado; recusa block
  entities/inquebraveis), absorb_projectile (canaliza absorvendo ate 10 projeteis por TIPO -
  desvio: o original guardava o NBT completo; agachado re-dispara), alter_potion (garrafa no
  offhand + 1o efeito ativo -> splash 50% / lingering 10% agachado; ramo charm_potion_kit TODO).
  Restantes WARLOCK: absorb_artefact (espera os 211 artefatos). ✓ scrying_orb (AS-14),
  ✓ orb_space (AS-8d).
  BATTLEMAGE: runeword (sistema de runewords).
  ✓ AS-7k (SAGE): spectral_wall (desvio: colocacao instantanea vs EntityBuilder gradual),
  spectral_floor, teleport_object (usa o circulo de transporte do Redux; TODO charm_hoarders_orb),
  molten_boulder (construct que rola deixando magma conjurado + fogo, render = bloco de magma girando).
  SAGE COMPLETA: ✓ phase_jump (AS-16), ✓ AS-15:
  forced_channel/thoughtsteal/tome_warp/awaken_tome/perfect_theory. ✓ spring_charge (AS-14),
  arcane_wall/ternary_storm (AS-7p), conceal_object (AS-10a), experiment/pocket_library (AS-11).
- **Rituais** (14) + lecterns + SpellLecternInteract (5 spells).
- **Metamagic**: ✓ AS-9a (3.41.0): READ-HOOKS prontos (ASMetamagicEvents no SpellCastEvent.Pre —
  spell_blast/range/cooldown/duration por nível enquanto o buff dura; arcane_augmentation
  (range+blast), intensifying_focus (potency) e continuity_charm (duration + custo) one-shot,
  consumidos no cast; spell_siphon recarrega 5*nível de mana por kill no primeiro item com mana)
  + 3 SPELLS novas via MetamagicBuffSpell (um metamagic por vez, re-cast amplifica até nível III,
  nível III re-cast = 90% desconto no cooldown; advanced/sorcery — element consistente com a
  reclassificação de extension; type METAMAGIC novo no Redux 0.1.26). Os shells do fairy_ring
  (spell_siphon/spell_cooldown do empowering) agora funcionam.
  ✓ AS-9b (3.42.0): metamagic_projectile — spell arma flag (attachment PLAYER_DATA); o próximo cast
  (que não seja ray/arrow/projectile/metamagic) é cancelado e vira MetamagicProjectileEntity que
  casta a spell no impacto (LocationCastContext) e adota minions órfãos recém-conjurados; render =
  MagicProjectileRenderer com dispel_magic.png; velocidade pela fórmula 1.12.2 (range 20*RANGE,
  g=0.03). Desvios: sem blacklist de config (marco 7), som vanilla.
  ✓ AS-9c (3.42.0): RITUAIS CONTÍNUOS via ritual_core (bloco flat + BlockEntity — substitui o
  TileRune): arcane_barrier (mantém ForcefieldConstruct do Redux, raio 4 — desvio: sem cor/
  allow_players/piso 5x5 do original), condensing (engole shard/cristal jogado, ciclo 180t
  +0.75/+0.25, shard→cristal→grand; clique devolve; item flutuando via BER — desvio: sem os
  crystal_shards elementais do AS, não portados), forest (engole sapling e planta/cresce árvores
  raio 3-30 a cada 40t, expira em 1600t). Padrões de runas dos JSONs 1.12.2. Loot de runas já
  coberto no AS-8a (baús vanilla). Restante metamagic: ring_metamagic_preserve e
  charm_metamagic_amplifier (artefatos), cooldown de item no consumo.
- **Pocket dimension** (AS-8): pocket_dimension e o sistema de dimensão.
- **Estruturas/worldgen/biomes** (AS-8): 22 estruturas .nbt, spawns do skeleton mage selvagem
  (elemento por bioma), 2 biomes.
- **Artefatos** (211): entram por lote junto dos efeitos. Vários TODO já anotados no código:
  charm_seed_bag (harvest replanta), ring_lily_flower (fileira de lírios), rings de permanência
  (shrink/growth), amulet_elemental_offense + cristal no offhand (raise_skeleton_mage),
  head_fortune (conjure_pickaxe do wizardry), vessel_of_the_withered_oath (plague_of_darkness),
  belt_horse (horse_whistle), ring_disenchanter (words_of_unbinding), head_shield (wizard_shield).

## Spells individuais adiadas

- ✓ farsight, astral_projection, eagle_eye, scrying_orb, words_of_unbinding, spring_charge,
  static_charge, horse_whistle: portadas (AS-13/AS-14).
- **living_comet, conduit, prismatic_spray, grapple, contingency, clairvoyance_as, charge_as,
  attire_alteration, master_bolt**: sistemas próprios grandes (um por vez em lotes futuros).
- **Rays com dep de bloco (AS-3d)**: ✓ portados no AS-6c: electrify, shock_zone, firewall,
  molten_earth, summon_quicksand. ✓ AS-14: arcane_flame/wildfire_flame/enchant_fireplace.
  ✓ AS-32: beanstalk, ice_tower (+snow_slab/ice_door). NOTA: ice_workbench já estava portado;
  heat_furnace e moonlight NÃO existem/código morto no 1.12.2 (não portam).
  ✓ runeword_sealbreaker (AS-15), conceal_object (AS-10a).
  Desvios documentados: shock_zone/magma_shell/static_dome colocam os blocos de uma vez (o 1.12.2
  construia gradualmente via EntityBuilder); create_igloo usa hemisferio oco aproximado (a lista
  manual de posicoes do original nao foi replicada 1:1).
  ✓ portados no AS-6d: magma_shell, static_dome, quicksand_ring, create_igloo, frost_nova.
  ✓ portados no AS-6e: fairy_ring, wild_sporeling, sporelings_aid (cogumelos: fear/mind_trick do
  mushroom_mind em players e amulet_anchoring do mushroom_force ficam TODO; empowering sorteia
  entre mana_regeneration/spell_siphon/spell_cooldown - os 2 ultimos ainda sao shells de metamagic).

## AS-7l — Scribing Desk (feito) e pendências das estações

✓ AS-7l: SCRIBING DESK completo (bloco Blockbench original + GUI): relíquia (stone tablet
small/normal/large/grand por tier) + componentes de pesquisa (52 entradas parseadas do 1.12.2,
resolução em runtime ignora spells/itens ausentes) + cristais (1, master 2) + tinta (ink sac) +
livro -> spell book do AS + discoverSpell.
✓ AS-26 (3.59.0): SCRIBING/RITUAIS RESIDUAIS —
  ITEMRELIC COMPLETO: relíquia agora nasce SEM conteúdo; pesquisa = Scroll of Identification na
  outra mão (desvio: o 1.12.2 pesquisava na Sphere of Cognizance, não portada; relíquias antigas
  com spell contam como pesquisadas). Pesquisa sorteia SPELL 40% (scribing desk) / INCANTATION 15%
  (poção 10-60min por tier) / ENCHANTMENT 25% (tier+1 encantos aleatórios sem curses/encantos do
  wizardry, aplica no item da offhand com checagem de compatibilidade) / POWER 20% (metamagic/
  empowerment 5-20min); INCANTATION/POWER/ENCHANTMENT canalizam 120t no right-click e consomem;
  pools de efeito resolvidos em runtime (ausentes ignorados); sons vanilla (sounds.json não portado).
  ancient_mana_flask registrado como relíquia (loot: dungeon_additions w2 — no 1.12.2 não tinha
  loot nenhum). TRANSCRIBING TOME (charm UNCOMMON): canaliza 3s com scroll na offhand, tomo vira o
  spell book da spell (tipo de livro via applicableForItem). EVERFULL MANA FLASK (charm UNCOMMON):
  reserva 1400 auto-reenche +3/50t (condenser interval), use na offhand transfere 140 pra mainhand
  (desvio: textura única + barra em vez dos 11 modelos). RITUAL DISCOVERY: attachment KNOWN_RITUALS
  (copyOnDeath) + sync S2C (login/respawn/dimensão) + descoberta por Scroll of Identification na
  outra mão do ritual book (consome, mensagem); não descoberto = nome/descrição em ELDER FUTHARK
  na GUI e tooltip (runas continuam visíveis — dá pra executar sem descobrir, igual ao 1.12.2);
  criativo vê tudo. NOTA: o gate upstream estava DESATIVADO (hasRitualBeenDiscovered retornava
  true fixo) — o port ativa o sistema como projetado.
✓ AS-27 (3.60.0): SPHERE OF COGNIZANCE FECHADA — a estação já existia parcial (pesquisa passiva de
  spell books/scrolls); agora: PESQUISA DE RELÍQUIAS (80t, 2 cristais, sorteia o conteúdo via
  RelicItem.research + dica ancient_knowledge — vira o caminho primário; o identification scroll
  do AS-26 segue como alternativa), CHARM_STONE_TABLET/Moon-letter Dictionary (EPIC: relíquia
  pesquisada sob lua cheia = tipo SPELL garantido; desvio: sem a textura fullmoon por item
  property), dicas por NOME de spell (heal_ally/pocket_furnace/arcane_lock/remove_curse/
  resurrection — lista de candidatos nome/tipo/elemento do 1.12.2, +5 hint types), FIX: spell já
  conhecida pelo último usuário não pesquisa (não queima cristal — no port anterior queimava),
  128 chaves de lang dos hints em formato dot. Desvios mantidos: pesquisa passiva sem botão/GUI
  aberta (1.12.2 exigia inUse + botão), identification chance 0.05 hardcoded (config marco 7).
  TODO marco 6: BER animado (ModelSphereCognizance girando).
✓ AS-28 (3.61.0): WARLOCK — CONSUMO DO ABSORVIDO fechado.
  CAST DA SPELL ABSORVIDA: keybind H (key_warlock_cast) → WarlockCastC2S → servidor casta a spell
  do WARLOCK_DATA pagando FOME (max(1, custo/5); amulet_spellbinding NOVO artefato RARE = metade;
  fome 0 = dano de inanição custo/5) com gate warlock attuned/set completo e cooldown da spell
  (desvios: cooldown no attachment em vez do item-marcador dimension_focus; contínua absorvida dá
  pulso único em vez dos 40t; sem packet de END — era no-op no 1.12.2).
  GATES DE FIDELIDADE no absorb_spell: tier_limit (property, default 3) + satiety/replenish_hunger
  proibidas (loop de comida grátis). CRISTAL: bloco absorvido agora dá +10% de potência (flag
  ElementBlock; cristal comum +5%). Poção absorvida (aura) já estava fiel do AS-7h.
  FORBIDDEN_TOME POR TIER: property "ancientspellcraft:tier" via ItemProperties (selado até
  descobrir a spell; novice/apprentice/advanced/master — texturas 1.12.2, fecha o TODO do AS-7f).
✓ AS-29/30/31 (3.62-3.64.0): BATTLEMAGE FECHADO (residuais do AS-7o).
  AS-29 ESCUDO: mana 1000 no lugar de durabilidade (default runic_shield_durability; config marco 7);
  bloquear consome mana (regra vanilla: dano >= 3 gasta 1+floor) via LivingShieldBlockEvent sem
  desgastar o item; mana vazia derruba a guarda; +5 armor/+5 toughness offhand; só battlemage de
  set completo levanta; charm_glyph_shield_disable (RARE) remove o cooldown de quebra de guarda
  (machado/runeword_shatter derrubam via Player.disableShield — o shatter do AS-15 já era fiel);
  runeword_meditate agora transfere DO escudo pra lâmina (fecha o desvio do AS-12b). Desvios:
  recarga por sneak-use com magic_crystal (1.12.2: workbench/condenser/flask; upgrades marco 7),
  sem os 3 slots de artefato (Curios cobre).
  AS-30 EFEITOS 1:1: ElementalSwordEffects substitui a aproximação WarlockSpellEffects (tabela
  lesser/greater completa do EnumElementalSwordEffect — fire/ice/lightning/necromancy/earth/
  sorcery/healing; lightning inclui a recarga por andar); anvil exige progression do próximo tier
  no upgrade (excedente preservado); 32 texturas de espada por elemento via property client
  "ancientspellcraft:element" (elemento do set vestido). Desvios: embers/beam aproximados;
  lesser do SORCERY (era um evento de left-click à parte no 1.12.2) TODO.
  AS-31 NPC + CONTRACT: BattlemageMeleeGoal (golpe com efeitos elementais; desvio: ativa com alvo
  a <=5 blocos — o 1.12.2 usava o cooldown de spell como gatilho) nos class wizards friendly e
  evil; BATTLEMAGE de volta ao sorteio de spawn das 3 classes (espada master + escudo, drop 0);
  battlemage_contract (RARE): usado num battlemage friendly contrata mercenário por 7 dias
  (segue o dono + ataca quem o atacou; desvio: sistema Covenant do 1.12.2 virou vínculo direto
  no wizard).
✓ AS-32 (3.65.0): SPELLS RESTANTES —
  BEANSTALK (ray contínuo: trepadeira sobe/desce a parede, max_height do JSON);
  ICE_TOWER + blocos snow_slab (slab com item/loot) e ice_door (sem item/drop, só via torre) —
  torre hardcoded 1:1 com escada em espiral, porta de gelo e janelas;
  ABSORB_ARTEFACT (warlock channel: body_power_gem NOVO artefato acumula PowerGems, +1% de
  potência por gema no cast até 30; outros artefatos ficam INERTES em AbsorbedArtefact — fiel ao
  1.12.2 shipped, que tinha a ativação comentada; blacklist com os 5 defaults; tier_limit=2);
  COVENANT (alia um wizard comum: segue + ataca monstros; um por vez; re-cast desfaz; re-aplica
  no EntityJoinLevel; desvio: retarget NearestAttackableTarget<Monster> em vez do seletor com
  whitelist de config; reclassificada ebwizardry:sorcery);
  CONTINGENCY immobility (paralysis/containment/slow_time/frost/slowness aplicado — defaults
  1.12.2, config marco 7) e hostile_spellcast (spell attack/alteration/minion/projectile de
  não-aliado num raio de 20);
  COUNTERSPELL fiel: player alvo ganha o cooldown cheio da spell atual da wand
  (CastItemDataHelper), NPC conjurador ganha exaustão mágica V 4s;
  MUSHROOM_MIND em mobs: 30% mind control / 20% fear / resto mind_trick (efeitos do Redux;
  players já eram fiéis com nausea+blindness);
  CURE_ZOMBIE fiel: startConverting aberto por ACCESS TRANSFORMER (primeiro AT do projeto,
  META-INF/accesstransformer.cfg) — delay 3-5 min + caster como conversion starter (reputação);
  METAMAGIC: consumir one-shot põe a spell em cooldown de 1200t nas wands da hotbar.
PENDENTE: Arcane Anvil (estação do BATTLEMAGE) — precisa da linha de espadas battlemage
(hilt/blade/espadas por tier/crystal_silver_ingot + WizardClassWeaponHelper), fica com o lote da
classe BATTLEMAGE (runeword + battlemage_sword + contract + shield).

## AS-7m/7n/7o — NPCs, rituais e BATTLEMAGE (feitos) + pendências

✓ AS-7m: ClassWizard/EvilClassWizard (set completo da classe + class spells + livro na mão; desvio:
modelo humanoide do Redux, sem ModelClassWizard; TODO battlemage nos spawns quando a melee AI
existir). ✓ AS-17: trades de classe (compra spell books por 5 cristais; 50%: warlock vende runa
aleatória, sage/battlemage vendem stone tablet por tier; desvios: sem blank_rune, sem o trade de
artefato uncommon — loot subset não existe no Redux).
✓ AS-7n: rituais funcionais — 24 runas + ritual book; runas jogadas no chão (contagem do pattern) +
canalização 3s. Feitos: bonfire, rejuvenation, warlock_attunement (CLASS SPELLS DE WARLOCK AGORA
EXIGEM O RITUAL), elemental_attunement (+/-25% blast/range). TODO: BlockPlacedRune/TileRune (padrão
desenhado), arcane_barrier/condensing/forest ✓ (AS-9c), loot de runas ✓ (AS-8a). ✓ AS-17: GUI do
ritual book (click normal lê — nome/descrição/runas com ícones; sneak canaliza; desvios: página
única, sem elder futhark/RitualDiscoveryData — descoberta segue fora).
✓ AS-7o: BATTLEMAGE — 4 espadas (WandItem com dano melee 3/5/7/9 + efeito elemental do set no hit),
hilt/blade/crystal_silver ingot+nugget, battlemage_shield (item; TODO bloqueio de spells 433 linhas),
ARCANE ANVIL (estação 4: hilt+blade->novice, ingot->plating do Redux, espada+ingot->tier seguinte —
TODO exigir progression). TODO: runeword (sistema), battlemage_contract (companion), texturas de
espada por elemento, EnumElementalSwordEffect 1:1, EntityAIBattlemageMelee para NPCs.

✓ AS-7p: prismatic_spray (feixe elemental distinto por inimigo no raio; desvio: tabela
WarlockSpellEffects em vez do switch 1:1; TODO charm_prismatic_spray), ternary_storm (SAGE master:
stormcloud + blizzard do Redux + chão de magma/raio temporário), arcane_wall (bloco arcano
translúcido temporário em muro 3x2; TODO muros de worldgen isGenerated).
✓ AS-10a (3.43.0): LIVING_COMET (contínua master/fire: caster vira cometa — sobe 20t, acelera no
olhar, controle vertical 100t+, pouso 40t+ explode 1.8 + fire resistance; desvio: branch do
charm_meteorite_stone fica com artefatos) e CONCEAL_OBJECT (SAGE ray: bloco mirado vira
concealed_block invisível/atravessável com estado+BE guardados; re-cast reverte; element sorcery
por consistência com as demais sage). CHARGE_AS descartado — override config-gated do spell charge
do wizardry (mesmo precedente de conjure_pickaxe/plague_of_darkness_as).
✓ AS-10b (3.43.0): SKULL WATCH funcional (detecta não-aliado com linha de visão raio 15 → mensagem
ao dono com cooldown 12s/entidade, grito a cada 50t, redstone 15 enquanto triggered; modelo
Blockbench 1.12.2; desvios: toggles glowing/skeleton = artefatos Sentinel Eye/Domus Amulet no lote
de artefatos, som vanilla) e ARTEFACT PENSIVE (banco de XP: clique deposita tudo até 1395 = 30
níveis, shift-clique saca; property empty muda o modelo Blockbench cheio/vazio).
✓ AS-11 (3.44.0): ÉPICOS FECHADOS —
  attire_alteration (troca com o set guardado no PLAYER_DATA; wardrobe de 5 sets = charm_wardrobe TODO);
  conduit (contínua, mana mainhand→offhand por tick com eficiência das properties; ring_mana_transfer
  e charm_progression_orb TODO artefatos);
  master_bolt (item + projétil SHOCK que vira BLOCO Blockbench onde cai + re-cast puxa o jogador
  como raio com rastro de lightning_block temporário, explosão proporcional à queda e devolve o
  item; sneak-cast recolhe; desvio: sem static_aura no puxão);
  contingency x8 (arma listener → captura a próxima spell → dispara no gatilho: fire/drowning/
  damage/critical_health/fall/death implementados; immobility e hostile_spellcast TODO;
  ring_eternal_contingency TODO artefatos; disparo automático não cobra mana);
  grapple (cipó físico 1:1 — bloco puxa você, entidade vem até você, estica 1.5x e arrebenta;
  SEM JSON 1.12.2 de referência (era gerado em runtime): properties/tier/elemento/custo definidos
  no port (advanced/earth, range 12) + textura de spell nova — revisar se quiser; corda = partículas
  LEAF; charm_abseiling TODO artefatos);
  experiment (SAGE: chance base + elementos² %, sucesso = theory point no PLAYER_DATA — moeda do
  perfect_theory pendente; falha = forfeit do Redux/buff/debuff/nada; buffs/debuffs aproximados);
  pocket_library (SAGE: invoca a torre-template na frente e re-cast a 3 blocos desfaz restaurando
  o terreno via snapshot; desvio: sem persistência dos blocos do jogador dentro da torre).
✓ AS-12 (3.45.0): RUNEWORDS DO BATTLEMAGE — infra (RunewordSpell base configurável: cast com a
espada de battlemage arma a runeword ativa com cargas no CustomData; ASRunewordEvents aplica
efeito/modificador de dano por golpe e gasta carga; instants resolvem no cast) + 14 runewords:
arcane (dano x%), ignite, sol (cegueira), restoration (cura % vida perdida), disarm (cooldown nas
mãos do player alvo), expose (degraded_armor stacking), suppress (magical_exhaustion stacking),
exorcise (dano x vs mortos-vivos, fogo+weakness vs vivos), endure (resistance+ward+slowness),
implode (puxa inimigos no raio), fury (stacks de dano por golpe, decai 1/2s no inventoryTick),
displace (teleporta o alvo), strength (self-buff), blast (knockback forte).
Desvios: UMA runeword ativa por lâmina (original tinha mapa); fury armada por cast (era passiva
por glyph); arcane sem bypass de armadura; blast sem checks de anchoring.
✓ AS-12b (3.46.0): +6 runewords — briar (chance de brotar THORNS do Redux no alvo, sem gastar
carga), meditate (contínua: escudo de battlemage na offhand recarrega 3 de mana/10t na LÂMINA —
desvio: o escudo do port não guarda mana), imbue (consome poção da hotbar e grava o efeito na
lâmina; cada golpe aplica), reach/pull/push (golpe estendido até 9 blocos no clique em vazio —
client LeftClickEmpty + payload ExtendedReachC2S; pull puxa, push arremessa; attack() gasta a
carga pelos hooks normais). ✓ sealbreaker/shatter/empower fechadas no AS-15.
✓ AS-13 (3.46.0): STATIC_CHARGE (imbui a primeira espada da hotbar/offhand: +2 de dano por nível
por effect_duration; nível pela potência; desvio: carga no CustomData em vez de enchantment —
1.21 é data-driven e o efeito é handler de qualquer forma; sem glint) e HORSE_WHISTLE (assobia
pelo último cavalo/mais próximo domado raio 100; >20 teleporta, senão galopa até você;
belt_horse TODO artefatos).
✓ AS-25 (3.58.0): ARTEFATOS ONDA 4 — mana e sockets (10; Redux 0.1.28 sem mudança de API — o
getter getMobEffects já existia). INFRA: ManaArtifactItem (reserva de mana no CustomData, começa
cheio, barra de mana; recarga = sneak-use com magic_crystal na offhand, +MANA_PER_CRYSTAL do
config — desvio: 1.12.2 recarregava no arcane workbench) e SocketedArtifactItem (1 encaixe:
sneak-use com o item na offhand encaixa, sneak-use com offhand vazia devolve — desvio: 1.12.2
tinha GUI de slot).
BATERIAS (3): ring_mana_lesser (500), ring_mana_greater (1000), charm_majestic_mana (2500) —
cast por WAND com mana insuficiente puxa o custo da bateria equipada (handler fiel ao 1.12.2).
MANA PRÓPRIA (4): charm_wizard_tankard (enche +1/6s até 100; cheio dá pra beber:
mana_regeneration VIII 20s), charm_cube_phasing (carrega 1/s até 1000; use casta BLINK pagando
com a carga — desvio: sneak castava phase_step no 1.12.2, aqui sneak é a recarga),
amulet_invisibility (1500: -4 mana/2s = invisibilidade contínua), amulet_of_resistance (600:
sneak-use com poção imbui o 1º efeito; enquanto tiver mana, o efeito é removido de você, -1/s).
SOCKETS (3): amulet_elemental_offense virou socketed (cristal encaixado: +0.05 potência no
elemento; e o raise_skeleton_mage foca o elemento SEM risco de quebra), amulet_elemental_defense
(dano mágico do elemento do cristal x0.7 — mapa fire/frost/shock/wither/force/radiant),
amulet_pendant_of_eternity (encaixa spell book de BUFF: quando um efeito do buff expira,
re-casta sozinho, cd 1s).
✓ FORA DAS ONDAS fechado no AS-33 (3.66.0): ARTEFATOS-SISTEMA —
  charm_infernal_stone (calor 0-100: absorve FOGO no raio 3 segurando, -1/3s equipado; quente =
  fire spells -25% de mana consumindo 10 de calor; calor >= 50 = combustível 8000t sem se gastar);
  charm_diamond_goose (sneak-use grava o TIPO do bloco; equipado grasna por distância <=4/5-8,
  na mão indica direção relativa a cada 10s; SOM goose_1/2.ogg portado — primeiro do sounds.json);
  head_mask_of_perseigni (a cada 1s: wand da hotbar com PYROKINESIS + 15 de mana = aplica os
  efeitos da spell no alvo na mira de 10 blocos pagando a mana) — PYROKINESIS PORTADA junto
  (spell apprentice/fire que faltava);
  ring_arcane_flames (fogo próximo vira chama arcana 200t; desvio perf: raio 10 a cada 20t —
  1.12.2 varria raio 20 a cada ~10t);
  charm_devoritium_magnet (use: war_horn.ogg + exaustão mágica V 8s em criaturas no raio 15,
  cd 60s; delegates anti-magia do devoritium ✓ 3.68.0);
  belt_scroll_holder (SocketedArtifactItem: encaixa wand upgrade exceto storage/siphon/attunement/
  melee; com condenser = +1 mana/50t nas mãos; desvio: sem a GUI de slot do 1.12.2);
  DAILIES x6 (1x por dia de MC; desvio: sem a textura ready/not_ready por item property):
  cornucopia (comida aleatória), charm_bucket_coal (5-10 carvão), charm_gold_bag (2-8 nuggets),
  charm_evergrowing_crystal (10% astral shard/5% diamante/esmeralda), charm_philosophers_stone
  (alchemical_essence NOVA — transmuta 2-5 ferro em ouro no offhand), charm_remnant_cage (captura
  Remnant do Redux, diário = spectral dust do elemento, sneak solta como minion — desvio: minion =
  Remnant + MinionData, sem EntityRemnantMinion próprio).
  ItemSetArtefact (jóias de poder): o set bônus já estava no AS-18 — nada novo.
✓ AS-24 (3.57.0): ARTEFATOS ONDA 3d — tickables especiais (7): charm_specterlight_torch (revela
invisíveis raio 10 — remove invisibility/muffle; mirage não existe no Redux),
belt_enchanted_whetstone (repara 1 durabilidade de espada/machado na mão a cada 5s),
amulet_searing_skin (fireskin ativo = regen contínua), head_ardor_crown (seus minions raio 8:
speed II + strength), head_minion_circle (minions ociosos raio 20 orbitam em círculo de raio 3 —
desvio: sem remover IA de perambular), charm_guardian_blade (vida ≤33%: lâmina espectral animada
15s com dano x6, cd 30s), amulet_elemental_aura (static_aura/fireskin/ice_shroud ativo: 1/3 por
segundo de descarregar o elemento em inimigos raio 5 — dano SHOCK / fogo effect_duration x5 /
frost; valores lidos das spells do Redux em runtime).
ADIADOS PARA ONDA 4: baterias de mana (ring_mana_lesser/greater, charm_majestic_mana,
charm_wizard_tankard, charm_cube_phasing, amulet_invisibility, amulet_of_resistance — precisam
de slot no arcane workbench e/ou consumo), charm_diamond_goose (bind de bloco + sons),
head_mask_of_perseigni (auto-pyrokinesis de wand), charm_infernal_stone (sistema de calor),
ring_arcane_flames, charm_devoritium_magnet (delegate herdado do devoritium),
charm_glyph_shield_disable (bloqueio do shield), belt_scroll_holder.
✓ AS-23 (3.56.0): ARTEFATOS ONDAS 3b+3c — glyphs e auras do battlemage (13).
GLYPHS (5): leeching (30% no golpe da lâmina ROUBA um buff do alvo — desvio: no 1.12.2 vivia
dentro de um efeito elemental específico, no port vale para qualquer golpe da espada),
antigravity (jump boost + speed segurando a espada, dano x0.7), might (dano x1.2),
charge (cargas de runeword x2 ao armar — gate no RunewordSpell.cast), imbuement (cargas do
runeword_imbue x6 — gate no registro). glyph_shield_disable ADIADO para 3d (o shield do port
ainda não tem cooldown/bloqueio).
AURAS (8, tick 0.5s raio 10, exigem SET COMPLETO de battlemage): alacrity (speed aliados+self),
hatred (strength em aliados mortos-vivos), life (regen aliados+self), purity (slowness+weakness
em inimigos mortos-vivos), warding (ward aliados, ward II self), wither (wither em inimigos,
com 0.01 de dano para puxar agro), defense (improved_armor IV self), vulnerability
(degraded_armor V em inimigos). charm_aura_tenacity NÃO portado (registrado sem efeito no
1.12.2 — código morto).
✓ AS-22 (3.55.0): ARTEFATOS ONDA 3a — buffs de tick (5): amulet_celerity (+10% velocidade,
modifier transiente com limpeza no PlayerTick), amulet_oakflesh (oakflesh do Redux ativo em
bioma de floresta = regeneração contínua), amulet_holy_aura (mortos-vivos raio 6 pegam fogo 5s
a cada 2s), amulet_healing_mushroom (ferido: 50% a cada 6s de brotar cogumelos de cura raio 7,
cd 45s), amulet_cursed_pendant (meia-noite: 50% de invocar evil class wizard vingativo, cd 15
min). ONDA 3 dividida: 3a ✓; 3b = glyphs (leeching, antigravity, imbuement, shield_disable,
might, charge); 3c = auras (alacrity, hatred, life, purity, warding, wither, tenacity, defense,
vulnerability); 3d = tickables especiais + mana (ring_mana_lesser/greater, charm_majestic_mana —
baterias do workbench; amulet_of_resistance, amulet_invisibility — mana própria;
specterlight_torch, wizard_tankard, diamond_goose, cube_phasing, devoritium_magnet,
elemental_aura, searing_skin, ardor_crown, mask_of_perseigni, minion_circle, guardian_blade,
enchanted_whetstone, infernal_stone, ring_arcane_flames — sistemas próprios).
✓ AS-21 (3.54.0): ARTEFATOS ONDA 2c — poções/flechas/tick (9): belt_temporal_anchor (imune a
slowness), amulet_poison_resistance (50% nega veneno), amulet_curse_ward (nega maldições),
amulet_persistence (imune a shrinkage/growth), amulet_cursed_mirror (50% espelha maldição em
conjuradores raio 12), amulet_absorb_potion (nega o efeito ruim absorvido pelo absorb_potion),
ring_poison_arrow (flecha: 20% veneno 5s — via ProjectileImpactEvent), head_mask_of_silence
(exaustão mágica I em conjuradores raio 8, 1x/s), ring_prismarine (pegando fogo: apaga + fire
resistance 6s, cd 60s — desvio: sem castar extinguish, spell não portada). charm_ice_arrow NÃO
portado (código morto/comentado no 1.12.2). ONDA 2 COMPLETA (2a+2b+2c = 20 artefatos).
✓ AS-20 (3.53.0): ARTEFATOS ONDA 2b — morte e tempo (5): ring_undeath (dano letal: maldição de
morto-vivo permanente + cura 50% + sobrevive, cd 5 min), amulet_time_knot (dano letal com
time_knot ativo: volta ao ponto gravado em vez de morrer — loopPlayer extraído do expiry —,
apaga fogo, cd 5 min), amulet_time_slow (vida ≤6: slow_time do Redux 6s, cd 8 min),
charm_reanimation (matar esqueleto/zumbi: 15% de erguer esqueleto minion 30s, arco se o morto
usava — minion = Skeleton vanilla + MinionData, igual domus), charm_plunderers_mark (matar evil
wizard — inclui evil class wizard — dropa astral diamond shard).
✓ AS-19 (3.52.0): ARTEFATOS ONDA 2a — dano recebido (6, via IArtifactEffect.onPlayerHurt):
charm_cryostasis (vida ≤6: 25% de castar cryostasis em si), ring_protector (vida baixa: 50% de
castar de graça uma spell de minion de wand da hotbar — desvio: primeira encontrada, sem mexer
em seleção/cooldowns), ring_berserker (STRENGTH 15s), amulet_rabbit (25%: SPEED 10s + WEAKNESS II
10s), amulet_shield (dano >1: wizard_shield XVI 5s, cd 3 min), belt_soul_scorch (soul_scorch 3s
no atacante direto). ONDA 2 dividida em 3: 2a ✓; 2b = morte/tempo (ring_undeath,
charm_reanimation, charm_plunderers_mark, amulet_time_knot, amulet_time_slow); 2c =
poções/flechas/tick (absorb_potion, curse_ward, cursed_mirror, pendant_of_eternity, persistence,
poison_resistance, belt_temporal_anchor, charm_ice_arrow, ring_poison_arrow, head_mask_of_silence,
ring_prismarine). amulet_elemental_defense adiado pra onda 4 (cristal encaixado/slots).
✓ AS-18 (3.51.0): ARTEFATOS ONDA 1 — 43 artefatos funcionais (só registra com efeito; decisão do
usuário). INFRA: Curios 9.5.1 no modpack (Redux já integra; fallback hotbar/inventário sem ele);
curios/entities/player.json ativa os slots ring/necklace/charm/belt/head/body (cobre também os
artefatos do EB); tags curios por tipo; loot via injeção nos subsets ebwizardry
(uncommon/rare/epic_artifacts, que já existiam por datagen no Redux) com tabelas próprias do AS.
GANCHOS (26): seed_bag, lily_flower (fileira 10 x range), hoarders_orb, prismatic_spray (feixe
único x1.5 + poison/paralysis/wither/blindness/frost/fogo), chaos_blast_multitarget (2º alvo 60%),
eternal_contingency (não consome; cooldown do anel cd*10+(tier+1)*500), permanent_shrinkage/growth
(duração infinita), elemental_offense (cristal offhand sem quebra; desvio: o 1.12.2 usava cristal
encaixado no amuleto — sem slots no port), absorb_crystal (blocos de cristal), potion_kit (gate
fiel do ramo de engarrafar no alter_potion; sem o charm a spell recusa — o ramo de mapping por
config fica pro marco 7), chaos_magic (attunement MAGIC + 25% potência warlock class spells),
wardrobe (5 conjuntos em ciclo; sem o cloak visual), meteorite_stone (gate fiel do pouso-meteoro
do living_comet — antes explodia sem o charm), mana_transfer (conduit por ray no jogador mirado),
progression_orb (conduit transfere progressão 10/tick), disenchanter (desencanta offhand),
belt_horse (speed no cavalo montado), sentinel_eye/domus (skull watch: glowing no intruso /
esqueleto minion cd 1200t — flags pelo artefato ao COLOCAR o bloco; desvio: 1.12.2 setava no cast
do skull_sentinel, spell não portada; minion = Skeleton vanilla + MinionData, lifetime 600),
kinetic (speed II ao pisar no lightning_block), glyph_illumination/magic_light (mantêm mage
light), head_shield (wizard_shield decai -1 e renova 70t — de quebra o decaimento base ficou fiel:
amp0 não renova mais), metamagic_preserve (33% de não consumir o buff one-shot),
metamagic_amplifier (metamagic novo começa no nível II).
MODIFICADORES (17): mana_orb/amulet_mana/ring_mana_cost (custo x0.85/0.90/0.95),
ring_blast/range/duration (custo x1.25, +0.25 no modificador), elemental_grimoire (+0.1 potência
fire/ice/lightning), 7 orbs elementais (+0.30 no elemento / -0.5 fora; default 30 do 1.12.2),
joias de poder ring/amulet/orb (+0.05/0.10/0.20 potência e custo) + set bônus (2+ joias = +5%
potência por peça extra, handler próprio).
ADIADOS: head_fortune (conjure_pickaxe do Redux), vessel_of_the_withered_oath
(plague_of_darkness do Redux), charm_infernal_stone (sistema de calor), elemental cloaks/belts
(itens com elemento + tick próprios — onda 2/3), potência por cristal encaixado do
elemental_offense (slots — onda 4).
✓ AS-17 (3.50.0): RESIDUAIS — GUI DO RITUAL BOOK (RitualBookScreen: click normal lê o livro —
nome, descrição importada do 1.12.2 e runas com ícones/contagem/tooltip; sneak canaliza; desvios:
página única, sem futhark/discovery), TRADES DE CLASSE no ClassWizard (compra spell books por 5
cristais; 50%: warlock vende runa aleatória — sem blank_rune no port; sage/battlemage vendem stone
tablet por tier; sem o trade de artefato uncommon; flag ClassTradesAdded no NBT) e MODELSENTINEL
1:1 (cristal duplo do Blockbench 1.12.2 como LayerDefinition + textura entity/sentinel.png no
lugar do magic_crystal girando).
✓ AS-16 (3.49.0): PHASE_JUMP (contínua SAGE: canaliza e ao soltar teleporta min..max x blast +
extra por segundo — Banish do Redux; release pelo tracker do PlayerTick, padrão spring_charge) e
UNSEALING_SCROLL (pergaminho raro de loot — right-click dissolve muro arcano GERADO a até 5
blocos, consome 1; entra no pool dungeon_additions com peso 2; helper dissolveConnectedPermanent
extraído do sealbreaker para o TemporaryBlockEntity).
✓ AS-15 (3.48.0): LECTERNS/TOMES DO SAGE + RUNEWORDS FINAIS —
  SAGE TOME (4 tiers, WandItem do Redux — desvio igual às espadas: sem as 36 variantes elementais;
  receita do novice com magic_silk no lugar do enchanted_filament, que não existe no port) +
  enchanted_page + SAGE LECTERN estação (BE + GUI 3 slots: tomo + pages → tier seguinte, 5/10/15
  páginas; desvios: sem exigir progression — mesma simplificação do anvil; sem livro 3D animado;
  sem lectern "natural" de estrutura com spell aleatória);
  perfect_theory (canaliza 5s no lectern com mystic book VAZIO + 1 theory point → escreve
  perfect_theory_spell; desvio: o replay lê o LastExperiment do PLAYER_DATA no cast, não engarrafa
  NBT no livro; ExperimentSpell agora grava o resultado), awaken_tome (anima o tomo da mainhand em
  AnimatedItemEntity + tome_controller: click recall, hit retarget, sneak+tome_warp troca de lugar,
  cooldown 200t), tome_warp (spell gravável — habilita o swap do controller), forced_channel (ray:
  casta a spell atual do alvo até max_tier; wizard NPC = spell fixa por UUID; desvio: sem cooldown
  de 80t no item) e thoughtsteal (ray: copia a spell atual do alvo pro slot selecionado do sage
  tome). RUNEWORDS: empower (+100 de carga na lâmina; sistema de carga novo no CustomData: +20 por
  cast, +5 por golpe, golpe cheio = efeito elemental com dano — affectEntity(true) — e reseta),
  shatter (com cargas: golpe derruba a guarda de escudos — canDisableShield), sealbreaker (instant
  ray 10 blocos: dissolve até 64 arcane_wall GERADOS conectados — sem lifetime no BE = de
  estrutura — e estoura 3.5 sem quebrar blocos). 23/23 RUNEWORDS FECHADAS.
✓ AS-14 (3.47.0): WORDS_OF_UNBINDING (remove 1 nível do upgrade da wand na mainhand — upgrade
escolhido pelo item de upgrade na offhand, senão o primeiro; devolve o item; usa
CastItemDataHelper.removeUpgrade do Redux 0.1.27); SPRING_CHARGE (contínua: carrega no chão até
40t, soltar/descolar do chão lança — altura vertical_speed*potência*ticks + impulso horizontal no
olhar via tracker no PlayerTick; desvio: ring_cloudwalker fica com artefatos); CHAMAS —
arcane_flame (ray reutiliza o bloco temporário existente via TemporaryBlockEntity, lifetime
30s*potência), wildfire_flame (BE próprio: dano 2/contato + anda 1 bloco a cada 30t atrás do ser
vivo mais próximo no raio 2*blast), enchant_fireplace (fogo/campfire com ≥3 lados sólidos vira
teleportation_flame; sneak grava origem, cast normal linka DUAS chamas — desvio: link bidirecional,
o original era unidirecional; 20t dentro teleporta; texturas das 3 chamas ausentes no 1.12.2 —
fallback soul_fire, wildfire sem ícone de spell); CÂMERA CLIENT (CameraDummyEntity no ClientLevel
como render view + ASCameraClientHandler no ClientTick): farsight (contínua, zoom FOV 0.1 via
ComputeFovModifierEvent), eagle_eye (efeito → câmera fixa +50y do ponto de cast, só sob céu aberto,
recast/sneak remove), astral_projection (efeito+sixth_sense+transience → câmera livre: frente/
pulo/agachar movem 1 bloco a cada 2t), scrying_orb (WARLOCK: sneak-ray grava pos a 2 blocos da
face; cast projeta a visão 6s; recast desliga; desvio: pos do client é cópia local — relogar
exige regravar; sem as partículas FLASH/DUST do 1.12.2).

## AS-8 — worldgen (em curso)

✓ AS-8a: MINÉRIOS (devoritium y-48..16 + 7 crystal ores elementais y0..64, biome modifier
overworld); SPAWNS naturais (skeleton_mage w4, evil_class_wizard w2 só no escuro); LOOT em baús
vanilla (dungeon/mineshaft/stronghold/pyramid/jungle/mansion: relíquias + runas + nuggets, 35%);
3 ESTRUTURAS jigsaw single-piece com NBT original 1.12.2 (fallen_tower, warlock_rite,
battlemage_camp — datafix automático do template; blocos de mods não portados viram ar) + loot
tables próprias (battlemage_camp/warlock_rite/fallen_tower).
NOTA: gerar mundo NOVO para testar; /locate structure ancientspellcraft:fallen_tower.
✓ AS-8b: +5 estruturas (ancient_temple, ancient_vault x2 variantes enterradas, battlemage_keep x2,
bookvault enterrada, sage_hill) + loot tables ancient_vault/sage_camp/treasure_chest/foodstuff
(NBTs referenciam baús do ebwizardry: shrine/library_ruins_bookshelf — resolvem pelo Redux).
✓ AS-8c: variantes _chest_ nos pools (sage_hill 8+base w8, camp 1+1, keep 4 — eram variantes
INTEIRAS da estrutura, não sub-templates); MARKERS dos templates viram ENTIDADES REAIS no NBT
(33 em 21 templates: wizard/evil_wizard/warlock→class wizards com a classe da estrutura,
skeleton_mage, horse; stone_guardian e skeleton_mage_ghost→skeleton_mage com TODO); remap de
blocos antigos (bookshelf/lectern→oak_*, gilded_wood→gilded_oak_wood, runestone→chiseled TODO);
8 BLOCOS PORTADOS (sealed/unsealed_stone, sentinel_block+diamond, sage_lectern, unseal_button,
placed_rune + 7 runas colocadas); FIX: scribing_desk e arcane_anvil não tinham BlockItem;
lazy-init de class wizards/skeleton mages carregados de NBT.
✓ AS-8d: POCKET DIMENSION (ancientspellcraft:pocket, void flat, céu do End, noite fixa, sem
spawns) + ORB_SPACE (canaliza 3s com warlock orb → entra; lá dentro → volta ao ponto de partida;
plot por jogador em grade por UUID com a POCKET_LIBRARY colocada na primeira visita — desvio
documentado: sem o sistema de orbe físico 1:1).
✓ AS-8e (3.39.0): sage_flax/Lunar Flax (1 bloco com propriedade DAY em vez dos 2 blocos do 1.12.2;
recolhe de dia via randomTick; colheita = tesoura à noite via loot; worldgen rarity 4 + heightmap —
aproximação do "y aleatório 0-255" do original), log/leaves_crystal_tree (decorativos; SEM worldgen
— fiel ao 1.12.2, que registrava os blocos mas nunca gerava a árvore; log entra na tag minecraft:logs
p/ decay das folhas — efeito colateral: smelta charcoal), astral_diamond_ore (y5-15, veio 3, 3x/chunk,
dropa shard) + crystal_silver_ore (y5-20, veio 3, 4x/chunk, dropa nugget) + item astral_diamond_shard
+ recipes 9 shards <-> ebwizardry:astral_diamond; tags mineable/needs_diamond_tool criadas em
data/minecraft (blocos antigos ainda sem tags de ferramenta — TODO à parte).
✓ AS-8f (3.40.0): dimension_boundary + 8 variantes elementais (inquebráveis, textura crystal_block
do Redux) + dimension_focus/_gold (clicar dentro da pocket dim volta ao ponto de partida — desvio:
não devolve orb, port não tem orbe físico 1:1); plot do orb_space ganha casca de boundary do
elemento do orb + focus_gold de saída; VOID CREEPER (clone do creeper, spawn overworld w2 1-1,
loot magic_crystal/shard/grand — desvios: sem blacklist de mushroom biome, sem spawn egg).
✓ AS-8g (3.40.0): SEALED STONE com property unsealing 0-3 (contagia vizinhos, random tick progride,
vira unsealed_stone), UNSEAL BUTTON bloco cheio inquebrável (clique inicia o desselamento),
SENTINELS funcionais (BlockEntity casta magic_missile em players ≤5 blocos a cada 60t; vida num
proxy invisível atacável SpellCasterEntity — iron 5, diamond 20; morto → bloco some; render =
magic_crystal do Redux girando — desvios: sem variantes gold/large_iron do 1.12.2 (templates não
usam), som ambiente beacon vanilla, sem ModelSentinel próprio). TAGS de ferramenta aplicadas
(novos + antigos): pickaxe p/ ores/devoritium/sentinels/estações de pedra (crystal ores e
devoritium = needs_iron_tool, nível 2 do 1.12.2), axe p/ crystal tree/scribing desk/sage lectern.
✓ unsealing scroll (AS-16); ✓ ModelSentinel 1:1 (AS-17 — cristal duplo Blockbench + textura
sentinel.png; LargeSentinel segue fora, os templates não usam).

## Overrides descartados (não voltam)

- **conjure_pickaxe** e **plague_of_darkness_as**: eram overrides config-gated de spells do próprio
  wizardry para integrar artefatos — a integração virá pelos artefatos, sem override.
- **drain_vitality, hand_of_gaia, SpellProjectileAOEPotion**: classes mortas no 1.12.2
  (nunca registradas, sem JSON).

## Desvios deliberados do 1.12.2

- **Elementos reclassificados (3.36.0)**: as 53 spells que eram `magic` ("None") ganharam elemento
  temático (13 chaos, 18 sorcery, 8 healing, 7 earth, 4 fire, 1 ice, 2 necromancy — tabela do
  usuário em ELEMENTOS-NONE-AS.md). Elemento novo `ancientspellcraft:chaos` (LIGHT_PURPLE,
  npcSelectable=false — só classificação, sem armor/wand/cristal; wizards NPC nunca sorteiam).
  Requer Redux >= 0.1.23 (flag npcSelectable + ícone por namespace).

## Detalhes menores (TODO no código)

✓ AS-34 (3.67.0): COSMÉTICOS — SONS PRÓPRIOS COMPLETOS (83 oggs + sounds.json 1.12.2 inteiro,
348 entradas; ASSounds com goose/war_horn/relic_use_loop/relic_activate/relic_activate_2/
transmutation wired no RelicItem/TranscribingTome; demais eventos registram conforme uso);
BER DA SPHERE OF COGNIZANCE (esfera 4x4x4 do ModelSphereCognizance girando/flutuando sobre o
pedestal — desvio: rotação por gameTime, sem a aceleração na direção do jogador); TINTE DA ROBE
do skeleton mage pelo elemento (renderToBuffer com a cor do Element); textura FULLMOON do
charm_stone_tablet (property client); RemnantCage agora solta o RemnantMinion próprio (a entity
já existia no port).
FICAM (baixo valor/sem uso — decisão documentada):
- Partículas custom do 1.12.2 (DARK_MIST, RAINDROP, TIME_KNOT...) aproximadas com as do Redux.
- ModelClassWizard próprio (a armadura de classe já dá a identidade visual) e modelo do druid
  (usa evil wizard) — desvios de AS-7m mantidos.
- LargeSentinel: os templates de estrutura não usam — sem uso in-game.
- Remap runestone→chiseled nos templates (cosmético de estrutura).
- Shaders (assets/shaders) — mesmo caso dos shaders do Redux (marco 6 do port principal).
- ~~Mecânicas anti-magia do devoritium (handlers)~~ ✓ 3.68.0: IDevoritium portado (stepOn/entityInside
  nos 5 blocos, inventoryTick em ingot/nugget/block items/magnet, onTick no magnet equipado,
  hurtEnemy do magnet, construct sobre devoritium expira em 20t). Armas/armadura de devoritium do
  1.12.2 seguem não registradas no port (hitEntityDelegate fica sem outros consumidores).

## AS-35 (3.70.0) — shader do astral projection + limpeza de TODOs + attunement

- Shader do astral projection via API nova do Redux (PostEffects.register, 0.1.32): reusa o
  sixth_sense do EB — fiel à intenção do 1.12.2 (PotionAstralProjection.SHADER apontava para uma
  cópia idêntica do JSON do EB e nunca era carregado; os 4 JSONs de shader do AS 1.12.2 eram
  cópias mortas — nada mais a portar).
- Gate das class spells completo: warlock attuned não casta spell de outra classe
  (warlock_attunement_prevents_spell_cast do 1.12.2) + lang keys dot-format adicionadas
  (must_have_full_matching_set só existia no formato legacy com dois-pontos).
- TODOs stale removidos (trabalho já entregue): BER da Sphere (AS-34), câmera do astral/eagle_eye
  (AS-8/17), progression no arcane anvil (já exigia), texturas/efeitos da espada (AS-30).
- Lesser do SORCERY anotado como código morto do 1.12.2 (handler nunca existiu — decisão AS-32).
- Residuais que FICAM (baixo valor): skeleton mage elemento por bioma + radiant_spark fallback,
  armadura animada (sage tome), ritual sobre runas colocadas, textura por tier do forbidden tome,
  progression exigida no arcane anvil p/ hilt+blade (não existia no 1.12.2).
