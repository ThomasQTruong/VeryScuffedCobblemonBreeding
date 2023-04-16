# Very Scuffed Cobblemon Breeding
My attempt to create a scuffed Cobblemon "breeding" for my friends and I to use.
- This is my first ever mod! :)
- It is an instant breeding system with a 5 minute default cooldown.
- Can only breed Cobblemons from your PC.
- Single player cooldown has a slight issue.
  - If you rejoin, you are instantly out of cooldown.
    - Cheat at your own will.
- Tries to mimic the actual breeding system.
  - Can breed using ditto, self, or same egg groups.
    - Offspring will be the same as the mother.
    - Offspring is the base evolution.
    - Moves and EXP are reset.
    - 1/8192 chance to get a shiny.
  - Also has the breeding restrictions.
    - No same genders.
    - No differing egg groups.
    - No Undiscovered egg group.
    - No double ditto.
  - 3 IVs randomly get inherited from either parent.
    - Unless breeding items come out.
  - EVs get reset.
  - Nature gets RNG'd since no Everstone in the game currently.
  - Friendship gets randomized.
  - Gender gets randomized based on the Cobblemon's gender ratio.
  - Ability gets randomized.
    - Hidden abilities are supported.
    - 60% chance to pass down if either parents have a hidden ability.

## Download
[Latest](https://github.com/ThomasQTruong/VeryScuffedCobblemonBreeding/releases/tag/1.0.0)

## How to use:
1. Open up the breeding GUI.
    - Command: /pokebreed
    - ![image](https://user-images.githubusercontent.com/58405482/232265114-48c663b1-8966-4f62-8911-6519d7d2cc9e.png)
2. Select two Cobblemons to breed.
    - ![image](https://user-images.githubusercontent.com/58405482/232265199-6c2311e6-e348-41be-a984-3d6a79b6dc5d.png)
    - Next/Previous box is at the bottom right.
      - ![image](https://user-images.githubusercontent.com/58405482/232265149-941782aa-e863-4c98-91ba-5c1616c3f6b6.png)
3. Confirm breed.
    - Confirmation button is between the Next/Previous buttons.
    - ![image](https://user-images.githubusercontent.com/58405482/232265217-2b3493e5-272d-43d8-b7b3-49dd284f98da.png)
4. Trying to breed again when under cooldown will let you know the cooldown duration.
    - ![image](https://user-images.githubusercontent.com/58405482/232265354-a8c21114-5a5d-4343-8be5-f7a41ed43727.png)

## Configs
### Permissions
- command.pokebreed - default is permission level 2.
### Cooldowns
- command.pokebreed.cooldown - default is 5 minutes.

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
