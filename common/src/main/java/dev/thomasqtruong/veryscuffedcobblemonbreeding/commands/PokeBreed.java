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

    // Constructor
    public BreedSession(ServerPlayerEntity breeder) {
      this.breeder = breeder;
      this.breederUUID = breeder.getUuid();
      this.timestamp = System.currentTimeMillis();
    }

    public void cancel() {
      breeder.sendMessage(Text.literal("Breed cancelled.").formatted(Formatting.RED));
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
        cancel();
        return;
      }
      // Proceeding to breed.
      cancelled = true;

      // Pokemon exists.
      if (breederPokemon1 != null) {
        // Take away pokemon.
        // party1.remove(breederPokemon1);
      }

      // Pokemon exists.
      if (breederPokemon1 != null) {
        // Give pokemon and evolve is neccessary.
        // party2.add(breederPokemon1);
        breederPokemon1.getEvolutions().forEach(evolution -> {
          if (evolution instanceof TradeEvolution) {
            evolution.evolve(breederPokemon1);
          }
        });
      }

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
