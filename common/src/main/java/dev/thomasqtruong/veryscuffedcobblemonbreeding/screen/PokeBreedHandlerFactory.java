package dev.thomasqtruong.veryscuffedcobblemonbreeding.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
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

/**
 * Handles the PokeBreed GUI.
 */
public class PokeBreedHandlerFactory implements NamedScreenHandlerFactory {
  private PokeBreed.BreedSession breedSession;
  private int boxNumber = 0;
  final private int[] pageSettings = {1, 5, 10,
          15, 20, 25,
          50, 100, 200};


  /**
   * Default constructor; copies over breedSession.
   *
   * @param breedSession - the breed session to copy.
   */
  public PokeBreedHandlerFactory(PokeBreed.BreedSession breedSession) {
    this.breedSession = breedSession;
  }


  /**
   * Constructor for next/previous page.
   *
   * @param breedSession - the breed session to copy over.
   * @param boxNumber - the current box number to display.
   */
  public PokeBreedHandlerFactory(PokeBreed.BreedSession breedSession, int boxNumber) {
    this.breedSession = breedSession;
    // Negative, figure out the actual page number.
    if (boxNumber < 0) {
      boxNumber *= -1;                                 // Turn to positive.
      boxNumber %= breedSession.maxPCSize;             // Mod by max.
      boxNumber = breedSession.maxPCSize - boxNumber;  // Max - modded = current.
    }
    // Positive, keep within the max boxes range.
    this.boxNumber = boxNumber % breedSession.maxPCSize;
  }

  /**
   * Get display name for the GUI.
   */
  @Override
  public Text getDisplayName() {
    return Text.of("Breed: PC Box " + (boxNumber + 1));
  }


  /**
   * Gets the number of rows in the GUI.
   *
   * @return int - the number of rows.
   */
  public int rows() {
    return 6;
  }


  /**
   * Gets the number of slots in the GUI.
   *
   * @return int - the number of slots.
   */
  public int size() {
    return rows() * 9;
  }


  /**
   * Updates the user's GUI inventory.
   *
   * @param inventory - the PokeBreed GUI.
   */
  public void updateInventory(SimpleInventory inventory) {
    ItemStack emptyPokemon = new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE);

    // For index 15-17, set as blank.
    for (int i = 15; i <= 17; ++i) {
      // Set as gray glass.
      inventory.setStack(i, new ItemStack(Items.GRAY_STAINED_GLASS_PANE)
              .setCustomName(Text.of(" ")));
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
    inventory.setStack(size() - 1, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(
            Text.literal("Next Box")).build());
    inventory.setStack(size() - 2, new ItemStack(Items.GRAY_DYE).setCustomName(
            Text.literal("Click to Breed")));
    inventory.setStack(size() - 3, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(
            Text.literal("Previous Box")).build());

    // Settings
    int pageIndex = 0;
    for (int i = 24; i <= 42; i += 9) {
      for (int j = 0; j < 3; ++j) {
        // Is current setting, make green pane.
        if (pageSettings[pageIndex] == breedSession.pageChangeSetting) {
          inventory.setStack(i + j, new ItemStack(Items.LIME_STAINED_GLASS_PANE)
                  .setCustomName(Text.literal("Change box by " + String.valueOf(pageSettings[pageIndex]))));
        } else {
          inventory.setStack(i + j, new ItemStack(Items.WHITE_STAINED_GLASS_PANE)
                  .setCustomName(Text.literal("Change box by " + String.valueOf(pageSettings[pageIndex]))));
        }
        ++pageIndex;
      }
    }
  }


  /**
   * Create PokeBreed GUI.
   *
   * @param syncId - the ID used to sync (?).
   * @param inv - the player's inventory.
   * @param player - the player themselves.
   * @return ScreenHandler - the created PokeBreed GUI.
   */
  @Nullable
  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
    // Make GUI of size() size.
    SimpleInventory inventory = new SimpleInventory(size());
    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

    updateInventory(inventory);

