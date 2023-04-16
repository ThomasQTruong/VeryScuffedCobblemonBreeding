# Very Scuffed Cobblemon Breeding
My attempt to create a scuffed Cobblemon "breeding" for my friends and I to use.
- It is an instant breeding system with a 5 minute default cooldown.
- Can only breed Cobblemons from your PC.
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
2. Select two Cobblemons to breed.
    - Next/Previous box is at the bottom right.
3. Confirm breed.
    - Confirmation button is between the Next/Previous buttons.

## Configs
### Permissions
- command.pokebreed - default is permission level 2.
### Cooldowns
- command.pokebreed.cooldown - default is 5 minutes.
