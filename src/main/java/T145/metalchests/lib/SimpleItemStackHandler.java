package T145.metalchests.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import T145.metalchests.tiles.base.TileBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class SimpleItemStackHandler extends ItemStackHandler {

	private final boolean allowWrite;
	private final TileBase te;

	public SimpleItemStackHandler(int invSize, @Nullable TileBase te, boolean allowWrite) {
		super(invSize);
		this.allowWrite = allowWrite;
		this.te = te;
	}

	public SimpleItemStackHandler(NonNullList<ItemStack> topStacks, @Nullable TileBase te, boolean allowWrite) {
		super(topStacks);
		this.allowWrite = allowWrite;
		this.te = te;
	}

	public SimpleItemStackHandler(int invSize, boolean allowWrite) {
		this(invSize, null, allowWrite);
	}

	public SimpleItemStackHandler(NonNullList<ItemStack> topStacks, boolean allowWrite) {
		this(topStacks, null, allowWrite);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (allowWrite) {
			return super.insertItem(slot, stack, simulate);
		} else {
			return stack;
		}
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (allowWrite) {
			return super.extractItem(slot, amount, simulate);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void onContentsChanged(int slot) {
		if (te != null) {
			te.markDirty();
		}
	}

	public NonNullList<ItemStack> getStacks() {
		return stacks;
	}

	public int size() {
		return stacks.size();
	}

	public void dropItems(World world, BlockPos pos) {
		for (int i = 0; i < size(); ++i) {
			ItemStack stack = getStackInSlot(i);

			if (!stack.isEmpty()) {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
		}
	}

	public int calcRedstone() {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < size(); ++j) {
			ItemStack itemstack = getStackInSlot(j);

			if (!itemstack.isEmpty()) {
				f += itemstack.getCount() / Math.min(getSlotLimit(j), itemstack.getMaxStackSize());
				++i;
			}
		}

		f = f / size();
		return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
	}
}