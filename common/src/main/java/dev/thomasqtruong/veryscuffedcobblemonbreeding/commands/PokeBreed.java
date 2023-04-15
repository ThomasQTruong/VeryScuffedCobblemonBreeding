package dev.thomasqtruong.veryscuffedcobblemonbreeding.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityPool;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions.VeryScuffedCobblemonBreedingPermissions;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.screen.PokeBreedHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.literal;

public class PokeBreed {
  // Schedules when players are out of cooldown.
  public static ScheduledThreadPoolExecutor scheduler;
  // Keeps track of every breeding session.
  public HashMap<UUID, BreedSession> breedSessions = new HashMap<>();

  public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    // Set up command.
    dispatcher.register(
            literal("pokebreed")
                    .requires(src -> VeryScuffedCobblemonBreedingPermissions.checkPermission(src, VeryScuffedCobblemonBreeding.permissions.POKEBREED_PERMISSION))
                    .executes(this::execute)
    );
    // Set up scheduler.
    scheduler = new ScheduledThreadPoolExecutor(1, r -> {
      Thread thread = Executors.defaultThreadFactory().newThread(r);
      thread.setName("PokeBreed Thread");
      return thread;
    });
    scheduler.setRemoveOnCancelPolicy(true);
    scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
  }

  // PokeBreed command executed.
  private int execute(CommandContext<ServerCommandSource> ctx) {
    if (ctx.getSource().getPlayer() != null) {
      ServerPlayerEntity player = ctx.getSource().getPlayer();

      // Breed session already exists for player.
      if (breedSessions.containsKey(player.getUuid())) {
        BreedSession breedSession = breedSessions.get(player.getUuid());

        // Bred never happened in the first place.
        if (breedSession.timeBred == 0) {
          breedSession.cancel("Possibly a dupelicate somehow.");
          breedSessions.remove(player.getUuid());
        } else {
          // Pokemon was bred before; check if it never got off cooldown.
          long timeSince = System.currentTimeMillis() - breedSession.timeBred;

          // Cooldown was supposed to be over!
          if (timeSince > 1000L * 60 * VeryScuffedCobblemonBreedingConfig.COOLDOWN_IN_MINUTES) {
            breedSessions.remove(player.getUuid());
          }
        }
      }

      // Checking if user is under cooldown still.
      if (breedSessions.containsKey(player.getUuid())) {
        // Get time since in seconds.
        BreedSession breedSession = breedSessions.get(player.getUuid());
        long cooldownDuration = (System.currentTimeMillis() - breedSession.timeBred) / 1000;
        // Total cooldown time - time since = time left.
        cooldownDuration = (VeryScuffedCobblemonBreedingConfig.COOLDOWN_IN_MINUTES * 60L) - cooldownDuration;

        Text toSend = Text.literal("Breed cooldown: " + cooldownDuration + " seconds.").formatted(Formatting.RED);
        player.sendMessage(toSend);

        return -1;
      }

      // Create and start breeding session.
      BreedSession breedSession = new BreedSession(player);
      breedSessions.put(player.getUuid(), breedSession);
      breedSession.start();
    }
    return 1;
  }

  public class BreedSession {
    // Breeder information.
    public ServerPlayerEntity breeder;
    // Breeding information.
    public boolean breederAccept = false;
    public Pokemon breederPokemon1;
    public Pokemon breederPokemon2;
    public long timeBred;
    public boolean cancelled = false;
    public boolean changePage = false;
    public boolean dittoOrSelfBreeding = false;
    UUID breederUUID;

    // Constructor
    public BreedSession(ServerPlayerEntity breeder) {
      this.breeder = breeder;
      this.breederUUID = breeder.getUuid();
      this.timeBred = 0;
    }

    public void cancel(String msg) {
      breeder.sendMessage(Text.literal("Breed cancelled: " + msg).formatted(Formatting.RED));
      breedSessions.remove(breederUUID);
      this.cancelled = true;
    }

    public void start() {
      // Give player GUI.
      PokeBreedHandlerFactory breedHandler = new PokeBreedHandlerFactory(this);
      breeder.openHandledScreen(breedHandler);
    }

    public void doBreed() {
      // Breed cancelled, why are we still doing the breed?
      if (this.cancelled) {
        System.out.println("Something funky is goin' on");
        cancel("Something funky is goin' on.");
        return;
      }
      // Only provided 1 or 0 Pokemon to breed or pokemons don't exist.
      if (breederPokemon1 == null || breederPokemon2 == null) {
        cancel("Not enough Cobblemons provided.");
        return;
      }

      // Failed the breeding conditions.
      if (!checkBreed()) {
        return;
      }

      // Proceeding to breed.
      cancelled = true;

      // Pokemons exist.
      if (breederPokemon1 != null && breederPokemon2 != null) {
        // Get the bred Pokemon.
        Pokemon baby = getPokemonBred();

        // Add Pokemon to party.
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(breeder);
        party.add(baby);

        // Send success message and set cooldown.
        Text toSend = Text.literal("Breed complete!").formatted(Formatting.GREEN);
        breeder.sendMessage(toSend);
        scheduler.schedule(() -> {
          breedSessions.remove(breederUUID);
        }, VeryScuffedCobblemonBreedingConfig.COOLDOWN_IN_MINUTES, TimeUnit.MINUTES);
        timeBred = System.currentTimeMillis();
      } else {
        cancel("One of the Cobblemons does not exist!");
      }
    }


    public boolean checkBreed() {
      // Get Pokemon attributes.
      String pokemon1Gender = String.valueOf(breederPokemon1.getGender());
      String pokemon2Gender = String.valueOf(breederPokemon2.getGender());
      String pokemon1Species = String.valueOf(breederPokemon1.getSpecies());
      String pokemon2Species = String.valueOf(breederPokemon2.getSpecies());
      HashSet<EggGroup> pokemon1EggGroup = breederPokemon1.getSpecies().getEggGroups();
      HashSet<EggGroup> pokemon2EggGroup = breederPokemon2.getSpecies().getEggGroups();

      // Cannot breed same genders (unless genderless + ditto).
      if (pokemon1Gender.equals(pokemon2Gender) && !pokemon1Gender.equals("GENDERLESS")) {
        cancel("Cannot breed same genders.");
        return false;
      }
      // Both dittos.
      if (pokemon1Species.equals("ditto") && pokemon2Species.equals("ditto")) {
        cancel("Cannot breed dittos.");
        return false;
      }

      // Get matching egg groups.
      HashSet<EggGroup> matchingEggGroups = new HashSet<>();
      for (EggGroup g : pokemon1EggGroup) {
        if (pokemon2EggGroup.contains(g)) {
          matchingEggGroups.add(g);
        }
      }

      // None are ditto.
      if (!(pokemon1Species.equals("ditto") || pokemon2Species.equals("ditto"))) {
        if (pokemon1Gender.equals("GENDERLESS") && pokemon2Gender.equals("GENDERLESS")
                && !pokemon1Species.equals(pokemon2Species)) {
          // Both are genderless and not the same species.
          cancel("Cannot breed two differing genderless species (unless theres a ditto).");
          return false;
        } else if (pokemon1Gender.equals("GENDERLESS") || pokemon2Gender.equals("GENDERLESS")) {
          // One is genderless.
          cancel("Cannot breed a genderless non-ditto species with a regular Cobblemon.");
          return false;
        } else if (pokemon1EggGroup.contains(EggGroup.UNDISCOVERED) || pokemon2EggGroup.contains(EggGroup.UNDISCOVERED)) {
          // In undiscovered egg group.
          cancel("Cannot breed with Undiscovered egg group.");
          return false;
        } else if (matchingEggGroups.size() == 0) {
          // Not the same egg group.
          cancel("Cannot breed two Cobblemons from different egg groups.");
          return false;
        }
      }

      // Both Pokemons look breedable, check if one is ditto or self.
      if ((pokemon1Species.equals("ditto") || pokemon2Species.equals("ditto"))
              || pokemon1Species.equals(pokemon2Species)) {
        dittoOrSelfBreeding = true;
      }
      return true;
    }


    public Pokemon getPokemonBred() {
      Pokemon baby;
      // Ditto/self breeding = itself.
      if (dittoOrSelfBreeding) {
        if (!String.valueOf(breederPokemon1.getSpecies()).equals("ditto")) {
          // Pokemon 1 is not ditto.
          baby = breederPokemon1.clone(true, true);
          while (baby.getPreEvolution() != null) {
            Species preEvolution = baby.getPreEvolution().getSpecies();
            baby.setSpecies(preEvolution);
          }
        } else {
          // Pokemon 2 is not ditto.
          baby = breederPokemon2.clone(true, true);
          while (baby.getPreEvolution() != null) {
            Species preEvolution = baby.getPreEvolution().getSpecies();
            baby.setSpecies(preEvolution);
          }
        }
      } else {
        // TESTING
        baby = breederPokemon1.clone(true, true);
      }


      // Got the Pokemon, time to set its proper default.
      baby.setEvs(CobblemonStatProvider.INSTANCE.createEmptyEVs());
      baby.setExperienceAndUpdateLevel(0);
      baby.removeHeldItem();
      baby.initializeMoveset(true);
      baby.heal();


      Random RNG = new Random();
      // Generate friendship (base% - 30%).
      int intRNG = RNG.nextInt() % 77 + baby.getForm().getBaseFriendship();
      baby.setFriendship(intRNG, true);


      // Set gender and abilities.
      int maleRatio = (int) (baby.getForm().getMaleRatio() * 100);
      intRNG = RNG.nextInt(101);
      if (maleRatio < 0) {
        // No male ratio (genderless).
        baby.setGender(Gender.GENDERLESS);
      } else if (intRNG <= maleRatio) {
        // In male ratio (male).
        baby.setGender(Gender.MALE);
      } else {
        // Is female.
        baby.setGender(Gender.FEMALE);
      }


      // Priority.LOWEST = common ability, Priority.LOW = hidden ability.
      // Remove all hidden abilities.
      AbilityPool possibleAbilities = baby.getForm().getAbilities();
      // Defaulting to common ability.
      intRNG = 100;

      // Get lists of all the possible hidden/common abilities.
      List<AbilityTemplate> possibleHiddens = new ArrayList<>();
      List<AbilityTemplate> possibleCommons = new ArrayList<>();

      for (PotentialAbility potentialAbility : possibleAbilities) {
        // Is a hidden ability.
        if (potentialAbility.getPriority() == Priority.LOW) {
          possibleHiddens.add(potentialAbility.getTemplate());
        } else if (potentialAbility.getPriority() == Priority.LOWEST) {
        // Is a common ability.
          possibleCommons.add(potentialAbility.getTemplate());
        }
      }

      // Pokemon has hidden ability, offspring has a 60% chance of getting it too.
      if (possibleHiddens.contains(baby.getAbility().getTemplate())) {
        intRNG = RNG.nextInt(100);  // 0-99
      }

      // Hit hidden ability.
      if (intRNG < 60) {  // 0-59 (60%)
        // Add every hidden ability to possibleDraws, draw random hidden if exists.
        if (possibleHiddens.size() > 0) {
          intRNG = RNG.nextInt(possibleHiddens.size());
          Ability ability = new Ability(possibleHiddens.get(intRNG), false);
          baby.setAbility(ability);
        }
      } else {
        // Did not hit hidden ability, draw random common if exists.
        if (possibleCommons.size() > 0) {
          intRNG = RNG.nextInt(possibleCommons.size());
          Ability ability = new Ability(possibleCommons.get(intRNG), false);
          baby.setAbility(ability);
        }
      }


      // No Everstone, RNG nature.
      // if (baby.heldItem() != ) {
      baby.setNature(Natures.INSTANCE.getRandomNature());
      //}


      // Get IVs.

      return baby;
    }
  }
}
