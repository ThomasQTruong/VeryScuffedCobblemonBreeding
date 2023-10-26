# Orphan Scuffed Cobblemon Breeding (Mod)
- Unofficial version of Scuffed Breeding.
- This was requested.
  - It deletes the parents after breeding.
    - Held items will be given back.

# Description
My attempt to create a scuffed Cobblemon "breeding" for my friends and I to use.
- This is my first ever mod! :)
- It is an instant breeding system with a 5-minute default cooldown.
- Single player cooldown has a slight issue.
    - If you rejoin, you are instantly out of cooldown.
        - Cheat at your own will.
- Tries to mimic the actual breeding system.
    - Can breed using ditto, self, or same egg groups.
        - Offspring will be the same as the mother.
        - Offspring is the base evolution.
        - Moves and EXP are reset.
        - Shinies are obtainable (same rates as your Cobblemon config).
    - Also has the breeding restrictions.
        - No same genders.
        - No differing egg groups.
        - No Undiscovered egg group.
        - No double ditto.
        - Ditto breeding can be disabled.
    - 3 IVs randomly get inherited from either parent.
        - Unless breeding items are used (i.e. *power belt* and *destiny knot*).
    - EVs get reset.
    - Friendship gets randomized.
    - Gender gets randomized based on the Cobblemon's gender ratio.
    - Ability gets randomized.
        - Hidden abilities are supported.
          - 60% chance to pass down if either parents have a hidden ability.
          - Can be disabled in config.

## Official Version Downloads
- [Github](https://github.com/ThomasQTruong/VeryScuffedCobblemonBreeding/releases)
- [Modrinth](https://modrinth.com/mod/veryscuffedcobblemonbreeding)
- [CursedForge](https://curseforge.com/minecraft/mc-mods/veryscuffedcobblemonbreeding/)

## Dependencies
- Architectury: [Modrinth](https://modrinth.com/mod/architectury-api) | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/architectury-api)

## How to use:
1. Open up the breeding GUI.
    - Command: /pokebreed
    - ![image](https://user-images.githubusercontent.com/58405482/232265114-48c663b1-8966-4f62-8911-6519d7d2cc9e.png)
    - No perms mod/plugin?
         - Check out the configs section under.
2. Select two Cobblemons to breed.
    - ![image](https://user-images.githubusercontent.com/58405482/232265199-6c2311e6-e348-41be-a984-3d6a79b6dc5d.png)
    - Next/Previous box is at the bottom right.
        - ![image](https://user-images.githubusercontent.com/58405482/232265149-941782aa-e863-4c98-91ba-5c1616c3f6b6.png)
3. Confirm breed.
    - Confirmation button is between the Next/Previous buttons.
    - ![image](https://user-images.githubusercontent.com/58405482/232265217-2b3493e5-272d-43d8-b7b3-49dd284f98da.png)
4. Trying to breed again when under cooldown will let you know the cooldown duration.
    - ![image](https://user-images.githubusercontent.com/58405482/232265354-a8c21114-5a5d-4343-8be5-f7a41ed43727.png)

## Getting breeding items [LEGACY]:
- Not needed anymore since v1.4 added breeding items to the game.
- Only obtainable via commands.
    - Mainly dedicated for servers to sell these special items.
- Can change the item to whatever you want.
    - The only thing that matters is the **breedItem** NBT attribute.
- Use commands to get breeding items:
    - > /give **[Player]** minecraft:light_gray_dye{display:{Name:'{"text":"Everstone","italic":"false"}'}, breedItem:"Everstone"}
    - > /give **[Player]** minecraft:red_dye{display:{Name:'{"text":"Destiny Knot","italic":"false"}'}, breedItem:"Destiny Knot"}
    - > /give **[Player]** minecraft:light_blue_dye{display:{Name:'{"text":"Power Anklet","italic":"false"}'}, breedItem:"Power Anklet"}
    - > /give **[Player]** minecraft:yellow_dye{display:{Name:'{"text":"Power Band","italic":"false"}'}, breedItem:"Power Band"}
    - > /give **[Player]** minecraft:orange_dye{display:{Name:'{"text":"Power Belt","italic":"false"}'}, breedItem:"Power Belt"}
    - > /give **[Player]** minecraft:pink_dye{display:{Name:'{"text":"Power Bracer","italic":"false"}'}, breedItem:"Power Bracer"}
    - > /give **[Player]** minecraft:purple_dye{display:{Name:'{"text":"Power Lens","italic":"false"}'}, breedItem:"Power Lens"}
    - > /give **[Player]** minecraft:lime_dye{display:{Name:'{"text":"Power Weight","italic":"false"}'}, breedItem:"Power Weight"}

## Configs
### Permissions
- command.pokebreed - default is level 2.
  - 0 = anyone can use it.
- command.vippokebreed - permission level to get VIP cooldown.
### Cooldowns
- command.pokebreed.cooldown - default is 5 minutes.
- command.pokebreed.vipcooldown - default is 3 minutes.
### Other Features
- ditto.breeding - default is 1.
  - 1 = ditto breeding ENABLED.
  - 0 = ditto breeding DISABLED.
- hidden.ability - default is 1.
  - Only matters if either parent has a hidden ability.
  - 1 = hidden ability can be passed.
  - 0 = hidden ability cannot be passed.

## Credits
This would not have been possible without these open source works:
- Side Mod Template: [CobblemonExtras](https://github.com/Xwaffle1/CobblemonExtras) by [Xwaffle1](https://github.com/Xwaffle1/)
- Command Cooldown: [PokeGift](https://github.com/Polymeta/Pokegift/) by [Polymeta](https://github.com/Polymeta)

## Addons used in images:
Datapacks
- [Cardboard Cutout Mon](https://modrinth.com/resourcepack/cardboard-cutout-mon) by [EikoBiko](https://modrinth.com/user/EikoBiko)
    - Adds cardboard cutout textures for Cobblemons that do not have a model yet.
- [Questionably Lore Accurate Pokemon Spawns](https://modrinth.com/datapack/questionably-lore-accurate-pokemon-spawns) by [FrankTheFarmer2](https://modrinth.com/user/FrankTheFarmer2)
    - Adds spawn files for some Cobblemons that do not have a model yet.
