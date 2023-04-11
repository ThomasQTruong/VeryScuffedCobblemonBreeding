package dev.thomasqtruong.veryscuffedcobblemonbreeding.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions.VeryScuffedCobblemonBreedingPermissions;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.screen.PokeBreedHandlerFactory;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PokeBreed {
  public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
      literal("pokebreed")
              .requires(src -> VeryScuffedCobblemonBreedingPermissions.checkPermission(src, VeryScuffedCobblemonBreeding.permissions.POKEBREED_PERMISSION))
              .executes(this::execute)
    );
  }

  // Keeps track of every breeding session.
  public HashMap<UUID, BreedSession> breedSessions = new HashMap<>();

  public class BreedSession {
    // Breeder information.
    public ServerPlayerEntity breeder;
    UUID breederUUID;

    // Breeding information.
    public boolean breederAccept = false;
    public Pokemon breederPokemon1;
    public Pokemon breederPokemon2;
    long timestamp;
    public boolean cancelled = false;
    public boolean changePage = false;

    // Constructor
    public BreedSession(ServerPlayerEntity breeder) {
      this.breeder = breeder;
      this.breederUUID = breeder.getUuid();
      this.timestamp = System.currentTimeMillis();
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
      
      // Get Pokemon attributes.
      String pokemon1Gender = String.valueOf(breederPokemon1.getGender());
      String pokemon2Gender = String.valueOf(breederPokemon2.getGender());
      String pokemon1Species = String.valueOf(breederPokemon1.getSpecies());
      String pokemon2Species = String.valueOf(breederPokemon2.getSpecies());

      // Cannot breed same genders (unless genderless + ditto).
      if (pokemon1Gender.equals(pokemon2Gender) && !pokemon1Gender.equals(String.valueOf("GENDERLESS"))) {
        cancel("Cannot breed same genders.");
        return;
      }
      // Both dittos.
      if (pokemon1Species.equals("ditto") && pokemon2Species.equals("ditto")) {
        cancel("Cannot breed dittos.");
        return;
      }
      // None are ditto.
      if (!(pokemon1Species.equals("ditto") || pokemon2Species.equals("ditto"))) {
        if (pokemon1Gender.equals(String.valueOf("GENDERLESS")) && pokemon2Gender.equals(String.valueOf("GENDERLESS")
            && !pokemon1Species.equals(pokemon2Species))) {
          // Both are genderless and not the same species.
          cancel("Cannot breed two differing genderless species (unless theres a ditto).");
          return;
        } else if (pokemon1Gender.equals(String.valueOf("GENDERLESS")) || pokemon2Gender.equals(String.valueOf("GENDERLESS"))) {
          // One is genderless.
          cancel("Cannot breed a genderless non-ditto species with a regular Cobblemon.");
          return;
        }
      }
      // Possibly Nidoking/Nidoqueen

      // Proceeding to breed.
      cancelled = true;

      // Pokemon exists.
      if (breederPokemon1 != null) {
        // Take away pokemon.
        // party1.remove(breederPokemon1);
      }

      // Pokemon exists.
      /*
      if (breederPokemon1 != null) {
        // Give pokemon and evolve is neccessary.
        // party2.add(breederPokemon1);
        breederPokemon1.getEvolutions().forEach(evolution -> {
          if (evolution instanceof TradeEvolution) {
            evolution.evolve(breederPokemon1);
          }
        });
      }*/

      Text toSend = Text.literal("Breed complete!").formatted(Formatting.GREEN);
      breeder.sendMessage(toSend);
      breedSessions.remove(breederUUID);
    }
  }

  private int execute(CommandContext<ServerCommandSource> ctx) {
    if (ctx.getSource().getPlayer() != null) {
      ServerPlayerEntity player = ctx.getSource().getPlayer();

      BreedSession breedSession = new BreedSession(player);
      breedSession.start();
    }
    return 1;
  }
}
