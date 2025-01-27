package dev.thomasqtruong.veryscuffedcobblemonbreeding.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.util.ItemBuilder;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.PokeBreed;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.util.PokemonUtility;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.SimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


/**
 * Handles the PokeBreed GUI.
 */
public class PokeBreedHandlerFactory implements MenuProvider {
  private SimpleContainer inventory = new SimpleContainer(size());
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
  public @NotNull Component getDisplayName() {
    return Component.literal("Breed: PC Box " + (boxNumber + 1));
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
   */
  public void updateInventory() {
    ItemBuilder emptyPokemon = new ItemBuilder(Items.LIGHT_BLUE_STAINED_GLASS_PANE);
    ItemBuilder emptyNode = new ItemBuilder(Items.GRAY_STAINED_GLASS_PANE).setCustomName(Component.literal(" "));

    // For index 15-17, set as blank.
    for (int i = 15; i <= 17; ++i) {
      // Set as gray glass.
      inventory.setItem(i, emptyNode.getStack());
    }

    // Breeding choices.
    inventory.setItem(6, emptyPokemon.setCustomName(Component.literal("To Breed #1")).build());
    if (breedSession.breederPokemon1 != null) {
      inventory.setItem(6, PokemonUtility.INSTANCE.pokemonToItem(breedSession.breederPokemon1));
    }
    inventory.setItem(7, new ItemBuilder(Items.PINK_STAINED_GLASS_PANE).setCustomName(
            Component.literal(" ")).build());
    inventory.setItem(8, emptyPokemon.setCustomName(Component.literal("To Breed #2")).build());
    if (breedSession.breederPokemon2 != null) {
      inventory.setItem(8, PokemonUtility.INSTANCE.pokemonToItem(breedSession.breederPokemon2));
    }

    // Buttons
    inventory.setItem(size() - 1, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(
            Component.literal("Next Box")).build());
    inventory.setItem(size() - 2, new ItemBuilder(Items.GRAY_DYE).setCustomName(
            Component.literal("Click to Breed")).build());
    inventory.setItem(size() - 3, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(
            Component.literal("Previous Box")).build());

    // Settings
    int pageIndex = 0;
    for (int i = 24; i <= 42; i += 9) {
      for (int j = 0; j < 3; ++j) {
        // Is current setting, make green pane.
        if (pageSettings[pageIndex] == breedSession.pageChangeSetting) {
          inventory.setItem(i + j, new ItemBuilder(Items.LIME_STAINED_GLASS_PANE).setCustomName(
                  Component.literal("Change box by " + String.valueOf(pageSettings[pageIndex]))).build());
        } else {
          inventory.setItem(i + j, new ItemBuilder(Items.WHITE_STAINED_GLASS_PANE).setCustomName(
                  Component.literal("Change box by " + String.valueOf(pageSettings[pageIndex]))).build());
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
  public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
    ServerPlayer serverPlayer = (ServerPlayer) player;

    updateInventory();

    // Grab player's Party and PC data.
    PlayerPartyStore breederParty = null;
    PCStore breederStorage = null;
    try {
      breederParty = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer);
      breederStorage = Cobblemon.INSTANCE.getStorage().getPC(serverPlayer);
    } catch (Exception e) {
      return null;
    }

    // Set up Party in GUI.
    for (int i = 0; i < breederParty.size(); ++i) {
      // Get pokemon.
      Pokemon pokemon = breederParty.get(i);

      // Pokemon exists.
      if (pokemon != null) {
        // Turn Pokemon into item.
        ItemStack item = PokemonUtility.INSTANCE.pokemonToItem(pokemon);
        CompoundTag slotTag = new CompoundTag();
        slotTag.putInt("slot", i);
        item.set(DataComponents.CUSTOM_DATA, CustomData.of(slotTag));
        inventory.setItem(i, item);
      } else {
      // Doesn't exist.
        // Put a red stained-glass instead.
        inventory.setItem(i, new ItemBuilder(Items.RED_STAINED_GLASS_PANE).setCustomName(
                Component.literal("Empty").withStyle(ChatFormatting.GRAY)).build());
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
        ItemStack item = PokemonUtility.INSTANCE.pokemonToItem(pokemon);
        CompoundTag slotTag = new CompoundTag();
        slotTag.putInt("slot", i);
        item.set(DataComponents.CUSTOM_DATA, CustomData.of(slotTag));
        inventory.setItem((int) (row * 9) + index, item);
      } else {
        inventory.setItem((int) (row * 9) + index, new ItemBuilder(Items.RED_STAINED_GLASS_PANE)
                .setCustomName(Component.literal("Empty")
                        .withStyle(ChatFormatting.GRAY)).build());
      }
    }
    PlayerPartyStore finalBreederParty = breederParty;

    // Returns the GUI.
    return new ChestMenu(MenuType.GENERIC_9x6, syncId, inv, inventory, rows()) {
      /**
       * When a slot is clicked in the GUI.
       *
       * @param slotIndex - the index of the clicked slot.
       * @param button - the button clicked (?).
       * @param actionType - the type of action (?).
       * @param player - the player that clicked.
       */
      @Override
      public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        // If player cancels.
        if (breedSession.cancelled) {
          ServerPlayer serverPlayer = (ServerPlayer) player;
          serverPlayer.sendSystemMessage(Component.literal("Breeding has been cancelled.")
                  .withStyle(ChatFormatting.RED));
          serverPlayer.closeContainer();
        }

        // Player clicked accept.
        if (slotIndex == size() - 2) {
          breedSession.breederAccept = true;
          breedSession.doBreed();
          breedSession.breeder.closeContainer();
        }
        // Ignore when clicking a slot outside the GUI.
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
          player.openMenu(new PokeBreedHandlerFactory(breedSession, boxNumber + breedSession.pageChangeSetting));
          // Back to default value.
          breedSession.changePage = false;
        } else if (slotIndex == size() - 3) {
          // Clicked previous page.
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openMenu(new PokeBreedHandlerFactory(breedSession, boxNumber - breedSession.pageChangeSetting));
          // Back to default value.
          breedSession.changePage = false;
        } else {
          // Get item that was clicked.
          ItemStack stack = inventory.getItem(slotIndex);
          // If item is a slot.
          if (stack != null && stack.has(DataComponents.CUSTOM_DATA)
                                          && Objects.nonNull(stack.get(DataComponents.CUSTOM_DATA).contains("slot"))) {
            // Extract information.
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = Objects.requireNonNull(customData).copyTag();

            // Get pokemon at slot.
            int slot = tag.getInt("slot");
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
                // First Pokemon already selected, select on second slot if not duplicate.
                if (breedSession.breederPokemon1 == pokemon) {
                  return;
                }
                breedSession.breederPokemon2 = pokemon;
              }
            }
          }
        }

        updateInventory();
      }


      /**
       * Disable transferring between slots.
       *
       * @param player - the player that clicked the slot.
       * @param index - the clicked slot's index.
       * @return ItemStack - an empty item to prevent transfer.
       */
      @Override
      public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
      }


      /**
       * If the breed GUI is still valid.
       *
       * @param player - the player using the GUI.
       */
      @Override
      public boolean stillValid(Player player) {
        return true;
      }


      /**
       * Action: when player closes PokeBreed GUI, cancel breeding.
       *
       * @param player - the player that was trying to breed Cobblemons.
       */
      @Override
      public void removed(Player player) {
        // GUI closed AND it wasn't to change page (player closed).
        if (!breedSession.cancelled && !breedSession.changePage) {
          // Cancel session.
          breedSession.cancel("GUI closed.");
          breedSession.breeder.closeContainer();
        }
        super.removed(player);
      }
    };
  }
}
