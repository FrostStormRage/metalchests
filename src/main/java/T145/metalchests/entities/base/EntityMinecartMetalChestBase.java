package T145.metalchests.entities.base;

import javax.annotation.Nullable;

import T145.metalchests.MetalChests;
import T145.metalchests.api.IInventoryHandler;
import T145.metalchests.blocks.BlockMetalChest;
import T145.metalchests.containers.ContainerMetalChest;
import T145.metalchests.core.ModLoader;
import T145.metalchests.entities.EntityMinecartCopperChest;
import T145.metalchests.entities.EntityMinecartCrystalChest;
import T145.metalchests.entities.EntityMinecartDiamondChest;
import T145.metalchests.entities.EntityMinecartGoldChest;
import T145.metalchests.entities.EntityMinecartIronChest;
import T145.metalchests.entities.EntityMinecartObsidianChest;
import T145.metalchests.entities.EntityMinecartSilverChest;
import T145.metalchests.items.ItemChestStructureUpgrade;
import T145.metalchests.lib.MetalChestType;
import T145.metalchests.lib.MetalChestType.StructureUpgrade;
import T145.metalchests.tiles.TileMetalChest;
import mods.railcraft.api.carts.IItemCart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

@Optional.Interface(modid = "railcraft", iface = "mods.railcraft.api.carts.IItemCart", striprefs = true)
public abstract class EntityMinecartMetalChestBase extends EntityMinecart implements IInventoryHandler, IInteractionObject, IItemCart {

	private static final DataParameter<NBTTagCompound> TILE_INVENTORY = EntityDataManager.<NBTTagCompound>createKey(EntityMinecartMetalChestBase.class, DataSerializers.COMPOUND_TAG);
	private final TileMetalChest chest = new TileMetalChest(getChestType());
	private final ItemStackHandler inventory = new ItemStackHandler(getChestType().getInventorySize());

	public EntityMinecartMetalChestBase(World world) {
		super(world);
	}

	public EntityMinecartMetalChestBase(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public static EntityMinecartMetalChestBase create(World world, double x, double y, double z, MetalChestType type) {
		switch (type) {
		case COPPER:
			return new EntityMinecartCopperChest(world, x, y, z);
		case CRYSTAL:
			return new EntityMinecartCrystalChest(world, x, y, z);
		case DIAMOND:
			return new EntityMinecartDiamondChest(world, x, y, z);
		case GOLD:
			return new EntityMinecartGoldChest(world, x, y, z);
		case OBSIDIAN:
			return new EntityMinecartObsidianChest(world, x, y, z);
		case SILVER:
			return new EntityMinecartSilverChest(world, x, y, z);
		default:
			return new EntityMinecartIronChest(world, x, y, z);
		}
	}

	public abstract MetalChestType getChestType();

	public TileMetalChest getChest() {
		return chest;
	}

	public void setTopStacks(ItemStackHandler topStacks) {
		this.dataManager.set(TILE_INVENTORY, topStacks.serializeNBT());
	}

	public ItemStackHandler getTopStacks() {
		NBTTagCompound tag = this.dataManager.get(TILE_INVENTORY);
		chest.getTopStacks().deserializeNBT(tag);
		return chest.getTopStacks();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TILE_INVENTORY, new NBTTagCompound());
	}

	@Override
	public ItemStackHandler getInventory() {
		return inventory;
	}

	public void setInventory(IItemHandler stacks) {
		for (int slot = 0; slot < stacks.getSlots(); ++slot) {
			if (slot < getChestType().getInventorySize()) {
				inventory.setStackInSlot(slot, stacks.getStackInSlot(slot));
			}
		}
	}

	@Override
	public ItemStack getCartItem() {
		return new ItemStack(ModLoader.MINECART_METAL_CHEST, 1, getChestType().ordinal());
	}

	@Override
	public void killMinecart(DamageSource source) {
		super.killMinecart(source);

		if (world.getGameRules().getBoolean("doEntityDrops")) {
			for (int i = 0; i < inventory.getSlots(); ++i) {
				ItemStack stack = inventory.getStackInSlot(i);

				if (!stack.isEmpty()) {
					InventoryHelper.spawnItemStack(world, posX, posY, posZ, stack);
				}
			}

			entityDropItem(new ItemStack(ModLoader.METAL_CHEST, 1, getChestType().ordinal()), 0.0F);
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (super.processInitialInteract(player, hand) || world.isRemote) {
			return true;
		}

		if (player.isSneaking()) {
			ItemStack stack = player.getHeldItem(hand);

			if (stack.getItem() instanceof ItemChestStructureUpgrade) {
				ItemChestStructureUpgrade upgradeItem = (ItemChestStructureUpgrade) stack.getItem();
				StructureUpgrade upgrade = StructureUpgrade.byMetadata(stack.getItemDamage());

				if (getChestType() == upgrade.getBase()) {
					EntityMinecartMetalChestBase newCart = create(world, posX, posY, posZ, upgrade.getUpgrade());

					onSlotChanged();
					newCart.setInventory(inventory);
					newCart.onSlotChanged();
					setDead();
					world.spawnEntity(newCart);

					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
					}
					player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.4F, 0.8F);
				}
			}
		} else {
			player.openGui(MetalChests.MODID, hashCode(), world, 0, 0, 0);
		}

		return true;
	}

	@Override
	public IBlockState getDefaultDisplayTile() {
		return ModLoader.METAL_CHEST.getDefaultState().withProperty(BlockMetalChest.VARIANT, getChestType());
	}

	@Override
	public EntityMinecart.Type getType() {
		return EntityMinecart.Type.CHEST;
	}

	@Override
	public int getDefaultDisplayTileOffset() {
		return 8;
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		if (isDead) {
			return false;
		} else {
			return player.getDistanceSq(this) <= 64.0D;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setTag("Inventory", inventory.serializeNBT());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		inventory.deserializeNBT(tag.getCompoundTag("Inventory"));
	}

	@Override
	protected void applyDrag() {
		float f = 0.98F;

		int i = 15 - ItemHandlerHelper.calcRedstoneFromInventory(inventory);
		f += (float) i * 0.001F;

		motionX *= f;
		motionY *= 0.0D;
		motionZ *= f;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public ContainerMetalChest createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
		return new ContainerMetalChest(this, player, getChestType());
	}

	@Override
	public String getGuiID() {
		return "metalchests:" + getChestType().getName() + "_cart";
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public void onSlotChanged() {
		chest.setInventory(inventory);
		chest.sortTopStacks();
		setTopStacks(chest.getTopStacks());
	}

	@Optional.Method(modid = "railcraft")
	@Override
	public boolean canPassItemRequests() {
		return true;
	}

	@Optional.Method(modid = "railcraft")
	@Override
	public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
		return !ItemHandlerHelper.insertItemStacked(inventory, stack, true).isEmpty();
	}

	@Optional.Method(modid = "railcraft")
	@Override
	public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
		ItemStack result = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack extractedStack = inventory.extractItem(i, stack.getCount(), true);

			if (ItemStack.areItemStacksEqual(extractedStack, stack)) {
				result = extractedStack;
			}
		}

		return !result.isEmpty();
	}
}