    // Grab player's Party and PC data.
    PlayerPartyStore breederParty = null;
    PCStore breederStorage = null;
    try {
      breederParty = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer.getUuid());
      breederStorage = Cobblemon.INSTANCE.getStorage().getPC(serverPlayer.getUuid());
    } catch (NoPokemonStoreException e) {
      return null;
    }

    // Set up Party in GUI.
    for (int i = 0; i < breederParty.size(); ++i) {
      // Get pokemon.
      Pokemon pokemon = breederParty.get(i);

      // Pokemon exists.
      if (pokemon != null) {
        // Turn Pokemon into item.
        ItemStack item = PokemonUtility.pokemonToItem(pokemon);
        NbtCompound slotNbt = item.getOrCreateSubNbt("slot");
        slotNbt.putInt("slot", i);
        item.setSubNbt("slot", slotNbt);
        inventory.setStack(i, item);
      } else {
      // Doesn't exist.
        // Put a red stained glass instead.
        inventory.setStack(i, new ItemStack(Items.RED_STAINED_GLASS_PANE).setCustomName(
                Text.literal("Empty").formatted(Formatting.GRAY)));
      }
    }

    PCBox box = breederStorage.getBoxes().get(boxNumber);
    breedSession.maxPCSize = breederStorage.getBoxes().size();
    // Set up PC in GUI (for every Pokemon in box [box size = 6x5]).
    for (int i = 0; i < 30; i++) {
      Pokemon pokemon = box.get(i);
      double row = 1 + Math.floor((double) i / 6.0D);
      int index = i % 6;

      if (pokemon != null) {
        ItemStack item = PokemonUtility.pokemonToItem(pokemon);
        NbtCompound slotNbt = item.getOrCreateSubNbt("slot");
        slotNbt.putInt("slot", i);
        item.setSubNbt("slot", slotNbt);
        inventory.setStack((int) (row * 9) + index, item);
      } else {
        inventory.setStack((int) (row * 9) + index, new ItemStack(Items.RED_STAINED_GLASS_PANE)
                .setCustomName(Text.literal("Empty")
                        .formatted(Formatting.GRAY)));
      }
    }
    PlayerPartyStore finalBreederParty = breederParty;

    // Returns the GUI.
    return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6,
            syncId, inv, inventory, rows()) {

      /**
       * When a slot is clicked in the GUI.
       *
       * @param slotIndex - the index of the clicked slot.
       * @param button - the button clicked (?).
       * @param actionType - the type of action (?).
       */
      @Override
      public void onSlotClick(int slotIndex, int button, SlotActionType actionType,
                              PlayerEntity player) {
        // If player cancels.
        if (breedSession.cancelled) {
          player.sendMessage(Text.literal("Breeding has been cancelled.")
                  .formatted(Formatting.RED));
          player.closeHandledScreen();
        }

        // Clicked accept.
        if (slotIndex == size() - 2) {
          breedSession.breederAccept = true;
          breedSession.doBreed();
          breedSession.breeder.closeHandledScreen();
        }
        // Ignore when clicking a slot outside of the GUI.
        if (slotIndex > size()) {
          return;
        }

        // Clicked on a breeding Pokemon, remove from breed.
        if (slotIndex == 6) {
          breedSession.breederPokemon1 = null;
        } else if (slotIndex == 8) {
          breedSession.breederPokemon2 = null;
        } else if ((slotIndex >= 24 && slotIndex <= 26) ||
                (slotIndex >= 33 && slotIndex <= 35) ||
                (slotIndex >= 42 && slotIndex <= 44)) {
          // Clicked a page change setting.
          breedSession.pageChangeSetting = pageSettings[(slotIndex % 9 - 6) + (slotIndex / 9 - 2) * 3];
        } else if (slotIndex == size() - 1) {
          // Clicked next page.
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openHandledScreen(new PokeBreedHandlerFactory(breedSession,
                  boxNumber + breedSession.pageChangeSetting));
          // Back to default value.
          breedSession.changePage = false;
        } else if (slotIndex == size() - 3) {
          // Clicked previous page.
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openHandledScreen(new PokeBreedHandlerFactory(breedSession,
                  boxNumber - breedSession.pageChangeSetting));
          // Back to default value.
          breedSession.changePage = false;
        } else {
          // Get item that was clicked.
          ItemStack stack = getInventory().getStack(slotIndex);
          // If item is a slot.
          if (stack != null && stack.hasNbt() && stack.getSubNbt("slot") != null) {
            // Get pokemon at slot.
            int slot = stack.getSubNbt("slot").getInt("slot");
            Pokemon pokemon = null;
            if (slotIndex >= 0 && slotIndex <= 5) {
              pokemon = finalBreederParty.get(slot);
            } else {
              pokemon = box.get(slot);
            }

            // Pokemon exists.
            if (pokemon != null) {
              if (breedSession.breederPokemon1 == null) {
                // Selected Pokemon is already in 2nd slot.
                if (breedSession.breederPokemon2 == pokemon) {
                  return;
                }
                // First Pokemon not selected yet, select on first slot.
                breedSession.breederPokemon1 = pokemon;
              } else {
                // First Pokemon already selected, select on second slot if not dupelicate.
                if (breedSession.breederPokemon1 == pokemon) {
                  return;
                }
                breedSession.breederPokemon2 = pokemon;
              }
            }
          }
        }

        updateInventory(inventory);
      }


      /**
       * Disable transferring between slots.
       *
       * @param player - the player that clicked the slot.
       * @param index - the clicked slot's index.
       * @return ItemStack - the item at the.
       */
      @Override
      public ItemStack quickMove(PlayerEntity player, int index) {
        return null;
      }


      /**
       * Disable insertion in the slots (return false always).
       *
       * @param slot - the slot that was inserted to.
       * @return boolean - whether it can be inserted into.
       */
      @Override
      public boolean canInsertIntoSlot(Slot slot) {
        return false;
      }


      /**
       * Disable dropping items from inventory.
       *
       * @param player - the player that tried to drop.
       * @param inventory - the PokeBreed GUI.
       */
      @Override
      protected void dropInventory(PlayerEntity player, Inventory inventory) {
      }


      /**
       * Action: when player closes PokeBreed GUI, cancel breeding.
       *
       * @param player - the player that was trying to breed Cobblemons.
       */
      @Override
      public void onClosed(PlayerEntity player) {
        // GUI closed AND it wasn't to change page (player closed).
        if (!breedSession.cancelled && !breedSession.changePage) {
          // Cancel session.
          breedSession.cancel("GUI closed.");
          breedSession.breeder.closeHandledScreen();
        }
      }
    };
  }
}
