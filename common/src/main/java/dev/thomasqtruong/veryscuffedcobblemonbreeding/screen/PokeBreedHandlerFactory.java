package dev.thomasqtruong.veryscuffedcobblemonbreeding.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.util.ItemBuilder;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.PokeBreed;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.util.PokemonUtility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class PokeBreedHandlerFactory implements NamedScreenHandlerFactory {
  private PokeBreed.BreedSession breedSession;
  private int boxNumber = 0;

  // Default constructor.
  public PokeBreedHandlerFactory(PokeBreed.BreedSession breedSession) {
    this.breedSession = breedSession;
  }

  // Constructor for next/previous page.
  public PokeBreedHandlerFactory(PokeBreed.BreedSession breedSession, int boxNumber) {
    this.breedSession = breedSession;
    // Negative box number, loop back to the positive.
    if (boxNumber < 0) {
      boxNumber = VeryScuffedCobblemonBreedingConfig.MAX_PC_BOX_COUNT - 1;
    }
    // Keep within the max boxes range.
    this.boxNumber = boxNumber % VeryScuffedCobblemonBreedingConfig.MAX_PC_BOX_COUNT;
  }

  // Get GUI name.
  @Override
  public Text getDisplayName() {
    return Text.of("Breed: PC Box " + (boxNumber + 1));
  }

  // Get sizes.
  int rows() {
    return 5;
  }

  int size() {
    return rows() * 9;
  }

  // Create GUI.
  @Nullable
  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
    // Make GUI of size() size.
    SimpleInventory inventory = new SimpleInventory(size());
    ItemStack emptyPokemon = new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE);

    // Set up the GUI.
    for (int i = 0; i < size(); ++i) {
      inventory.setStack(i, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
    }
    // Breeding choices.
    inventory.setStack(6, emptyPokemon.setCustomName(Text.of("To Breed #1")));
    if (breedSession.breederPokemon1 != null) {
      inventory.setStack(6, PokemonUtility.pokemonToItem(breedSession.breederPokemon1));
    }
    inventory.setStack(7, new ItemStack(Items.PINK_STAINED_GLASS_PANE).setCustomName(Text.of(" ")));
    inventory.setStack(8, emptyPokemon.setCustomName(Text.of("To Breed #2")));
    if (breedSession.breederPokemon2 != null) {
      inventory.setStack(8, PokemonUtility.pokemonToItem(breedSession.breederPokemon2));
    }
    // Buttons
    inventory.setStack(size() - 1, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(Text.literal("Next Box")).build());
    inventory.setStack(size() - 2, new ItemStack(Items.GRAY_DYE).setCustomName(Text.literal("Click to Breed")));
    inventory.setStack(size() - 3, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(Text.literal("Previous Box")).build());

    // Grab player's PC data.
    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
    PCStore breederStorage = null;
    try {
      breederStorage = Cobblemon.INSTANCE.getStorage().getPC(serverPlayer.getUuid());
    } catch (NoPokemonStoreException e) {
      return null;
    }
    PCBox box = breederStorage.getBoxes().get(boxNumber);

    // Set up PC in GUI (for every pokemon in box [box size = 6x5]).
    for (int i = 0; i < 30; i++) {
      Pokemon pokemon = box.get(i);
      double row = Math.floor((double) i / 6.0D);
      int index = i % 6;

      if (pokemon != null) {
        ItemStack item = PokemonUtility.pokemonToItem(pokemon);
        NbtCompound slotNbt = item.getOrCreateSubNbt("slot");
        slotNbt.putInt("slot", i);
        item.setSubNbt("slot", slotNbt);
        inventory.setStack((int) (row * 9) + index, item);
      } else {
        inventory.setStack((int) (row * 9) + index, new ItemStack(Items.RED_STAINED_GLASS_PANE).setCustomName(Text.literal("Empty").formatted(Formatting.GRAY)));
      }
    }

    GenericContainerScreenHandler container = new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, inv, inventory, rows()) {
      @Override
      public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // If player cancels.
        if (breedSession.cancelled) {
          player.sendMessage(Text.literal("Breeding has been cancelled.").formatted(Formatting.RED));
          player.closeHandledScreen();
        }
        // Get clicked index.
        double row = Math.floor((double) slotIndex / 9.0D);
        int index = slotIndex % 9;

        // First/second pokemon already selected; keep up to date.
        if (breedSession.breederPokemon1 != null) {
          ItemStack pokemonItem = PokemonUtility.pokemonToItem(breedSession.breederPokemon1);
          setStackInSlot(6, nextRevision(), pokemonItem);
        }
        if (breedSession.breederPokemon2 != null) {
          ItemStack pokemonItem = PokemonUtility.pokemonToItem(breedSession.breederPokemon2);
          setStackInSlot(8, nextRevision(), pokemonItem);
        }

        // Clicked on a breeding Pokemon, remove from breed.
        if (slotIndex == 6) {
          breedSession.breederPokemon1 = null;
          inventory.setStack(6, emptyPokemon.setCustomName(Text.of("To Breed #1")));
        }
        if (slotIndex == 8) {
          breedSession.breederPokemon2 = null;
          inventory.setStack(8, emptyPokemon.setCustomName(Text.of("To Breed #2")));
        }

        // Clicked next page.
        if (slotIndex == size() - 1) {
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openHandledScreen(new PokeBreedHandlerFactory(breedSession, boxNumber + 1));
          // Back to default value.
          breedSession.changePage = false;
        }
        // Clicked accept.
        if (slotIndex == size() - 2) {
          breedSession.breederAccept = true;
        }
        // Clicked previous page.
        if (slotIndex == size() - 3) {
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openHandledScreen(new PokeBreedHandlerFactory(breedSession, boxNumber - 1));
          // Back to default value.
          breedSession.changePage = false;
        }

        // Ignore when clicking a slot outside of the GUI.
        if (slotIndex > size()) {
          return;
        }

        // Get item that was clicked.
        ItemStack stack = getInventory().getStack(slotIndex);
        // If item is a slot.
        if (stack != null && stack.hasNbt() && stack.getSubNbt("slot") != null) {
          // Get pokemon at slot.
          int slot = stack.getSubNbt("slot").getInt("slot");
          Pokemon pokemon = box.get(slot);

          // Pokemon exists.
          if (pokemon != null) {
            if (breedSession.breederPokemon1 == null) {
              // Selected pokemon is already in 2nd slot.
              if (breedSession.breederPokemon2 == pokemon) {
                return;
              }
              // First Pokemon not selected yet, select on first slot.
              breedSession.breederPokemon1 = pokemon;
              ItemStack pokemonItem = PokemonUtility.pokemonToItem(pokemon);
              setStackInSlot(6, nextRevision(), pokemonItem);
            } else {
              // First Pokemon already selected, select on second slot if not dupelicate.
              if (breedSession.breederPokemon1 == pokemon) {
                return;
              }
              breedSession.breederPokemon2 = pokemon;
              ItemStack pokemonItem = PokemonUtility.pokemonToItem(pokemon);
              setStackInSlot(8, nextRevision(), pokemonItem);
            }
          }
        }
        // Accepted breeding.
        if (breedSession.breederAccept) {
          breedSession.doBreed();
          breedSession.breeder.closeHandledScreen();
        }
      }

      @Override
      public ItemStack transferSlot(PlayerEntity player, int index) {
        return null;
      }

      @Override
      public boolean canInsertIntoSlot(Slot slot) {
        return false;
      }

      @Override
      protected void dropInventory(PlayerEntity player, Inventory inventory) {
      }

      @Override
      public void close(PlayerEntity player) {
        // GUI closed AND it wasn't to change page (player closed).
        if (!breedSession.cancelled && !breedSession.changePage) {
          // Cancel session.
          breedSession.cancel("GUI closed.");
          breedSession.breeder.closeHandledScreen();
        }
      }
    };

    return container;
  }
}
